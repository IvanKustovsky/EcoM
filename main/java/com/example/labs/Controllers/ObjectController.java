package com.example.labs.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.labs.DataBase.Const;
import com.example.labs.Models.Enterprise;
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

public class ObjectController extends BaseController implements Initializable {

    @FXML
    protected TableView<Enterprise> objectsTable;
    @FXML
    private TableColumn<Enterprise, String> descriptionCol;
    @FXML
    private TableColumn<Enterprise, String> idCol;
    @FXML
    private TableColumn<Enterprise, String> locationCol;
    @FXML
    private TableColumn<Enterprise, String> objectNameCol;
    @FXML
    private TextField enterpriseFilter;
    ObservableList<Enterprise> EnterprisesList = FXCollections.observableArrayList();

    @FXML
    void deleteData() {
        enterprise = objectsTable.getSelectionModel().getSelectedItem();
        if (enterprise != null) {
            EnterprisesList = objectsTable.getItems();
            try {
                String deleteQuery = "DELETE FROM " + Const.OBJECT_TABLE + " WHERE " + Const.OBJECT_ID +" = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, enterprise.getId());
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
        enterprise = objectsTable.getSelectionModel().getSelectedItem();
        if (enterprise != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/labs/addEnterprise.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
            }

            AddEnterpriseController addEnterpriseController = loader.getController();
            addEnterpriseController.setUpdate(true);
            addEnterpriseController.setTextField(enterprise.getNameObject(), enterprise.getLocation(),
                    enterprise.getDescription());
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
    void getAddView() {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/labs/addEnterprise.fxml")));
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
    void importExcelData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\user\\Desktop" +
                "\\НАВЧАННЯ\\5 семестр\\ЕкоМ\\Лаб2"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                String insert = "INSERT INTO " + Const.OBJECT_TABLE + "(" + Const.OBJECT_ID + "," + Const.OBJECT_NAME + "," +
                        Const.OBJECT_LOCATION + "," + Const.OBJECT_DESCRIPTION + ")" +
                        "VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insert);

                Sheet sheet = workbook.getSheetAt(0);
                // Починаємо читання з другого рядка (індекс 1)
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row.getCell(i) != null) {
                        int id = (int) row.getCell(0).getNumericCellValue();
                        String name = row.getCell(1).getStringCellValue();
                        String location = row.getCell(2).getStringCellValue();
                        String description = row.getCell(3).getStringCellValue();
                        int rightId = 0;
                        // Перевірка, чи існує об'єкт за іменем та локацією
                        String checkQuery = "SELECT COUNT(*) FROM " + Const.OBJECT_TABLE +
                                " WHERE " + Const.OBJECT_NAME + " = ? AND " +
                                Const.OBJECT_LOCATION + " = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                        checkStatement.setString(1, name);
                        checkStatement.setString(2, location);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (resultSet.next()) {
                            int count = resultSet.getInt(1);
                            if (count > 0) { //Якщо існує запис із таким же іменем та локацією, продовжуємо
                                continue;
                            } else {
                                int maxId = DB_Handler.getMaxIdFromTable(Const.OBJECT_TABLE);
                                rightId = (DB_Handler.isIdContains(id)) ? maxId + 1 : id;
                            }
                        }
                        preparedStatement.setInt(1,rightId);
                        preparedStatement.setString(2, name);
                        preparedStatement.setString(3, location);
                        preparedStatement.setString(4, description);
                        preparedStatement.executeUpdate();
                    }
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
        exportIntoExcelBtn(objectsTable);
    }

    private void loadData() {
        refreshTable();
        idCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_ID));
        objectNameCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_NAME));
        locationCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_LOCATION));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>(Const.OBJECT_DESCRIPTION));
    }

    @FXML
    private void refreshTable() {
        try {
            EnterprisesList.clear();
            query = "SELECT * FROM "+Const.OBJECT_TABLE;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                EnterprisesList.add(new Enterprise(
                        resultSet.getInt(Const.OBJECT_ID),
                        resultSet.getString(Const.OBJECT_NAME),
                        resultSet.getString(Const.OBJECT_LOCATION),
                        resultSet.getString(Const.OBJECT_DESCRIPTION)));
                objectsTable.setItems(EnterprisesList);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private ObservableList<Enterprise> filterStringData(String filter) {
        ObservableList<Enterprise> filteredList = FXCollections.observableArrayList();
        for (Enterprise item : EnterprisesList) {
                filterByEnterpriseName(filter, filteredList, item);
        }
        return filteredList;
    }
    private void filterByEnterpriseName(String filter, ObservableList<Enterprise> filteredList, Enterprise item) {
        if (item.getNameObject().toLowerCase().contains(filter.toLowerCase())) {
            filteredList.add(item);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        enterpriseFilter.setVisible(false);
        enterpriseFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadData(); // Повернення до старого вигляду, якщо текст у полі пустий
            } else {
                objectsTable.setItems(filterStringData(newValue));
            }
        });

        loadData();
    }
}
