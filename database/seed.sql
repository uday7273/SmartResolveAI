USE complaint_management_db;

-- 1. Insert default departments
INSERT INTO departments (name, description) VALUES
('IT Support', 'Handles network, internet, router, computer, and software issues.'),
('Electrical Department', 'Handles power outages, wiring, switches, lighting, and electrical appliances.'),
('Plumbing Department', 'Handles water leaks, pipe bursts, taps, toilets, and drainage problems.'),
('Housekeeping & Cleaning', 'Handles cleanliness, trash removal, and sanitization requests.'),
('General Maintenance', 'Handles furniture, doors, windows, keys, and structural repairs.'),
('Security Department', 'Handles surveillance, access control, gates, and safety compliance.');

-- 2. Insert demo users (password is 'password' hashed with BCrypt)
-- Hash: $2a$10$8.UnVuG9HHgffUDAlk8GPuRyy.iSB14Kq5m3JD4.dPX5XR347K4tO
INSERT INTO users (name, email, password, phone, role) VALUES
('System Administrator', 'admin@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8GPuRyy.iSB14Kq5m3JD4.dPX5XR347K4tO', '+1234567890', 'ADMIN'),
('Alice Technician', 'technician@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8GPuRyy.iSB14Kq5m3JD4.dPX5XR347K4tO', '+1234567891', 'TECHNICIAN'),
('John Doe', 'user@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8GPuRyy.iSB14Kq5m3JD4.dPX5XR347K4tO', '+1234567892', 'USER'),
('Bob Technician', 'bob@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8GPuRyy.iSB14Kq5m3JD4.dPX5XR347K4tO', '+1234567893', 'TECHNICIAN');

-- 3. Insert a sample complaint for testing
INSERT INTO complaints (title, description, category, priority, status, ai_summary, ai_suggested_response, created_by, assigned_to) VALUES
('Hostel Wi-Fi down in Room 302', 'The Wi-Fi router seems to be blinking red and there is no internet connection since this morning.', 'NETWORK', 'HIGH', 'ASSIGNED', 'Wi-Fi outage in Room 302', 'Restart router and verify DHCP leases.', 3, 2);

-- 4. Insert sample comments
INSERT INTO complaint_comments (complaint_id, user_id, comment) VALUES
(1, 3, 'Please resolve this quickly, I have exams coming up!'),
(1, 2, 'I will check the router in the hallway shortly.');

-- 5. Insert sample history
INSERT INTO complaint_history (complaint_id, old_status, new_status, changed_by) VALUES
(1, 'OPEN', 'ASSIGNED', 1);

-- 6. Insert sample notifications
INSERT INTO notifications (user_id, complaint_id, message, is_read) VALUES
(3, 1, 'Your complaint has been assigned to Alice Technician.', FALSE);
