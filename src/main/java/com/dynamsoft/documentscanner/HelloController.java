package com.dynamsoft.documentscanner;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    private ComboBox<String> scannersComboBox;
    private List<Scanner> scanners = new ArrayList<Scanner>();
    public void initialize(){
        try {
            this.loadScanners();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadScanners() throws IOException, InterruptedException {
        DynamsoftService service = new DynamsoftService();
        scanners = service.getScanners();
        List<String> names = new ArrayList<String>();
        for (Scanner scanner:scanners) {
            try {
                names.add(scanner.getName() + " (" +DeviceType.getDisplayName(scanner.getType())+ ")");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        scannersComboBox.setItems(FXCollections.observableList(names));
    }

    @FXML
    protected void onScanButtonClicked() {
        System.out.println("test");
    }

}