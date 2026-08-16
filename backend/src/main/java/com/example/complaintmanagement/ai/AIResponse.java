package com.example.complaintmanagement.ai;

public class AIResponse {
    private String category;
    private String priority;
    private String department;
    private String summary;
    private String suggestedResponse;

    // Constructors
    public AIResponse() {}

    public AIResponse(String category, String priority, String department, String summary, String suggestedResponse) {
        this.category = category;
        this.priority = priority;
        this.department = department;
        this.summary = summary;
        this.suggestedResponse = suggestedResponse;
    }

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getSuggestedResponse() { return suggestedResponse; }
    public void setSuggestedResponse(String suggestedResponse) { this.suggestedResponse = suggestedResponse; }

    // Builder
    public static AIResponseBuilder builder() {
        return new AIResponseBuilder();
    }

    public static class AIResponseBuilder {
        private String category;
        private String priority;
        private String department;
        private String summary;
        private String suggestedResponse;

        public AIResponseBuilder category(String category) { this.category = category; return this; }
        public AIResponseBuilder priority(String priority) { this.priority = priority; return this; }
        public AIResponseBuilder department(String department) { this.department = department; return this; }
        public AIResponseBuilder summary(String summary) { this.summary = summary; return this; }
        public AIResponseBuilder suggestedResponse(String suggestedResponse) { this.suggestedResponse = suggestedResponse; return this; }

        public AIResponse build() {
            return new AIResponse(category, priority, department, summary, suggestedResponse);
        }
    }
}
