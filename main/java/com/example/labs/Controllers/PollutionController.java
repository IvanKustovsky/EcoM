package com.example.labs.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.labs.Calculations.Calculations;
import com.example.labs.DataBase.Const;
import com.example.labs.DataBase.DataBaseHandler;
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
    ObservableList<Pollution> PollutionsList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Pollution, Double> concentrationCol;
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
                String insert = "INSERT INTO " + Const.POLLUTION_TABLE + "(" + Const.POLLUTION_OBJECT_ID + "," +
                        Const.POLLUTION_CODE_POLLUTANT + "," + Const.POLLUTION_VALUE + "," +
                        Const.POLLUTION_CONCENTRATION + "," +Const.POLLUTION_HQ + "," + Const.POLLUTION_CR + ","
                        + Const.POLLUTION_YEAR + ")" +
                        "VALUES(?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insert);

                Sheet sheet = workbook.getSheetAt(0);
                // Починаємо читання з другого рядка (індекс 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    double id_object = row.getCell(0).getNumericCellValue();
                    double code_pollutant = row.getCell(1).getNumericCellValue();
                    double value_pollution = row.getCell(2).getNumericCellValue();
                    double year = row.getCell(3).getNumericCellValue();
                    double concentration = row.getCell(4).getNumericCellValue();
                    double hq = 0, cr = 0, rfc, sf;
                    String rfcStr = DB_Handler.getTableColumnById(
                            Const.POLLUTANT_TABLE,Const.POLLUTANT_RFC,(int)code_pollutant);
                    String sfStr = DB_Handler.getTableColumnById(
                            Const.POLLUTANT_TABLE,Const.POLLUTANT_SF,(int)code_pollutant);

                    try {
                        rfc = Double.parseDouble(rfcStr);
                        sf = Double.parseDouble(sfStr);
                        hq = Calculations.CalcHq(concentration,rfc);
                        cr = Calculations.CalcCR(concentration,sf);
                    } catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    // Перевірка, чи існує об'єкт з такими ж ім'ям і локацією
                    String checkQuery = "SELECT COUNT(*) FROM " + Const.POLLUTION_TABLE +
                            " WHERE " + Const.POLLUTION_OBJECT_ID + " = ? AND " +
                            Const.POLLUTION_CODE_POLLUTANT + " = ? AND " + Const.POLLUTION_YEAR + " = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setDouble(1, id_object);
                    checkStatement.setDouble(2, code_pollutant);
                    checkStatement.setDouble(3, year);
                    ResultSet resultSet = checkStatement.executeQuery();
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            continue;
                        }
                    }
                    preparedStatement.setDouble(1, id_object);
                    preparedStatement.setDouble(2, code_pollutant);
                    preparedStatement.setDouble(3, value_pollution);
                    preparedStatement.setDouble(4, concentration);
                    preparedStatement.setDouble(5, hq);
                    preparedStatement.setDouble(6, cr);
                    preparedStatement.setDouble(7, year);

                    preparedStatement.executeUpdate();
                }

                System.out.println("Data imported successfully!");

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
    private ObservableList<Pollution> filterStringData(String filter, boolean isFilterByPollutantName) {
        ObservableList<Pollution> filteredList = FXCollections.observableArrayList();
        for (Pollution item : PollutionsList) {
            if(isFilterByPollutantName){
                filterByPollutantName(filter, filteredList, item);
            } else {
                filterByEnterpriseName(filter, filteredList, item);
            }
        }
        return filteredList;
    }

    private void filterByEnterpriseName(String filter, ObservableList<Pollution> filteredList, Pollution item) {
        if (item.getNameObject().toLowerCase().contains(filter.toLowerCase())) {
            filteredList.add(item);
        }
    }

    private void filterByPollutantName(String filter, ObservableList<Pollution> filteredList, Pollution item) {
        if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
            filteredList.add(item);
        }
    }

    private ObservableList<Pollution> filterDigitData(String filter, boolean isFilterByYear) {
        ObservableList<Pollution> filteredList = FXCollections.observableArrayList();

        // Перевірка, чи рядок є числом
        if (isNumeric(filter)) {
            for (Pollution item : PollutionsList) {
                if (isFilterByYear) {
                    filterByYear(filter, filteredList, item);
                } else {
                    filterById(filter, filteredList, item);
                }
            }
        }

        return filteredList;
    }
    private void filterByYear(String filter, ObservableList<Pollution> filteredList, Pollution item) {
        if (item.getYear() == Integer.parseInt(filter)) {
            filteredList.add(item);
        }
    }

    private void filterById(String filter, ObservableList<Pollution> filteredList, Pollution item) {
        if (item.getId_pollution() == Integer.parseInt(filter)) {
            filteredList.add(item);
        }
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
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/labs/addPollution.fxml"));
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
            query = "SELECT * FROM "+Const.POLLUTION_TABLE;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            DataBaseHandler dataBaseHandler = new DataBaseHandler();
            while (resultSet.next()){
                int pollutionId = resultSet.getInt(Const.POLLUTION_ID);
                int object_id = resultSet.getInt(Const.POLLUTION_OBJECT_ID);
                int code = resultSet.getInt(Const.POLLUTION_CODE_POLLUTANT);
                String enterpriseName = dataBaseHandler.getTableColumnById(Const.OBJECT_TABLE,Const.OBJECT_NAME,object_id);
                String pollutantName = dataBaseHandler.getTableColumnById(Const.POLLUTANT_TABLE,Const.POLLUTANT_NAME,code);
                double pollutionValue = resultSet.getDouble(Const.POLLUTION_VALUE);
                double concentration = resultSet.getDouble(Const.POLLUTION_CONCENTRATION);
                int pollutionYear = resultSet.getInt(Const.POLLUTION_YEAR);
                double hq = 0, cr = 0, rfc, sf;
                String rfcStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_RFC,code);
                String sfStr = DB_Handler.getTableColumnById(
                        Const.POLLUTANT_TABLE,Const.POLLUTANT_SF,code);

                try {
                    rfc = Double.parseDouble(rfcStr);
                    sf = Double.parseDouble(sfStr);
                    hq = Calculations.CalcHq(concentration,rfc);
                    cr = Calculations.CalcCR(concentration,sf);
                } catch (Exception ex){
                    System.out.println(ex.getMessage());
                }

                PollutionsList.add(new Pollution(pollutionId, enterpriseName, pollutantName, pollutionValue,
                        concentration, hq, cr, pollutionYear));

            }
            pollutionTable.setItems(PollutionsList);
        } catch (SQLException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setUpFilters(){
        enterpriseFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            } else {
                pollutionTable.setItems(filterStringData(newValue, false));
            }
        });

        pollutantFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            } else {
                pollutionTable.setItems(filterStringData(newValue, true));
            }
        });

        idFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            } else {
                pollutionTable.setItems(filterDigitData(newValue, false));
            }
        });

        yearFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            }
            else {
                pollutionTable.setItems(filterDigitData(newValue, true));
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idFilter.setVisible(false);
        enterpriseFilter.setVisible(false);
        pollutantFilter.setVisible(false);
        yearFilter.setVisible(false);
        setUpFilters();
        loadData();
    }
}

