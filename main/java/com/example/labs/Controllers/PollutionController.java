package com.example.labs.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.labs.Services.Calculations;
import com.example.labs.DataBase.Const;
import com.example.labs.Models.Pollution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PollutionController extends BaseController implements Initializable {
    ObservableList<Pollution> filteredList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Pollution, Double> concentrationCol;
    @FXML
    private TableColumn<Pollution, Double> compensationCol;
    @FXML
    private TableColumn<Pollution, Double> crCol;
    @FXML
    private TableColumn<Pollution, Double> hqCol;
    @FXML
    private TableColumn<Pollution, String> enterpriseNameCol;
    @FXML
    private TableColumn<Pollution, String> idCol;
    @FXML
    private TableColumn<Pollution, String> pollutantNameCol;
    @FXML
    private TableView<Pollution> pollutionTable;
    @FXML
    private TableColumn<Pollution, String> valuePollutionCol;
    @FXML
    private TableColumn<Pollution, String> yearCol;
    @FXML
    private TextField enterpriseFilter;
    @FXML
    private TextField idFilter;
    @FXML
    private TextField pollutantFilter;
    @FXML
    private TextField yearFilter;


    @FXML
    void deleteData() {
        Pollution selectedItem = pollutionTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            PollutionsList = pollutionTable.getItems();
            try {
                String deleteQuery = "DELETE FROM " + Const.POLLUTION_TABLE + " WHERE " + Const.POLLUTION_ID +" = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, selectedItem.getId_pollution());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                refreshTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            alert.setHeaderText(null);
            alert.setContentText("Щоб видалити запис спочатку оберіть його!");
            alert.showAndWait();
        }
    }

    @FXML
    void editData() {
        pollution = pollutionTable.getSelectionModel().getSelectedItem();
        if (pollution != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/labs/addPollution.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
            }

            AddPollutionController addPollutionController = loader.getController();
            addPollutionController.setUpdate(true);
            addPollutionController.setTextField(pollution.getNameObject(), pollution.getName(),
                    pollution.getValue_pollution(),pollution.getConcentration(),pollution.getYear());
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } else {
            alert.setHeaderText(null);
            alert.setContentText("Щоб оновити запис спочатку оберіть його!");
            alert.showAndWait();
        }
    }

    @FXML
    void importExcelData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\user\\Desktop" +
                "\\НАВЧАННЯ\\5 семестр\\ЕкоМ\\Лаб2"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                query = "INSERT INTO " + Const.POLLUTION_TABLE + "(" + Const.POLLUTION_OBJECT_ID + "," +
                        Const.POLLUTION_CODE_POLLUTANT + "," + Const.POLLUTION_VALUE + "," +
                        Const.POLLUTION_CONCENTRATION + "," +Const.POLLUTION_HQ + "," + Const.POLLUTION_CR + ","
                        + Const.POLLUTION_COMPENSATION + "," + Const.POLLUTION_YEAR + ")" +
                        "VALUES(?,?,?,?,?,?,?,?)";

                Sheet sheet = workbook.getSheetAt(0);
                // Починаємо читання з другого рядка (індекс 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    int id_object = (int) row.getCell(0).getNumericCellValue();
                    int pollutantCode = (int) row.getCell(1).getNumericCellValue();
                    double pollutionValue = row.getCell(2).getNumericCellValue();
                    int year = (int) row.getCell(3).getNumericCellValue();
                    double concentrationValue = row.getCell(4).getNumericCellValue();
                    // Перевірка, чи існує об'єкт з такими ж ім'ям і локацією
                    String checkQuery = "SELECT COUNT(*) FROM " + Const.POLLUTION_TABLE +
                            " WHERE " + Const.POLLUTION_OBJECT_ID + " = ? AND " +
                            Const.POLLUTION_CODE_POLLUTANT + " = ? AND " + Const.POLLUTION_YEAR + " = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setDouble(1, id_object);
                    checkStatement.setDouble(2, pollutantCode);
                    checkStatement.setDouble(3, year);
                    ResultSet resultSet = checkStatement.executeQuery();
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            continue;
                        }
                    }
                    DB_Handler.executeInsertProcess(query,id_object,pollutantCode,concentrationValue,pollutionValue,year);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        refreshTable();
    }

    @FXML
    void exportIntoExcelBtn() {
        exportIntoExcelBtn(pollutionTable);
    }


    private ObservableList<Pollution> filterData(String filter, ObservableList<Pollution> filteredList, boolean isYearData,
                                                 boolean isIdData, boolean isPollutantData, boolean isEnterpriseData) {
        ObservableList<Pollution> currentFilteredList = FXCollections.observableArrayList();
        String lowerCaseFilter = filter.toLowerCase();
        for (Pollution item : filteredList) {
            filter(lowerCaseFilter, currentFilteredList, item, isYearData, isIdData, isPollutantData, isEnterpriseData);
        }

        return currentFilteredList;
    }

    private void filter(String filter, ObservableList<Pollution> filteredList, Pollution item, boolean isYearData,
                        boolean isIdData, boolean isPollutantData, boolean isEnterpriseData) {
        if (isYearData && String.valueOf(item.getYear()).toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
        if (isIdData && String.valueOf(item.getId_pollution()).toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
        if (isEnterpriseData && item.getNameObject().toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
        if (isPollutantData && item.getName().toLowerCase().contains(filter)) {
            filteredList.add(item);
        }
    }

    private void checkAndSetFilteredList(String newValue, ObservableList<Pollution> sourceList,
                                         String filterValue, boolean isYearData, boolean isIdData,
                                         boolean isPollutantData, boolean isEnterpriseData) {
            if (filteredList.isEmpty()) {
                filteredList = filterData(filterValue, sourceList, isYearData, isIdData, isPollutantData, isEnterpriseData);
            } else {
                filteredList = filterData(filterValue, filteredList, isYearData, isIdData, isPollutantData, isEnterpriseData);
            }
        if (newValue.isEmpty()) {
            filteredList = sourceList;
        }
            pollutionTable.setItems(filteredList);
    }

    private void setUpFilters() {
        enterpriseFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, PollutionsList, newValue, false,
                        false, false, true));
        idFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, PollutionsList, newValue, false,
                        true, false, false));
        pollutantFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, PollutionsList, newValue, false,
                        false, true, false));
        yearFilter.textProperty().addListener((observable, oldValue, newValue) ->
                checkAndSetFilteredList(newValue, PollutionsList, newValue, true,
                        false, false, false));
    }

    private void loadData() {
        refreshTable();
        idCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_ID));
        enterpriseNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_NAME));
        pollutantNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_NAME));
        valuePollutionCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_VALUE));
        concentrationCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_CONCENTRATION));
        hqCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_HQ));
        crCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_CR));
        yearCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_YEAR));
        compensationCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_COMPENSATION));
        compensationCol.setCellFactory(column -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.1f", item));
                    }
                }
            };
        });
        hqCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(String.format("%.6f", item));
                    Tooltip tooltipMin = new Tooltip("Ризик виникнення шкідливих ефектів розглядають як зневажливо малий");
                    Tooltip tooltipLimit = new Tooltip("Гранична величина, що не потребує термінових " +
                            "заходів, однак не може розглядатися як досить прийнятна");
                    Tooltip tooltipHigh = new Tooltip("Імовірність розвитку шкідливих ефектів " +
                            "зростає пропорційно збільшенню HQ");
                    if (item < 1) {
                        setStyle("-fx-background-color: green;");
                        setTooltip(tooltipMin);
                    } else if (item == 1) {
                        setStyle("-fx-background-color: orange;");
                        setTooltip(tooltipLimit);
                    }  else {
                        setStyle("-fx-background-color: red;");
                        setTooltip(tooltipHigh);
                    }
                } else {
                    setText("");
                    setStyle("");
                }
            }
        });

        crCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(String.format("%.8f", item));
                    Tooltip tooltipMin = new Tooltip("Мінімальний - бажана величина ризику при проведенні оздоровчих і " +
                            "природоохоронних заходів");
                    Tooltip tooltipLow = new Tooltip("Низький - припустимий ризик (рівень, на якому, як " +
                            "правило, встановлюються гігієнічні нормативи для населення)");
                    Tooltip tooltipAverage = new Tooltip("Середній - припустимий для виробничих умов; за впливу на все " +
                            "населення необхідний динамічний контроль і поглиблене вивчення джерел і можливих наслідків " +
                            "шкідливих впливів для вирішення питання про заходи з управління ризиком ");
                    Tooltip tooltipHigh = new Tooltip("Високий - не прийнятний для виробничих умов і населення. " +
                            "Необхідне здійснення заходів з усунення або зниження ризику ");
                    if (item <= Math.pow(10,-6)) {
                        setStyle("-fx-background-color: green;");
                        setTooltip(tooltipMin);
                    } else if (item > Math.pow(10,-6) && item <= Math.pow(10,-4)) {
                        setStyle("-fx-background-color: yellow;");
                        setTooltip(tooltipLow);
                    }
                    else if (item > Math.pow(10,-4) && item <= Math.pow(10,-3)) {
                        setStyle("-fx-background-color: orange;");
                        setTooltip(tooltipAverage);
                    } else {
                        setStyle("-fx-background-color: red;");
                        setTooltip(tooltipHigh);
                    }
                } else {
                    setText("");
                    setStyle("");
                }
            }
        });
        concentrationCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(String.format("%.6f", item));
                } else {
                    setText("");
                }
            }
        });

    }
    @FXML
    void getAddView() {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/labs/addPollution.fxml")));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void refreshTable() {
        try {
            PollutionsList.clear();
            query = "SELECT * FROM " + Const.POLLUTION_TABLE;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int pollutionId = resultSet.getInt(Const.POLLUTION_ID);
                int object_id = resultSet.getInt(Const.POLLUTION_OBJECT_ID);
                int code = resultSet.getInt(Const.POLLUTION_CODE_POLLUTANT);
                String enterpriseName = DB_Handler.getTableColumnById(Const.OBJECT_TABLE,Const.OBJECT_NAME,object_id);
                String pollutantName = DB_Handler.getTableColumnById(Const.POLLUTANT_TABLE,Const.POLLUTANT_NAME,code);
                double pollutionValue = resultSet.getDouble(Const.POLLUTION_VALUE);
                double concentration = resultSet.getDouble(Const.POLLUTION_CONCENTRATION);
                int pollutionYear = resultSet.getInt(Const.POLLUTION_YEAR);
                double hq = 0, cr = 0, rfc, sf, gdk, mass_consumption, compensation = 0;
                String rfcStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_RFC,code);
                String sfStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_SF,code);
                String gdkStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_GDK,code);
                String mass_consumptionStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_MASS_CONSUMPTION,code);
                try {
                    gdk = Double.parseDouble(gdkStr);
                    mass_consumption = Double.parseDouble(mass_consumptionStr);
                    compensation = Calculations.calcCompensation(pollutionValue,mass_consumption,gdk);
                    rfc = Double.parseDouble(rfcStr);
                    sf = Double.parseDouble(sfStr);
                    hq = Calculations.CalcHq(concentration,rfc);
                    cr = Calculations.CalcCR(concentration,sf);
                } catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

                PollutionsList.add(new Pollution(pollutionId, enterpriseName, pollutantName, pollutionValue,
                        concentration, hq, cr, compensation, pollutionYear));

            }
            pollutionTable.setItems(PollutionsList);
        } catch (SQLException ex) {
            Logger.getLogger(PollutionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idFilter.setVisible(false);
        enterpriseFilter.setVisible(false);
        pollutantFilter.setVisible(false);
        yearFilter.setVisible(false);
        loadData();
        setUpFilters();
    }
}

