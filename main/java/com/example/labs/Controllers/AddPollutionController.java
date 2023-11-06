package com.example.labs.Controllers;

import com.example.labs.DataBase.Const;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPollutionController extends BaseController implements Initializable {
    private boolean isUpdate;
    @FXML
    private Label formTitleId;
    @FXML
    private ChoiceBox<String> enterprisesCB;
    @FXML
    private TextField pollutionValueId;
    @FXML
    private TextField concentrationValueId;
    @FXML
    private ChoiceBox<String> pollutantsCB;
    @FXML
    private ChoiceBox<Integer> yearCB;
    private double pollutionValue = 0, concentrationValue = 0;
    private int pollutionId = 0;

    @FXML
    void save() {
        String enterpriseName = enterprisesCB.getValue();
        String pollutantName = pollutantsCB.getValue();
        String pollutionStr = pollutionValueId.getText();
        String concentrationStr = concentrationValueId.getText();
        int year = yearCB.getValue();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        try {
            pollutionValue = Double.parseDouble(pollutionStr);
            concentrationValue = Double.parseDouble(concentrationStr);
        } catch (NumberFormatException e) {
            alert.setContentText("Значення викидів або концентрації не є числом!");
            alert.showAndWait();
        }

        if (enterpriseName.isEmpty() || pollutantName.isEmpty() || pollutionStr.isEmpty()
                || concentrationStr.isEmpty() || year == 0) {
            alert.setContentText("Заповніть всі дані!");
            alert.showAndWait();
        } else if (pollutionValue <= 0 || concentrationValue <=0) {
            alert.setContentText("Значення викидів та концентрації мають бути додатніми!");
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
        concentrationValueId.setText(null);
        yearCB.setValue(null);
    }

    private void getQuery() {
        if (!isUpdate) {
            query = "INSERT INTO " + Const.POLLUTION_TABLE + "("+Const.POLLUTION_OBJECT_ID + "," + Const.POLLUTION_CODE_POLLUTANT
                    + "," + Const.POLLUTION_VALUE + "," + Const.POLLUTION_CONCENTRATION + "," +Const.POLLUTION_HQ
                    + "," + Const.POLLUTION_CR + "," + Const.POLLUTION_COMPENSATION + "," + Const.POLLUTION_YEAR + ")" +
                    "VALUES(?,?,?,?,?,?,?,?)";
        }else{
            query = "UPDATE " + Const.POLLUTION_TABLE + " SET " +
                    Const.POLLUTION_OBJECT_ID + "=?," +
                    Const.POLLUTION_CODE_POLLUTANT + "=?," +
                    Const.POLLUTION_VALUE + "=?," +
                    Const.POLLUTION_CONCENTRATION + "=?," +
                    Const.POLLUTION_HQ + "=?," +
                    Const.POLLUTION_CR + "=?," +
                    Const.POLLUTION_COMPENSATION + "=?," +
                    Const.POLLUTION_YEAR + "=?" +
                    " WHERE " + Const.POLLUTION_ID + "=?";
        }
    }
    private void insert() {
            int pollutantCode = DB_Handler.getKeyByValue(DB_Handler.getPollutantsCodeAndName(),
                    pollutantsCB.getValue());
            int id_object = DB_Handler.getKeyByValue(DB_Handler.getEnterprisesNameAndID(),
                    enterprisesCB.getValue());
            int year = yearCB.getValue();
            // Перевірка, чи існує запис з такими ж ім'ям підприємства, назвою забрудника речовини та роком
            pollutionId = DB_Handler.isContainsRecordByEnterprisePollutantAndYear(id_object, pollutantCode, yearCB.getValue());
            if (pollutionId > 0) {
                isUpdate = true;
                getQuery();
                update();
                return;
            }
            DB_Handler.executeInsertProcess(query,id_object,pollutantCode,concentrationValue,pollutionValue,year);
    }
    private void update() {
        pollutionId = (pollution == null) ? pollutionId : pollution.getId_pollution();
        int pollutantCode = DB_Handler.getKeyByValue(DB_Handler.getPollutantsCodeAndName(),
                pollutantsCB.getValue());
        int id_object = DB_Handler.getKeyByValue(DB_Handler.getEnterprisesNameAndID(),
                enterprisesCB.getValue());
        int year = yearCB.getValue();
        DB_Handler.executeUpdateProcess(connection,query,id_object,pollutantCode,concentrationValue,pollutionValue,year,pollutionId);
    }

    void setTextField(String enterpriseName, String pollutantName, double pollutionValue,double concentrationValue, int year) {
        enterprisesCB.setValue(enterpriseName);
        pollutantsCB.setValue(pollutantName);
        pollutionValueId.setText(String.valueOf(pollutionValue));
        concentrationValueId.setText(String.valueOf(concentrationValue));
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
