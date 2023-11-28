package com.example.labs.Controllers;

import com.example.labs.DataBase.Const;
import com.example.labs.Services.TaxCalculation;
import javafx.collections.ObservableList;
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

public class AddPollutantController extends BaseController implements Initializable {
    @FXML
    private Label formTitleId;
    @FXML
    private TextField gdkId;
    @FXML
    private TextField massConsumptionId;
    @FXML
    private TextField rfcId;
    @FXML
    private TextField sfId;
    @FXML
    private TextField pollutantNameId;
    @FXML
    private ChoiceBox<Integer> dangerClassesCB;
    @FXML
    private TextField taxRateId;
    private boolean isUpdate;
    private double gdkValue = 0, massConsumptionValue = 0, rfcValue = 0, sfValue = 0, taxRateValue = 0;

    @FXML
    void save() {
        String pollutantName = pollutantNameId.getText();
        String gdk = gdkId.getText();
        String massConsumption = massConsumptionId.getText();
        String rfc = rfcId.getText();
        String sf = sfId.getText();
        String taxRateStr = taxRateId.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        try {
            gdkValue = getDoubleValue(gdk, "ГДК");
            massConsumptionValue = getDoubleValue(massConsumption, "Величина масової витрати");
            rfcValue = getDoubleValue(rfc, "Референтна концентрація");
            sfValue = getDoubleValue(sf, "Фактор нахилу");
            taxRateValue = TaxCalculation.defineTaxRate(Double.parseDouble(taxRateStr), dangerClassesCB.getValue());
        }
        catch (NullPointerException ex) {
            alert.setContentText("Оберіть клас небезпеки!");
            alert.showAndWait();
        }

       if (gdkValue <= 0 || massConsumptionValue <= 0 || rfcValue <= 0 || sfValue <= 0 || taxRateValue < 0) {
            alert.setContentText("ГДВ, величина масової витрати, референтна концентрація, фактор нахилу або ставка податку " +
                    "не може бути від'ємним значенням!");
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
        rfcId.setText(null);
        sfId.setText(null);
        dangerClassesCB.setValue(null);
        taxRateId.setText(null);
    }
    private void getQuery() {
        if (!isUpdate) {
            query = "INSERT INTO " + Const.POLLUTANT_TABLE + "(" + Const.POLLUTANT_CODE +","
                    + Const.POLLUTANT_NAME + "," + Const.POLLUTANT_GDK + ","
                    + Const.POLLUTANT_MASS_CONSUMPTION + "," + Const.POLLUTANT_RFC + "," + Const.POLLUTANT_SF
                    + "," + Const.POLLUTANT_CLASS_DANGER + "," + Const.POLLUTANT_TAX_RATE + ")"
                    + "VALUES(?,?,?,?,?,?,?,?)";
        }else{
            query = "UPDATE " + Const.POLLUTANT_TABLE + " SET " +
                    Const.POLLUTANT_NAME + "=?," +
                    Const.POLLUTANT_MASS_CONSUMPTION + "=?," +
                    Const.POLLUTANT_GDK + "=?," +
                    Const.POLLUTANT_RFC + "=?," +
                    Const.POLLUTANT_SF + "=?," +
                    Const.POLLUTANT_CLASS_DANGER + "=?," +
                    Const.POLLUTANT_TAX_RATE + "=?" +
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
            preparedStatement.setDouble(5, rfcValue);
            preparedStatement.setDouble(6, sfValue);
            preparedStatement.setInt(7, dangerClassesCB.getValue());
            preparedStatement.setDouble(8, taxRateValue);
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void update() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, pollutantNameId.getText());
            preparedStatement.setDouble(2, massConsumptionValue);
            preparedStatement.setDouble(3, gdkValue);
            preparedStatement.setDouble(4, rfcValue);
            preparedStatement.setDouble(5, sfValue);
            preparedStatement.setInt(6, dangerClassesCB.getValue());
            preparedStatement.setDouble(7, taxRateValue);
            preparedStatement.setInt(8, pollutant.getCode());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(AddEnterpriseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void setTextField(String nameObject, double gdk, double massConsumption, double rfc,
                      double sf, int danger_class, double tax_rate) {
        pollutantNameId.setText(nameObject);
        gdkId.setText(String.valueOf(gdk));
        massConsumptionId.setText(String.valueOf(massConsumption));
        rfcId.setText(String.valueOf(rfc));
        sfId.setText(String.valueOf(sf));
        dangerClassesCB.setValue(danger_class);
        taxRateId.setText(String.valueOf(tax_rate));
    }
    void setUpdate(boolean b) {
        this.isUpdate = b;
        if (b) {
            formTitleId.setText("Редагування даних");
        } else formTitleId.setText("Додавання об'єкта");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> dangerClasses = dangerClassesCB.getItems();
        dangerClasses.addAll(1,2,3,4);
        taxRateId.setText("0");
    }


}
