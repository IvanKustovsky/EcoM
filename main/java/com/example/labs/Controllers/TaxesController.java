package com.example.labs.Controllers;

import com.example.labs.DataBase.Const;
import com.example.labs.Models.Pollution;
import com.example.labs.Models.Tax;
import com.example.labs.Services.TaxCalculation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxesController extends BaseController implements Initializable {
    ObservableList<Tax> filteredList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Tax, String> objectNameCol;
    @FXML
    private TableColumn<Tax, String> pollutantNameCol;
    @FXML
    private TableColumn<Tax, Double> taxRateCol;
    @FXML
    private TableColumn<Tax, Double> taxSumCol;
    @FXML
    private TableColumn<Tax, Integer> yearCol;
    @FXML
    private TableView<Tax> taxesTable;
    @FXML
    private TableColumn<Tax, Double> valuePollutionCol;
    @FXML
    private TableColumn<Tax,Integer> classDangerCol;
    @FXML
    private TextField enterpriseFilter;
    @FXML
    private TextField pollutantFilter;
    @FXML
    private TextField yearFilter;
    @FXML
    private Text totalTaxAmount;
    private double totalTaxAmountValue;
    @FXML
    void exportIntoExcelBtn() {
        exportIntoExcelBtn(taxesTable);
    }
    private void loadData(){
        refreshTable();
        updateTotalTaxAmount();// Оновлення загальної суми податків
        objectNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_NAME));
        pollutantNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_NAME));
        classDangerCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_CLASS_DANGER));
        valuePollutionCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_VALUE));
        taxRateCol.setCellValueFactory(new PropertyValueFactory<>(Const.TAX_RATE));
        taxSumCol.setCellValueFactory(new PropertyValueFactory<>(Const.TAX_SUM));
        yearCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_YEAR));
        taxSumCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(String.format("%.2f", item));
                } else {
                    setText("");
                }
            }
        });
    }

    private void refreshTable() {
        try {
            TaxesList.clear();
            HashMap<Integer,String> pollutantsMap = DB_Handler.getPollutantsCodeAndName();
            for (Pollution pollution: PollutionsList) {
                if(pollution == null){
                    continue;
                }
                int pollutionId = pollution.getId_pollution();
                double pollutionValue = pollution.getValue_pollution();
                String pollutant = pollution.getName();
                String enterprise = pollution.getNameObject();
                int code = DB_Handler.getKeyByValue(pollutantsMap,pollutant);
                int dangerClass = Integer.parseInt(DB_Handler.getTableColumnById
                        (Const.POLLUTANT_TABLE, Const.POLLUTANT_CLASS_DANGER, code));
                int year = Integer.parseInt(DB_Handler.getTableColumnById
                        (Const.POLLUTION_TABLE, Const.POLLUTION_YEAR, pollutionId));
                String taxRateStr = DB_Handler.getTableColumnById(Const.POLLUTANT_TABLE,Const.POLLUTANT_TAX_RATE,code);
                double tax_rate = TaxCalculation.defineTaxRate(Double.parseDouble(taxRateStr),dangerClass);
                double tax_sum = TaxCalculation.calcTaxSum(pollutionValue,tax_rate);
                totalTaxAmountValue += tax_sum;
                TaxesList.add(new Tax(pollutionId,enterprise,pollutant,dangerClass,pollutionValue,tax_rate,tax_sum,year));
            }
            taxesTable.setItems(TaxesList);
            try {
                query = "DELETE FROM " + Const.TAX_TABLE;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                query = "INSERT INTO " + Const.TAX_TABLE + " (" + Const.TAX_POLLUTION_ID
                        + "," +Const.TAX_RATE + "," +Const.TAX_SUM +")" + " VALUES (?, ?, ?)";
                preparedStatement = connection.prepareStatement(query);
                for (Tax tax : TaxesList) {
                    preparedStatement.setInt(1, tax.getPollution_id());
                    preparedStatement.setDouble(2, tax.getRate());
                    preparedStatement.setDouble(3, tax.getSum());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TaxesController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    private ObservableList<Tax> filterData(String filter, ObservableList<Tax> filteredList, boolean isYearData,
                                           boolean isPollutantData, boolean isEnterpriseData) {
        ObservableList<Tax> currentFilteredList = FXCollections.observableArrayList();
        String lowerCaseFilter = filter.toLowerCase();

        for (Tax item : filteredList) {
            filter(lowerCaseFilter, currentFilteredList, item, isYearData, isPollutantData, isEnterpriseData);
        }
        return currentFilteredList;
    }

    private void filter(String filter, ObservableList<Tax> filteredList, Tax item, boolean isYearData,
                        boolean isPollutantData, boolean isEnterpriseData) {
        if (isYearData && String.valueOf(item.getYear()).toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
        if (isEnterpriseData && item.getNameObject().toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
        if (isPollutantData && item.getName().toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
    }

    private void checkAndSetFilteredList(String newValue, ObservableList<Tax> sourceList,
                                         String filterValue, boolean isYearData,
                                         boolean isPollutantData, boolean isEnterpriseData) {
        if (filteredList.isEmpty()) {
            filteredList = filterData(filterValue, sourceList, isYearData, isPollutantData, isEnterpriseData);
        } else {
            filteredList = filterData(filterValue, filteredList, isYearData, isPollutantData, isEnterpriseData);
        }
        if (newValue.isEmpty()) {
            filteredList = sourceList;
        }
        taxesTable.setItems(filteredList);
        updateTotalTaxAmount(); // Оновлення загальної суми податків
    }

    private void setUpFilters() {
        enterpriseFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, TaxesList, newValue, false,
                        false,  true));
        pollutantFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, TaxesList, newValue, false,
                        true, false));
        yearFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, TaxesList, newValue, true,
                        false, false));
    }

    private void updateTotalTaxAmount() {
        double totalTaxAmountValue = filteredList.isEmpty()
                ? TaxesList.stream().mapToDouble(Tax::getSum).sum()
                : filteredList.stream().mapToDouble(Tax::getSum).sum();

        totalTaxAmount.setText(String.format("%.2f", totalTaxAmountValue));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        enterpriseFilter.setVisible(false);
        pollutantFilter.setVisible(false);
        yearFilter.setVisible(false);
        loadData();
        setUpFilters();
    }
}
