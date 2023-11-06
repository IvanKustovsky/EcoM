module com.example.labs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires poi;
    requires poi.ooxml;
    requires mysql.connector.j;
    requires fontawesomefx;

    opens com.example.labs;
    opens com.example.labs.Controllers to javafx.fxml;
    opens com.example.labs.Models to javafx.base;
    exports com.example.labs.Controllers;
    exports com.example.labs;
    exports com.example.labs.DataBase;
    opens com.example.labs.DataBase to javafx.fxml;
    exports com.example.labs.Services;
    opens com.example.labs.Services;
}