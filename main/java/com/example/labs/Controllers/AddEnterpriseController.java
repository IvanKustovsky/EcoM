package com.example.labs.Controllers;

import com.example.labs.DataBase.Const;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddEnterpriseController extends ObjectController implements Initializable {
    @FXML
    private TextField descriptionId;
    @FXML
    private TextField enterpriseNameId;
    @FXML
    private TextField locationId;
    @FXML
    private Label formTitleId;
    private boolean isUpdate;

    @FXML
    void save() {
        String name = enterpriseNameId.getText();
        String location = locationId.getText();
        String description = descriptionId.getText();

        if (name.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Заповніть всі дані!");
            alert.showAndWait();
        } else {
            getQuery();
            if(isUpdate) {
                update();
                Stage stage = (Stage) formTitleId.getScene().getWindow();
                stage.close();
            } else {
                insert();
                clean();
            }

        }
    }

    @FXML
    private void clean() {
        enterpriseNameId.setText(null);
        locationId.setText(null);
        descriptionId.setText(null);
    }

    private void getQuery() {

        if (!isUpdate) {
            query = "INSERT INTO " + Const.OBJECT_TABLE + "("+Const.OBJECT_ID + "," + Const.OBJECT_NAME + "," +
                    Const.OBJECT_LOCATION + "," + Const.OBJECT_DESCRIPTION + ")" +
                    "VALUES(?,?,?,?)";
        }else{
            query = "UPDATE " + Const.OBJECT_TABLE + " SET " +
                    Const.OBJECT_NAME + "=?," +
                    Const.OBJECT_LOCATION + "=?," +
                    Const.OBJECT_DESCRIPTION + "=?" +
                    " WHERE " + Const.OBJECT_ID + "=?";
        }
    }
    private void insert() {
        try {
            // Отримуємо найбільший id з таблиці object
            int maxId = DB_Handler.getMaxIdFromTable(Const.OBJECT_TABLE);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, maxId + 1);
            preparedStatement.setString(2, enterpriseNameId.getText());
            preparedStatement.setString(3, locationId.getText());
            preparedStatement.setString(4, descriptionId.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void update() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, enterpriseNameId.getText());
            preparedStatement.setString(2, locationId.getText());
            preparedStatement.setString(3, descriptionId.getText());
            preparedStatement.setInt(4, enterprise.getId());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setTextField(String nameObject, String location, String description) {
        enterpriseNameId.setText(nameObject);
        locationId.setText(location);
        descriptionId.setText(description);
    }
    void setUpdate(boolean b) {
        this.isUpdate = b;
        if (b) {
            formTitleId.setText("Редагування даних");
        } else formTitleId.setText("Додавання об'єкта");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
