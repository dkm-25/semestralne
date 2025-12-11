package client;

import client.dto.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateTaskController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField deadlineField;

    @FXML
    private Label infoLabel;

    @FXML
    private Button createButton;

    private long groupId;
    private long userId;

    private Runnable onTaskCreated;
    private Task editingTask; // якщо редагування

    //------------------------------------------------------------------
    // 1) init – створення нової задачі
    //------------------------------------------------------------------
    public void init(long groupId, long userId, Runnable onTaskCreated) {
        this.groupId = groupId;
        this.userId = userId;
        this.onTaskCreated = onTaskCreated;
    }

    //------------------------------------------------------------------
    // 2) initEdit – редагування існуючої задачі
    //------------------------------------------------------------------
    public void initEdit(Task task, Runnable onUpdated) {
        this.editingTask = task;
        this.onTaskCreated = onUpdated;

        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        deadlineField.setText(task.getDeadline());

        createButton.setText("Зберегти");
    }

    //------------------------------------------------------------------
    // 3) onCreateClick – створити або зберегти редагування
    //------------------------------------------------------------------
    @FXML
    private void onCreateClick() {

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (title.isEmpty()) {
            infoLabel.setText("Назва задачі не може бути порожня");
            return;
        }

        if (deadline.isEmpty()) {
            infoLabel.setText("Виберіть дедлайн");
            return;
        }

        try {

            if (editingTask == null) {
                // ✔ створення нової задачі
                ApiClient.createTask(groupId, userId, title, description, deadline);
            } else {
                // ✔ редагування існуючої задачі
                ApiClient.updateTask(editingTask.getGroupId(), editingTask.getId(), title, description, deadline);

            }

            if (onTaskCreated != null)
                onTaskCreated.run();

            ((Stage) createButton.getScene().getWindow()).close();

        } catch (Exception e) {
            infoLabel.setText("Помилка створення/редагування задачі");
            e.printStackTrace();
        }
    }
}
