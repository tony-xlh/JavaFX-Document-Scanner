module com.dynamsoft.documentscanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dynamsoft.documentscanner to javafx.fxml;
    exports com.dynamsoft.documentscanner;
}