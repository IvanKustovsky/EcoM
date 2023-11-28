package com.example.labs.Controllers;

import com.example.labs.DataBase.DataBaseHandler;
import com.example.labs.Main;
import com.example.labs.Models.Enterprise;
import com.example.labs.Models.Pollutant;
import com.example.labs.Models.Pollution;
import com.example.labs.Models.Tax;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BaseController {
    @FXML
    protected Button importExcelFileBtn;
    @FXML
    protected Button exportIntoExcelBtn;
    @FXML
    protected Button objectBtn;
    @FXML
    protected Button pollutantBtn;
    @FXML
    protected Button pollutionBtn;
    @FXML
    protected Button taxesBtn;
    @FXML
    protected ImageView addDataImage;
    @FXML
    protected ImageView refreshTableImage;
    @FXML
    protected ImageView editDataImage;
    @FXML
    protected ImageView deleteDataImage;
    @FXML
    protected TextField enterpriseFilter;
    @FXML
    protected TextField idFilter;
    @FXML
    protected TextField pollutantFilter;
    @FXML
    protected TextField yearFilter;
    @FXML
    protected Button showHideBtn;
    protected String query = null;
    protected PreparedStatement preparedStatement = null ;
    protected ResultSet resultSet = null ;
    protected static DataBaseHandler DB_Handler = DataBaseHandler.getInstance();
    protected Connection connection = DB_Handler.getDbConnection();
    protected static Enterprise enterprise = null;
    protected static Pollutant pollutant = null;
    protected static Pollution pollution = null;
    protected static boolean isObjectSceneOpen = false;
    protected static boolean isPollutionSceneOpen = false;
    protected static boolean isPollutantSceneOpen = false;
    protected static boolean isTaxesSceneOpen = false;
    protected static ObservableList<Pollution> PollutionsList = FXCollections.observableArrayList();
    protected static ObservableList<Tax> TaxesList = FXCollections.observableArrayList();
    protected static ObservableList<Pollutant> PollutantsList = FXCollections.observableArrayList();
    protected static ObservableList<Enterprise> EnterprisesList = FXCollections.observableArrayList();
    protected final Alert alert = new Alert(Alert.AlertType.ERROR);
    private static Stage previousStage = Main.innitialStage;

    protected double getDoubleValue(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            showAlert("Помилка", fieldName + " не є числом!");
            return -1; // або інше значення за замовчуванням
        }
    }
    protected void showAlert(String header, String content) {
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    protected void showAddTip() {
        Tooltip tooltip = new Tooltip("Додати дані");
        Tooltip.install(addDataImage, tooltip);
    }

    @FXML
    protected void showDeleteTip() {
        Tooltip tooltip = new Tooltip("Видалити дані");
        Tooltip.install(deleteDataImage, tooltip);
    }

    @FXML
    protected void showEditTip() {
        Tooltip tooltip = new Tooltip("Редагувати дані");
        Tooltip.install(editDataImage, tooltip);
    }

    @FXML
    protected void showRefreshTip() {
        Tooltip tooltip = new Tooltip("Оновити таблицю");
        Tooltip.install(refreshTableImage, tooltip);
    }
    @FXML
    protected void showFilters() {
        if (showHideBtn.getText().equals("Show Filters")) {
            if(idFilter != null){idFilter.setVisible(true);}
            if(enterpriseFilter != null){enterpriseFilter.setVisible(true);}
            if(pollutantFilter != null){pollutantFilter.setVisible(true);}
            if(yearFilter != null){yearFilter.setVisible(true);}
            showHideBtn.setText("Hide Filters");
        } else{
            if(idFilter != null){idFilter.setVisible(false); idFilter.setText("");}
            if(enterpriseFilter != null){enterpriseFilter.setVisible(false); enterpriseFilter.setText("");}
            if(pollutantFilter != null){pollutantFilter.setVisible(false); pollutantFilter.setText("");}
            if(yearFilter != null){yearFilter.setVisible(false); yearFilter.setText("");}
            showHideBtn.setText("Show Filters");
        }
    }
    protected void exportIntoExcelBtn(TableView<?> table){
        FileChooser fileChooser = new FileChooser();

        // Виберіть розширення файлу за замовчуванням
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показати діалогове вікно для вибору файлу
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Дані з таблиці");

                // Створення першого рядка з назвами стовпців
                XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumns().size(); i++) {
                    TableColumn<?, ?> col = table.getColumns().get(i);
                    headerRow.createCell(i).setCellValue(col.getText());
                }

                // Заповнення решти даними
                for (int i = 0; i < table.getItems().size(); i++) {
                    XSSFRow row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumns().size(); j++) {
                        TableColumn<?, ?> col = table.getColumns().get(j);
                        String cellValue = col.getCellData(i).toString();
                        row.createCell(j).setCellValue(cellValue);
                    }
                }
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void openScene(String title, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root1));

            // Якщо попередня сцена існує - закриваємо її
            if (previousStage != null) {
                previousStage.close();
            }

            stage.show();
            previousStage = stage; // Зберігаємо посилання на поточну сцену
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToPollutantSceneBtn() {
        if (!isPollutantSceneOpen) {
            openScene("Забрудники", "/com/example/labs/pollutant.fxml");
            isPollutantSceneOpen = true;
            isObjectSceneOpen = false;
            isPollutionSceneOpen = false;
            isTaxesSceneOpen = false;
        }
    }

    @FXML
    void switchToTaxesSceneBtn() {
        if (!isTaxesSceneOpen) {
            openScene("Податки", "/com/example/labs/taxes.fxml");
            isTaxesSceneOpen = true;
            isPollutantSceneOpen = false;
            isObjectSceneOpen = false;
            isPollutionSceneOpen = false;
        }
    }

    @FXML
    protected void switchToPollutionSceneBtn() {
        if (!isPollutionSceneOpen) {
            openScene("Забруднення", "/com/example/labs/pollution.fxml");
            isPollutionSceneOpen = true;
            isPollutantSceneOpen = false;
            isObjectSceneOpen = false;
            isTaxesSceneOpen = false;
        }
    }

    @FXML
    protected void switchToObjectSceneBtn() {
        if (!isObjectSceneOpen) {
            openScene("Підприємства", "/com/example/labs/objects.fxml");
            isObjectSceneOpen = true;
            isPollutantSceneOpen = false;
            isPollutionSceneOpen = false;
            isTaxesSceneOpen = false;
        }
    }
}
