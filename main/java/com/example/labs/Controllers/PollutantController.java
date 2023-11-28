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

import com.example.labs.DataBase.Const;
import com.example.labs.Models.Pollutant;
import com.example.labs.Services.TaxCalculation;
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

public class PollutantController extends BaseController implements Initializable {
    @FXML
    private TableView<Pollutant> pollutantsTable;
    @FXML
    private TableColumn<Pollutant, String> gdkCol;
    @FXML
    private TableColumn<Pollutant, String> codeCol;
    @FXML
    private TableColumn<Pollutant, String> massConsumptionCol;
    @FXML
    private TableColumn<Pollutant, Double> rfcCol;
    @FXML
    private TableColumn<Pollutant, Double> sfCol;
    @FXML
    private TableColumn<Pollutant, String> pollutantNameCol;
    @FXML
    private TableColumn<Pollutant, Integer> dangerClassCol;
    @FXML
    private TableColumn<Pollutant, Double> taxRateCol;
    @FXML
    private TextField pollutantFilter;


    @FXML
    void deleteData() {
        pollutant = pollutantsTable.getSelectionModel().getSelectedItem();
        if (pollutant != null) {
            PollutantsList = pollutantsTable.getItems();
            try {
                String deleteQuery = "DELETE FROM " + Const.POLLUTANT_TABLE + " WHERE " + Const.POLLUTANT_CODE +" = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, pollutant.getCode());
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
        pollutant = pollutantsTable.getSelectionModel().getSelectedItem();
        if (pollutant != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/labs/addPollutant.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PollutantController.class.getName()).log(Level.SEVERE, null, ex);
            }

            AddPollutantController addPollutantController = loader.getController();
            addPollutantController.setUpdate(true);
            addPollutantController.setTextField(pollutant.getName(),pollutant.getGdk(),
                    pollutant.getMass_consumption(),pollutant.getRfc(),pollutant.getSf(),
                    pollutant.getDanger_class(),pollutant.getTax_rate());
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
        refreshTable();
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

                String insert = "INSERT INTO " + Const.POLLUTANT_TABLE + "(" + Const.POLLUTANT_CODE +","
                        + Const.POLLUTANT_NAME + "," + Const.POLLUTANT_GDK + ","
                        + Const.POLLUTANT_MASS_CONSUMPTION + "," + Const.POLLUTANT_RFC + "," + Const.POLLUTANT_SF
                        + "," + Const.POLLUTANT_TAX_RATE + "," + Const.POLLUTANT_CLASS_DANGER
                        + ")" + "VALUES(?,?,?,?,?,?,?,?)";
                preparedStatement = connection.prepareStatement(insert);

                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    int code = (int) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    double gdk = row.getCell(2).getNumericCellValue();
                    double mass_consumption = row.getCell(3).getNumericCellValue();
                    double rfc = row.getCell(4).getNumericCellValue(); // референтна концентрація
                    double sf = row.getCell(5).getNumericCellValue(); // фактор нахилу
                    int danger_class = (int) row.getCell(7).getNumericCellValue();
                    double tax_rate = TaxCalculation.defineTaxRate(row.getCell(6).getNumericCellValue(),danger_class);

                    int rightCode = 0;
                    // Перевірка, чи існує об'єкт з таким же ім'ям
                    String checkQuery = "SELECT COUNT(*) FROM " + Const.POLLUTANT_TABLE +
                            " WHERE " + Const.POLLUTANT_NAME + " = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, name);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) { //Якщо існує запис із таким же іменем, продовжуємо
                            continue;
                        } else {
                            int maxCode= DB_Handler.getMaxIdFromTable(Const.POLLUTANT_TABLE);
                            rightCode = (DB_Handler.isCodeContains(code)) ? maxCode + 1 : code;
                        }
                    }
                    preparedStatement.setInt(1, rightCode);
                    preparedStatement.setString(2, name);
                    preparedStatement.setDouble(3, gdk);
                    preparedStatement.setDouble(4, mass_consumption);
                    preparedStatement.setDouble(5, rfc);
                    preparedStatement.setDouble(6, sf);
                    preparedStatement.setDouble(7, tax_rate);
                    preparedStatement.setInt(8, danger_class);
                    preparedStatement.executeUpdate();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        refreshTable();
    }

    @FXML
    void exportIntoExcelBtn() {
        exportIntoExcelBtn(pollutantsTable);
    }
    @FXML
    void getAddView() {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/labs/addPollutant.fxml")));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadData() {
        refreshTable();
        codeCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_CODE));
        pollutantNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_NAME));
        massConsumptionCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_MASS_CONSUMPTION));
        gdkCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_GDK));
        rfcCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_RFC));
        sfCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_SF));
        rfcCol.setCellFactory(column -> createDoubleCellFactory());
        sfCol.setCellFactory(column -> createDoubleCellFactory());
        dangerClassCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_CLASS_DANGER));
        taxRateCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_TAX_RATE));
    }

    private TableCell<Pollutant, Double> createDoubleCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(String.format("%.5f", item));
                } else {
                    setText("");
                }
            }
        };
    }

    @FXML
    private void refreshTable() {
        try {
            PollutantsList.clear();

            query = "SELECT * FROM " + Const.POLLUTANT_TABLE;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                PollutantsList.add(new Pollutant(
                        resultSet.getInt(Const.POLLUTANT_CODE),
                        resultSet.getString(Const.POLLUTANT_NAME),
                        resultSet.getDouble(Const.POLLUTANT_MASS_CONSUMPTION),
                        resultSet.getDouble(Const.POLLUTANT_GDK),
                        resultSet.getDouble((Const.POLLUTANT_RFC)),
                        resultSet.getDouble(Const.POLLUTANT_SF),
                        resultSet.getInt(Const.POLLUTANT_CLASS_DANGER),
                        resultSet.getDouble(Const.POLLUTANT_TAX_RATE)));
            }
            pollutantsTable.setItems(PollutantsList);
        } catch (SQLException ex) {
            Logger.getLogger(PollutantController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ObservableList<Pollutant> filterStringData(String filter) {
        ObservableList<Pollutant> filteredList = FXCollections.observableArrayList();
        for (Pollutant item : PollutantsList) {
            filterByPollutantName(filter, filteredList, item);
        }
        return filteredList;
    }
    private void filterByPollutantName(String filter, ObservableList<Pollutant> filteredList, Pollutant item) {
        if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
            filteredList.add(item);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pollutantFilter.setVisible(false);
        pollutantFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            } else {
                pollutantsTable.setItems(filterStringData(newValue));
            }
        });
        loadData();
    }
}
