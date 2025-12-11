package client;

import client.dto.Group;
import client.dto.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import client.dto.User;



public class MainController {

    @FXML
    private Label infoLabel;

    @FXML
    private ListView<Group> groupsList;

    @FXML
    private ListView<Task> tasksListView; // ЄДИНИЙ список задач!

    private long userId;
    private String userName;



    private Long currentGroupId = null;




    private void handleWsMessage(Object payload) {
        System.out.println("WS EVENT: " + payload);
        loadTasks(currentGroupId);
    }




    // Викликається після успішного логіну
    public void init(long userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        infoLabel.setText("Logged in as: " + userName);

        setupTaskListCellFactory();
        loadGroups();
    }




    // -----------------------------------------
    // Відображення задач у ListView<Task>
    // -----------------------------------------
    private void setupTaskListCellFactory() {
        tasksListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);

                if (empty || t == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(t.getTitle() + " - " + t.getStatus());

                    if ("DONE".equals(t.getStatus())) {
                        setStyle("-fx-text-fill: #888888;"); // сірий
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    // -----------------------------------------
    // Клік по групі → завантажити задачі
    // -----------------------------------------
    @FXML
    private void onGroupSelected(MouseEvent event) {
        Group selected = groupsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadTasks(selected.getId());
        }
    }



    // -----------------------------------------
    // Завантаження груп
    // -----------------------------------------
    private void loadGroups() {
        try {
            groupsList.getItems().clear();

            Group[] groups = ApiClient.getGroups(ApiClient.getUserId());

            if (groups == null || groups.length == 0) {
                showInfo("У вас немає груп");
                return;
            }

            groupsList.getItems().setAll(groups);
            showInfo("Групи оновлено");
        } catch (Exception e) {
            showError("Помилка завантаження груп");
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowActivity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/activity_log.fxml"));
            Parent root = loader.load();

            ActivityLogController controller = loader.getController();
            controller.init(ApiClient.getUserId());

            Stage stage = new Stage();
            stage.setTitle("Activity Log");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Помилка відкриття активності");
        }
    }

    @FXML
    private void onEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();

            User current = ApiClient.getUser(ApiClient.getUserId());
            controller.init(current);

            Stage stage = new Stage();
            stage.setTitle("Профіль");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // -----------------------------------------
    // Завантаження задач
    // -----------------------------------------
    private void loadTasks(long groupId) {
        tasksListView.getItems().clear();

        try {
            Task[] tasks = ApiClient.getTasks(groupId);

            System.out.println("Loaded tasks JSON:");
            for (Task t : tasks) {
                System.out.println("Task " + t.getId() + ": " + t.getTitle() + " | " + t.getStatus());
            }

            // 🔥 Ось цей рядок треба було повернути! 🔥
            tasksListView.getItems().setAll(tasks);

        } catch (Exception e) {
            showError("Помилка завантаження задач");
            e.printStackTrace();
        }

    }

    // -----------------------------------------
    // Змінити статус задачі на DONE
    // -----------------------------------------
    @FXML
    private void onMarkTaskDone() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            infoLabel.setText("Виберіть задачу");
            return;
        }

        try {
            ApiClient.updateTaskStatus(selected.getGroupId(), selected.getId(), "DONE");

            System.out.println("Trying to update: group=" + selected.getGroupId() +
                    " task=" + selected.getId());

            infoLabel.setText("Статус оновлено");

            loadTasks(selected.getGroupId());

        } catch (Exception ex) {
            infoLabel.setText("Помилка при оновленні статусу");
            ex.printStackTrace();
        }
    }

    @FXML
    private void onCreateTask() {
        Group selected = groupsList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            infoLabel.setText("Виберіть групу");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/create_task.fxml"));
            Parent root = loader.load();

            CreateTaskController controller = loader.getController();
            controller.init(
                    selected.getId(),
                    ApiClient.getUserId(),
                    () -> loadTasks(selected.getId()) // callback після створення
            );

            Stage stage = new Stage();
            stage.setTitle("Нова задача");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        infoLabel.setStyle("-fx-text-fill: red;");
        infoLabel.setText(message);
    }

    private void showInfo(String message) {
        infoLabel.setStyle("-fx-text-fill: black;");
        infoLabel.setText(message);
    }
    @FXML
    private void onRefreshGroups() {
        try {
            loadGroups();
            showInfo("Групи оновлено");
        } catch (Exception e) {
            showError("Помилка оновлення груп");
        }
    }

    @FXML
    private void onRefreshTasks() {
        Group selected = groupsList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Виберіть групу");
            return;
        }

        try {
            loadTasks(selected.getId());
            showInfo("Задачі оновлено");
        } catch (Exception e) {
            showError("Помилка оновлення задач");
        }
    }



    @FXML
    private void onToggleStatus() {
        Task t = tasksListView.getSelectionModel().getSelectedItem();
        if (t == null) {
            infoLabel.setText("Виберіть задачу");
            return;
        }

        String newStatus = t.getStatus().equals("DONE") ? "OPEN" : "DONE";

        ApiClient.updateTaskStatus(t.getGroupId(), t.getId(), newStatus);

        loadTasks(t.getGroupId());

        infoLabel.setText("Статус змінено: " + newStatus);
    }

    @FXML
    private void onEditTaskClick() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            infoLabel.setText("Виберіть задачу");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/create_task.fxml"));
            Parent root = loader.load();

            CreateTaskController controller = loader.getController();
            controller.initEdit(selected, () -> loadTasks(selected.getGroupId()));

            Stage stage = new Stage();
            stage.setTitle("Редагування задачі");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreateGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Нова група");
        dialog.setHeaderText("Створення групи");
        dialog.setContentText("Назва групи:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                ApiClient.createGroup(ApiClient.getUserId(), name, "");

                infoLabel.setText("Групу створено");
                loadGroups();
            }
        });
    }

    @FXML
    private void onAddUserToGroup() {
        Group selected = groupsList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            infoLabel.setText("Виберіть групу");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Додати користувача");
        dialog.setHeaderText("Додати користувача до групи");
        dialog.setContentText("Введіть userId:");

        dialog.showAndWait().ifPresent(idStr -> {
            try {
                long userId = Long.parseLong(idStr);
                ApiClient.addUserToGroup(selected.getId(), userId);

                infoLabel.setText("Користувача додано");
            } catch (NumberFormatException e) {
                infoLabel.setText("Невірний userId");
            } catch (Exception e) {
                infoLabel.setText("Помилка додавання користувача");
                e.printStackTrace();
            }
        });
    }






}
