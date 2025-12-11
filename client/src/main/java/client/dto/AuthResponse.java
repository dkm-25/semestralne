package client.dto;

public class AuthResponse {

    private long userId;
    private String name;
    private String email;

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
