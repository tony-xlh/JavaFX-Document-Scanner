module com.dynamsoft.documentscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.dynamsoft.documentscanner to javafx.fxml;
    exports com.dynamsoft.documentscanner;
}