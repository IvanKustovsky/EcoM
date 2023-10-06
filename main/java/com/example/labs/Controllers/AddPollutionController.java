package com.example.labs.Controllers;

import com.example.labs.DataBase.Const;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddPollutionController extends BaseController implements Initializable {
    private boolean isUpdate;
    @FXML
    private Label formTitleId;
    @FXML
    private ChoiceBox<String> enterprisesCB;
    @FXML
    private TextField pollutionValueId;
    @FXML
    private ChoiceBox<String> pollutantsCB;
    @FXML
    private ChoiceBox<Integer> yearCB;
    private double pollutionValue = 0;

    @FXML
    void save(ActionEvent event) {
        String enterpriseName = enterprisesCB.getValue();
        String pollutantName = pollutantsCB.getValue();
        String pollutionStr = pollutionValueId.getText();
        int year = yearCB.getValue();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        try {
            pollutionValue = Double.parseDouble(pollutionStr);
        } catch (NumberFormatException e) {
            alert.setContentText("Значення викидів не є числом!");
            alert.showAndWait();
        }

        if (enterpriseName.isEmpty() || pollutantName.isEmpty() || pollutionStr.isEmpty() || year == 0) {
            alert.setContentText("Заповніть всі дані!");
            alert.showAndWait();
        } else if (pollutionValue <= 0) {
            alert.setContentText("Значення викидів має бути додатнім!");
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

    @FXML
    private void clean() {
        enterprisesCB.setValue(null);
        pollutantsCB.setValue(null);
        pollutionValueId.setText(null);
    }

    private void getQuery() {
        if (!isUpdate) {
            query = "INSERT INTO " + Const.POLLUTION_TABLE + "("+Const.POLLUTION_OBJECT_ID + "," + Const.POLLUTION_CODE_POLLUTANT
                    + "," + Const.POLLUTION_VALUE + "," + Const.POLLUTION_YEAR + ")" +
                    "VALUES(?,?,?,?)";
        }else{
            query = "UPDATE " + Const.POLLUTION_TABLE + " SET " +
                    Const.POLLUTION_OBJECT_ID + "=?," +
                    Const.POLLUTION_CODE_POLLUTANT + "=?," +
                    Const.POLLUTION_VALUE + "=?," +
                    Const.POLLUTION_YEAR + "=?" +
                    " WHERE " + Const.POLLUTION_ID + "=?";
        }
    }
    private void insert() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, DB_Handler.getKeyByValue(DB_Handler.getEnterprisesNameAndID(),
                                     enterprisesCB.getValue()));
            preparedStatement.setInt(2, DB_Handler.getKeyByValue(DB_Handler.getPollutantsCodeAndName(),
                                     pollutantsCB.getValue()) );
            preparedStatement.setDouble(3, pollutionValue );
            preparedStatement.setInt(4, yearCB.getValue());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void update() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, DB_Handler.getKeyByValue(DB_Handler.getEnterprisesNameAndID(),
                    enterprisesCB.getValue()));
            preparedStatement.setInt(2, DB_Handler.getKeyByValue(DB_Handler.getPollutantsCodeAndName(),
                    pollutantsCB.getValue()));
            preparedStatement.setDouble(3, pollutionValue);
            preparedStatement.setInt(4, yearCB.getValue());
            preparedStatement.setInt(5,pollution.getId_pollution());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddPollutionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setTextField(String enterpriseName, String pollutantName, double pollutionValue, int year) {
        enterprisesCB.setValue(enterpriseName);
        pollutantsCB.setValue(pollutantName);
        pollutionValueId.setText(String.valueOf(pollutionValue));
        yearCB.setValue(year);
    }
    void setUpdate(boolean b) {
        this.isUpdate = b;
        if (b) {
            formTitleId.setText("Редагування даних");
        } else formTitleId.setText("Додавання об'єкта");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> years = yearCB.getItems();
        ObservableList<String> enterprises = enterprisesCB.getItems();
        ObservableList<String> pollutants = pollutantsCB.getItems();
        years.addAll(2016,2017,2018,2019,2020,2021,2022,2023);
        enterprises.addAll(DB_Handler.getEnterprisesNameAndID().values());
        pollutants.addAll(DB_Handler.getPollutantsCodeAndName().values());

    }
}
