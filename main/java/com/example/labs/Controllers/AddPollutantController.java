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

public class AddPollutantController extends BaseController implements Initializable {
    @FXML
    private Label formTitleId;
    @FXML
    private TextField gdkId;
    @FXML
    private TextField massConsumptionId;
    @FXML
    private TextField pollutantNameId;
    private boolean isUpdate;
    private double gdkValue = 0, massConsumptionValue = 0;

    @FXML
    void save() {
        String pollutantName = pollutantNameId.getText();
        String gdk = gdkId.getText();
        String massConsumption = massConsumptionId.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        try {
            gdkValue = Double.parseDouble(gdk);
            massConsumptionValue = Double.parseDouble(massConsumption);
        } catch (NumberFormatException e) {
            alert.setContentText("ГДВ або величина масової витрати не є числом!");
            alert.showAndWait();
        }

        if (pollutantName.isEmpty() || gdk.isEmpty() || massConsumption.isEmpty()) {
            alert.setContentText("Заповніть всі дані!");
            alert.showAndWait();
        } else if (gdkValue <= 0 || massConsumptionValue <= 0) {
            alert.setContentText("ГДВ або величина масової витрати не може бути від'ємним значенням!");
            alert.showAndWait();
        }
        else {
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


    private void clean() {
        pollutantNameId.setText(null);
        gdkId.setText(null);
        massConsumptionId.setText(null);
    }
    private void getQuery() {
        if (!isUpdate) {
            query = "INSERT INTO " + Const.POLLUTANT_TABLE + "("+Const.POLLUTANT_CODE + "," + Const.POLLUTANT_NAME + "," +
                    Const.POLLUTANT_GDK + "," + Const.POLLUTANT_MASS_CONSUMPTION + ")" +
                    "VALUES(?,?,?,?)";
        }else{
            query = "UPDATE " + Const.POLLUTANT_TABLE + " SET " +
                    Const.POLLUTANT_NAME + "=?," +
                    Const.POLLUTANT_MASS_CONSUMPTION + "=?," +
                    Const.POLLUTANT_GDK + "=?" +
                    " WHERE " + Const.POLLUTANT_CODE + "=?";
        }
    }
    private void insert() {
        try {
            // Отримуємо найбільший id з таблиці object
            int maxCode = DB_Handler.getMaxIdFromTable(Const.POLLUTANT_TABLE);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, maxCode + 1);
            preparedStatement.setString(2, pollutantNameId.getText());
            preparedStatement.setDouble(3, massConsumptionValue);
            preparedStatement.setDouble(4, gdkValue);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void update() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, pollutantNameId.getText());
            preparedStatement.setDouble(2, gdkValue);
            preparedStatement.setDouble(3, massConsumptionValue);
            preparedStatement.setInt(4, pollutant.getCode());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void setTextField(String nameObject, double gdk, double massConsumption) {
        pollutantNameId.setText(nameObject);
        gdkId.setText(String.valueOf(gdk));
        massConsumptionId.setText(String.valueOf(massConsumption));
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
