package client.dto;

public class ActivityLog {
    private Long id;
    private Long userId;
    private String action;
    private String details;
    private String timestamp;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getTimestamp() { return timestamp; }
}
