package com.example.complaintmanagement.dto;

import java.util.Map;

public class DashboardStatisticsResponse {
    private long totalComplaints;
    private long pendingComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long criticalComplaints;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByPriority;
    private double averageResolutionTimeInHours;

    // Constructors
    public DashboardStatisticsResponse() {}

    public DashboardStatisticsResponse(long totalComplaints, long pendingComplaints, long inProgressComplaints,
                                       long resolvedComplaints, long criticalComplaints,
                                       Map<String, Long> complaintsByCategory, Map<String, Long> complaintsByPriority,
                                       double averageResolutionTimeInHours) {
        this.totalComplaints = totalComplaints;
        this.pendingComplaints = pendingComplaints;
        this.inProgressComplaints = inProgressComplaints;
        this.resolvedComplaints = resolvedComplaints;
        this.criticalComplaints = criticalComplaints;
        this.complaintsByCategory = complaintsByCategory;
        this.complaintsByPriority = complaintsByPriority;
        this.averageResolutionTimeInHours = averageResolutionTimeInHours;
    }

    // Getters and Setters
    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getPendingComplaints() { return pendingComplaints; }
    public void setPendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; }

    public long getInProgressComplaints() { return inProgressComplaints; }
    public void setInProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

    public long getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public long getCriticalComplaints() { return criticalComplaints; }
    public void setCriticalComplaints(long criticalComplaints) { this.criticalComplaints = criticalComplaints; }

    public Map<String, Long> getComplaintsByCategory() { return complaintsByCategory; }
    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; }

    public Map<String, Long> getComplaintsByPriority() { return complaintsByPriority; }
    public void setComplaintsByPriority(Map<String, Long> complaintsByPriority) { this.complaintsByPriority = complaintsByPriority; }

    public double getAverageResolutionTimeInHours() { return averageResolutionTimeInHours; }
    public void setAverageResolutionTimeInHours(double averageResolutionTimeInHours) { this.averageResolutionTimeInHours = averageResolutionTimeInHours; }

    // Builder
    public static DashboardStatisticsResponseBuilder builder() {
        return new DashboardStatisticsResponseBuilder();
    }

    public static class DashboardStatisticsResponseBuilder {
        private long totalComplaints;
        private long pendingComplaints;
        private long inProgressComplaints;
        private long resolvedComplaints;
        private long criticalComplaints;
        private Map<String, Long> complaintsByCategory;
        private Map<String, Long> complaintsByPriority;
        private double averageResolutionTimeInHours;

        public DashboardStatisticsResponseBuilder totalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; return this; }
        public DashboardStatisticsResponseBuilder pendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; return this; }
        public DashboardStatisticsResponseBuilder inProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; return this; }
        public DashboardStatisticsResponseBuilder resolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; return this; }
        public DashboardStatisticsResponseBuilder criticalComplaints(long criticalComplaints) { this.criticalComplaints = criticalComplaints; return this; }
        public DashboardStatisticsResponseBuilder complaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; return this; }
        public DashboardStatisticsResponseBuilder complaintsByPriority(Map<String, Long> complaintsByPriority) { this.complaintsByPriority = complaintsByPriority; return this; }
        public DashboardStatisticsResponseBuilder averageResolutionTimeInHours(double averageResolutionTimeInHours) { this.averageResolutionTimeInHours = averageResolutionTimeInHours; return this; }

        public DashboardStatisticsResponse build() {
            return new DashboardStatisticsResponse(totalComplaints, pendingComplaints, inProgressComplaints, resolvedComplaints, criticalComplaints, complaintsByCategory, complaintsByPriority, averageResolutionTimeInHours);
        }
    }
}
