package com.dynamsoft.documentscanner;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;

public class HelloController {
    @FXML
    private ComboBox<String> scannersComboBox;
    private List<Scanner> scanners;
    HelloController(){

    }

    private void loadScanners() {

    }

    @FXML
    protected void onScanButtonClicked() {

    }

}