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
import com.example.labs.Models.Pollutant;
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
    private TableColumn<Pollutant, String> pollutantNameCol;
    ObservableList<Pollutant> PollutantsList = FXCollections.observableArrayList();

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
            addPollutantController.setTextField(pollutant.getName(),pollutant.getGdk(),pollutant.getMass_consumption());
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
                "\\НАВЧАННЯ\\5 семестр\\ЕкоМ\\Лаб1"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 Workbook workbook = new XSSFWorkbook(fis);
                 Connection connection = new DataBaseHandler().getDbConnection()) {

                String insert = "INSERT INTO " + Const.POLLUTANT_TABLE + "(" + Const.POLLUTANT_CODE +","
                        + Const.POLLUTANT_NAME + "," + Const.POLLUTANT_GDK + ","
                        + Const.POLLUTANT_MASS_CONSUMPTION + ")" + "VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insert);

                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    int code = (int) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    double gdk = row.getCell(2).getNumericCellValue();
                    double mass_consumption = row.getCell(3).getNumericCellValue();
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
    void getAddView() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/example/labs/addPollutant.fxml"));
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
    }

    @FXML
    private void refreshTable() {
        try {
            PollutantsList.clear();

            query = "SELECT * FROM "+Const.POLLUTANT_TABLE;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                PollutantsList.add(new Pollutant(
                        resultSet.getInt(Const.POLLUTANT_CODE),
                        resultSet.getString(Const.POLLUTANT_NAME),
                        resultSet.getDouble(Const.POLLUTANT_MASS_CONSUMPTION),
                        resultSet.getDouble(Const.POLLUTANT_GDK)));
            }
            pollutantsTable.setItems(PollutantsList);
        } catch (SQLException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadData();
    }
}
