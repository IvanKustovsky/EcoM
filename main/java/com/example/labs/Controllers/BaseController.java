package com.example.labs.Controllers;

import com.example.labs.DataBase.DataBaseHandler;
import com.example.labs.Main;
import com.example.labs.Models.Enterprise;
import com.example.labs.Models.Pollutant;
import com.example.labs.Models.Pollution;
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
    private static Stage previousStage = Main.innitialStage;

    @FXML
    protected void showAddTip() {
        Tooltip tooltip = new Tooltip("Додати дані");
        Tooltip.install(addDataImage, tooltip);
    }

    @FXML
    protected void showDeleteTip() {
        Tooltip tooltip = new Tooltip("Оновити таблицю");
        Tooltip.install(refreshTableImage, tooltip);
    }

    @FXML
    protected void showEditTip() {
        Tooltip tooltip = new Tooltip("Редагувати дані");
        Tooltip.install(editDataImage, tooltip);
    }

    @FXML
    protected void showRefreshTip() {
        Tooltip tooltip = new Tooltip("Видалити дані");
        Tooltip.install(deleteDataImage, tooltip);
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

    protected boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    @FXML
    void switchToPollutantSceneBtn() {
        if (!isPollutantSceneOpen) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                        getResource("/com/example/labs/pollutant.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Забрудники");
                stage.setScene(new Scene(root1));

                stage.setOnCloseRequest(e -> isPollutantSceneOpen = false);
                //Якщо попередня сцена існує - закриваємо її
                if (previousStage != null) {
                    previousStage.close();
                }

                stage.show();
                isPollutantSceneOpen = true;
                isObjectSceneOpen = false;
                isPollutionSceneOpen = false;

                previousStage = stage;//Зберігаємо посилання на поточну сцену
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    @FXML
    protected void switchToPollutionSceneBtn() {
        if (!isPollutionSceneOpen) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                        getResource("/com/example/labs/pollution.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Забруднення");
                stage.setScene(new Scene(root1));

                stage.setOnCloseRequest(e -> isPollutionSceneOpen = false);

                //Якщо попередня сцена існує - закриваємо її
                if (previousStage != null) {
                    previousStage.close();
                }
                stage.show();
                isPollutionSceneOpen = true;
                isObjectSceneOpen = false;
                isPollutantSceneOpen = false;

                previousStage = stage;//Зберігаємо посилання на поточну сцену
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    protected void switchToObjectSceneBtn()  {
        if (!isObjectSceneOpen) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                        getResource("/com/example/labs/objects.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Підприємства");
                stage.setScene(new Scene(root1));

                stage.setOnCloseRequest(e -> isObjectSceneOpen = false);
                //Якщо попередня сцена існує - закриваємо її
                if (previousStage != null) {
                    previousStage.close();
                }
                stage.show();
                isObjectSceneOpen = true;
                isPollutantSceneOpen = false;
                isPollutionSceneOpen = false;

                previousStage = stage;//Зберігаємо посилання на поточну сцену
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
