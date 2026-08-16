package com.example.complaintmanagement;

import com.example.complaintmanagement.ai.AIComplaintAnalyzerService;
import com.example.complaintmanagement.ai.ComplaintAIService;
import com.example.complaintmanagement.ai.AIResponse;
import com.example.complaintmanagement.dto.*;
import com.example.complaintmanagement.entity.*;
import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Role;
import com.example.complaintmanagement.enums.Status;
import com.example.complaintmanagement.exception.BadRequestException;
import com.example.complaintmanagement.exception.DuplicateResourceException;
import com.example.complaintmanagement.repository.*;
import com.example.complaintmanagement.service.NotificationService;
import com.example.complaintmanagement.service.impl.AuthServiceImpl;
import com.example.complaintmanagement.service.impl.ComplaintServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ComplaintManagementApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ComplaintHistoryRepository historyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ComplaintAIService aiService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private User mockUser;
    private User mockTechnician;
    private User mockAdmin;
    private Complaint mockComplaint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User(1L, "John Doe", "user@example.com", "hashed_password", "+123", Role.USER, LocalDateTime.now());
        mockTechnician = new User(2L, "Alice Tech", "tech@example.com", "hashed_password", "+456", Role.TECHNICIAN, LocalDateTime.now());
        mockAdmin = new User(3L, "System Admin", "admin@example.com", "hashed_password", "+789", Role.ADMIN, LocalDateTime.now());

        mockComplaint = new Complaint(1L, "Water Leakage", "Water pipe burst in restroom", Category.PLUMBING, Priority.HIGH, Status.OPEN,
                "Water leak", "Fix pipe", mockUser, null, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    // --- Authentication Tests ---
    @Test
    void testRegisterUser_Success() {
        RegisterRequest req = new RegisterRequest("New User", "new@example.com", "password", "123", Role.USER);
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded_pass");
        
        User savedUser = new User(4L, req.getName(), req.getEmail(), "encoded_pass", req.getPhone(), req.getRole(), LocalDateTime.now());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("new@example.com", res.getEmail());
        assertEquals("New User", res.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        RegisterRequest req = new RegisterRequest("New User", "existing@example.com", "password", "123", Role.USER);
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- AI & Complaint Creation Tests ---
    @Test
    void testCreateComplaint_Success() {
        CreateComplaintRequest request = new CreateComplaintRequest("Internet Connection Failure", "The wifi router in Hall C is offline");
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        AIResponse mockAiResponse = new AIResponse("NETWORK", "HIGH", "IT Support", "Router offline", "Inspect router");
        when(aiService.analyzeComplaint(request.getTitle(), request.getDescription())).thenReturn(mockAiResponse);

        Complaint saved = new Complaint(2L, request.getTitle(), request.getDescription(), Category.NETWORK, Priority.HIGH, Status.OPEN,
                "Router offline", "Inspect router", mockUser, null, LocalDateTime.now(), LocalDateTime.now(), null);
        when(complaintRepository.save(any(Complaint.class))).thenReturn(saved);

        ComplaintResponse response = complaintService.createComplaint(request, mockUser.getEmail());

        assertNotNull(response);
        assertEquals(Category.NETWORK, response.getCategory());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(Status.OPEN, response.getStatus());
        verify(complaintRepository, times(1)).save(any(Complaint.class));
    }

    @Test
    void testAI_DeterministicFallback() {
        AIComplaintAnalyzerService realAiService = new AIComplaintAnalyzerService();
        // Trigger fallback by passing empty credentials or null key
        AIResponse fallbackNetwork = realAiService.analyzeComplaint("Wi-Fi keeps dropping - Outage", "We have no internet access and it has stopped since morning.");
        assertEquals("NETWORK", fallbackNetwork.getCategory());
        assertEquals("HIGH", fallbackNetwork.getPriority());
        assertEquals("IT Support", fallbackNetwork.getDepartment());

        AIResponse fallbackPlumbing = realAiService.analyzeComplaint("Restroom leak", "Water is dripping from the bathroom pipe onto the floor.");
        assertEquals("PLUMBING", fallbackPlumbing.getCategory());
        assertEquals("Plumbing Department", fallbackPlumbing.getDepartment());

        AIResponse fallbackCritical = realAiService.analyzeComplaint("Power Outage Short Circuit", "There is smoke coming from the electrical panel, potential shock hazard!");
        assertEquals("CRITICAL", fallbackCritical.getPriority());
        assertEquals("ELECTRICITY", fallbackCritical.getCategory());
    }

    // --- DSA Priority Queue Tests ---
    @Test
    void testPriorityQueueSort() {
        Complaint c1 = new Complaint(1L, "Low electricity issue", "Bulb dim", Category.ELECTRICITY, Priority.LOW, Status.OPEN, "", "", mockUser, null, LocalDateTime.now(), LocalDateTime.now(), null);
        Complaint c2 = new Complaint(2L, "Critical power shock", "Shock hazard", Category.ELECTRICITY, Priority.CRITICAL, Status.OPEN, "", "", mockUser, null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now(), null);
        Complaint c3 = new Complaint(3L, "High plumbing leak", "Leak", Category.PLUMBING, Priority.HIGH, Status.OPEN, "", "", mockUser, null, LocalDateTime.now().plusSeconds(2), LocalDateTime.now(), null);

        when(complaintRepository.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        List<ComplaintResponse> sortedList = complaintService.getPrioritizedComplaints();

        assertEquals(3, sortedList.size());
        assertEquals(Priority.CRITICAL, sortedList.get(0).getPriority()); // Critical first
        assertEquals(Priority.HIGH, sortedList.get(1).getPriority());     // High second
        assertEquals(Priority.LOW, sortedList.get(2).getPriority());      // Low last
    }

    // --- Status Transition Control Tests ---
    @Test
    void testUpdateStatus_UserClosesResolvedComplaint_Success() {
        mockComplaint.setStatus(Status.RESOLVED);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(mockComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComplaintResponse res = complaintService.updateComplaintStatus(1L, Status.CLOSED, mockUser.getEmail());
        assertEquals(Status.CLOSED, res.getStatus());
    }

    @Test
    void testUpdateStatus_UserClosesOpenComplaint_ThrowsException() {
        mockComplaint.setStatus(Status.OPEN);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(mockComplaint));

        assertThrows(BadRequestException.class, () ->
                complaintService.updateComplaintStatus(1L, Status.CLOSED, mockUser.getEmail())
        );
    }

    @Test
    void testUpdateStatus_TechnicianResolvesAssignedComplaint_Success() {
        mockComplaint.setStatus(Status.IN_PROGRESS);
        mockComplaint.setAssignedTo(mockTechnician);
        when(userRepository.findByEmail(mockTechnician.getEmail())).thenReturn(Optional.of(mockTechnician));
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(mockComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComplaintResponse res = complaintService.updateComplaintStatus(1L, Status.RESOLVED, mockTechnician.getEmail());
        assertEquals(Status.RESOLVED, res.getStatus());
    }

    @Test
    void testUpdateStatus_TechnicianResolvesUnassignedComplaint_ThrowsException() {
        mockComplaint.setStatus(Status.IN_PROGRESS);
        mockComplaint.setAssignedTo(null); // Unassigned
        when(userRepository.findByEmail(mockTechnician.getEmail())).thenReturn(Optional.of(mockTechnician));
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(mockComplaint));

        assertThrows(AccessDeniedException.class, () ->
                complaintService.updateComplaintStatus(1L, Status.RESOLVED, mockTechnician.getEmail())
        );
    }

    @Test
    void testAssignComplaint_Success() {
        mockComplaint.setStatus(Status.OPEN);
        when(userRepository.findByEmail(mockAdmin.getEmail())).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findById(mockTechnician.getId())).thenReturn(Optional.of(mockTechnician));
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(mockComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComplaintResponse res = complaintService.assignComplaint(1L, mockTechnician.getId(), mockAdmin.getEmail());

        assertEquals(Status.ASSIGNED, res.getStatus());
        assertEquals(mockTechnician.getName(), res.getAssignedTo().getName());
        verify(notificationService, times(2)).createNotification(any(User.class), any(Complaint.class), anyString());
    }
}
