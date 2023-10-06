package com.example.labs.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableColumn<Pollution, String> enterpriseLocationCol;
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
                    pollution.getValue_pollution(),pollution.getYear());
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
                "\\НАВЧАННЯ\\5 семестр\\ЕкоМ\\Лаб1"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 Workbook workbook = new XSSFWorkbook(fis);
                 Connection connection = new DataBaseHandler().getDbConnection()) {
                String insert = "INSERT INTO " + Const.POLLUTION_TABLE + "(" + Const.POLLUTION_OBJECT_ID + "," +
                        Const.POLLUTION_CODE_POLLUTANT + "," + Const.POLLUTION_VALUE + "," + Const.POLLUTION_YEAR + ")" +
                        "VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insert);

                Sheet sheet = workbook.getSheetAt(0);
                // Починаємо читання з другого рядка (індекс 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    double id_object = row.getCell(0).getNumericCellValue();
                    double code_pollutant = row.getCell(1).getNumericCellValue();
                    double value_pollution = row.getCell(2).getNumericCellValue();
                    double year = row.getCell(3).getNumericCellValue();
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
                    preparedStatement.setDouble(4, year);

                    preparedStatement.executeUpdate();
                }

                System.out.println("Data imported successfully!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        refreshTable();
    }

    private void loadData() {
        refreshTable();
        idCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_ID));
        enterpriseNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_NAME));
        enterpriseLocationCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_LOCATION));
        pollutantNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTANT_NAME));
        valuePollutionCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_VALUE));
        yearCol.setCellValueFactory(new PropertyValueFactory<>(Const.POLLUTION_YEAR));
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
                String enterpriseLocation = dataBaseHandler.getTableColumnById(Const.OBJECT_TABLE,Const.OBJECT_LOCATION,object_id);
                String pollutantName = dataBaseHandler.getTableColumnById(Const.POLLUTANT_TABLE,Const.POLLUTANT_NAME,code);
                double pollutionValue = resultSet.getDouble(Const.POLLUTION_VALUE);
                int pollutionYear = resultSet.getInt(Const.POLLUTION_YEAR);

                PollutionsList.add(new Pollution(pollutionId, enterpriseName, enterpriseLocation, pollutantName, pollutionValue, pollutionYear));

            }
            pollutionTable.setItems(PollutionsList);
        } catch (SQLException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadData();
    }
}

