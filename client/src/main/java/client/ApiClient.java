package client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import client.dto.User;

import client.dto.LoginRequest;
import client.dto.AuthResponse;
import client.dto.Group;
import client.dto.Task;
import client.dto.ActivityLog;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final RestTemplate rest = new RestTemplate();

    private static long userId = -1;
    private static String userName = "";

    public static long getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static ActivityLog[] getActivityLog(long userId) {
        String url = BASE_URL + "/logs/user/" + userId;

        RestTemplate rest = new RestTemplate();
        ResponseEntity<ActivityLog[]> resp =
                rest.getForEntity(url, ActivityLog[].class);

        return resp.getBody();
    }

    public static User getUser(long id) {
        String url = BASE_URL + "/users/" + id;
        return rest.getForObject(url, User.class);

    }

    public static void updateUser(long id, String name, String email, String password) {

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("email", email);
        if (password != null) {
            json.put("password", password);
        }

        String url = BASE_URL + "/users/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

        rest.exchange(url, HttpMethod.PUT, entity, String.class); // ← Ось тут зміна
    }






    // ---------- LOGIN ----------
    public static boolean login(String email, String password) {
        try {
            String url = BASE_URL + "/auth/login";

            LoginRequest req = new LoginRequest(email, password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginRequest> entity = new HttpEntity<>(req, headers);

            ResponseEntity<AuthResponse> resp =
                    rest.exchange(url, HttpMethod.POST, entity, AuthResponse.class);

            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                userId = resp.getBody().getUserId();
                userName = resp.getBody().getName();
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("LOGIN ERROR");
            e.printStackTrace();
            return false;
        }
    }

    // ---------- REGISTER ----------
    public static boolean register(String name, String email, String password) {
        try {
            String url = BASE_URL + "/auth/register";

            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("email", email);
            body.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> req = new HttpEntity<>(body, headers);

            rest.exchange(url, HttpMethod.POST, req, Void.class);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- GET GROUPS ----------
    public static Group[] getGroups(long userId) {
        try {
            String url = BASE_URL + "/groups/list?userId=" + userId;

            ResponseEntity<Group[]> resp =
                    rest.getForEntity(url, Group[].class);

            return resp.getBody() != null ? resp.getBody() : new Group[0];

        } catch (Exception e) {
            e.printStackTrace();
            return new Group[0];
        }
    }

    // ---------- CREATE GROUP ----------
    public static void createGroup(long userId, String name, String description) {
        try {
            String url = BASE_URL + "/groups?userId=" + userId;

            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> req = new HttpEntity<>(body, headers);

            rest.exchange(url, HttpMethod.POST, req, Void.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- ADD USER TO GROUP ----------
    public static boolean addUserToGroup(long groupId, long userId) {
        try {
            String url = BASE_URL + "/groups/" + groupId + "/join?userId=" + userId;

            rest.exchange(url, HttpMethod.POST, null, Void.class);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- GET TASKS ----------
    public static Task[] getTasks(long groupId) {
        try {
            String url = BASE_URL + "/groups/" + groupId + "/tasks";

            ResponseEntity<Task[]> resp =
                    rest.getForEntity(url, Task[].class);

            return resp.getBody() != null ? resp.getBody() : new Task[0];

        } catch (Exception e) {
            e.printStackTrace();
            return new Task[0];
        }
    }

    // ---------- CREATE TASK ----------
    public static Task createTask(long groupId, long userId, String title, String description, String deadline) {
        try {
            String url = BASE_URL + "/groups/" + groupId + "/tasks?userId=" + userId;

            Map<String, String> body = new HashMap<>();
            body.put("title", title);
            body.put("description", description);
            body.put("deadline", deadline);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Task> response =
                    rest.exchange(url, HttpMethod.POST, request, Task.class);

            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------- UPDATE TASK ----------
    public static boolean updateTask(long groupId, long taskId, String title, String desc, String deadline) {
        try {
            String url = BASE_URL + "/groups/" + groupId + "/tasks/" + taskId;

            Map<String, String> body = new HashMap<>();
            body.put("title", title);
            body.put("description", desc);
            body.put("deadline", deadline);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            rest.exchange(url, HttpMethod.PUT, entity, Void.class);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ---------- UPDATE STATUS ----------
    public static void updateTaskStatus(long groupId, long taskId, String newStatus) {
        try {
            String url = BASE_URL + "/groups/" + groupId + "/tasks/" + taskId + "/status";

            Map<String, String> body = new HashMap<>();
            body.put("status", newStatus);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            rest.exchange(url, HttpMethod.POST, request, Void.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
