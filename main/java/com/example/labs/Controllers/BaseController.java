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
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BaseController {
    @FXML
    protected Button importExcelFileBtn;
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
    protected String query = null;
    protected PreparedStatement preparedStatement = null ;
    protected ResultSet resultSet = null ;
    protected DataBaseHandler DB_Handler = new DataBaseHandler();
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
