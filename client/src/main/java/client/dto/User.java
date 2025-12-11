package client.dto;

public class User {
    private Long id;
    private String name;
    private String email;

    // Можна без пароля — пароль не повертається з бекенду.
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
