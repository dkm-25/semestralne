package client;

import client.dto.ActivityLog;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;


public class ActivityLogController {

    @FXML
    private TableView<ActivityLog> logTable;

    @FXML
    private TableColumn<ActivityLog, String> timeCol;

    @FXML
    private TableColumn<ActivityLog, String> actionCol;

    @FXML
    private TableColumn<ActivityLog, String> detailsCol;

    @FXML
    public void initialize() {
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimestamp().toString()));
        actionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAction()));
        detailsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDetails()));
    }

    public void loadLogs(long userId) {
        ActivityLog[] logs = ApiClient.getActivityLog(userId);
        logTable.getItems().setAll(logs);
    }

    public void init(long userId) {
        ActivityLog[] logs = ApiClient.getActivityLog(userId);
        logTable.getItems().setAll(logs);
    }
}

