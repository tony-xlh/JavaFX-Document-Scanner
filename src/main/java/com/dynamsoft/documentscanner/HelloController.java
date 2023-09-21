package com.dynamsoft.documentscanner;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    private ComboBox<String> scannersComboBox;
    @FXML
    private ListView documentListView;
    @FXML
    private CheckBox showUICheckBox;
    @FXML
    private CheckBox duplexCheckBox;
    @FXML
    private CheckBox ADFCheckBox;
    @FXML
    private ComboBox resolutionComboBox;
    private List<Scanner> scanners = new ArrayList<Scanner>();
    private DynamsoftService service = new DynamsoftService("http://127.0.0.1:18622","t0068MgAAAEm8KzOlKD/AG56RuTf2RSTo4ajLgVpDBfQkmIJYY7yrDj3jbzQpRfQRzGnACr7S1F/7Da6REO20jmF3QR4VDXI=");
    public void initialize(){
        try {
            this.loadResolutions();
            this.loadScanners();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadResolutions(){
        List<Integer> resolutions = new ArrayList<Integer>();
        resolutions.add(100);
        resolutions.add(200);
        resolutions.add(300);
        resolutionComboBox.setItems(FXCollections.observableList(resolutions));
        resolutionComboBox.getSelectionModel().select(1);
    }

    private void loadScanners() throws IOException, InterruptedException {
        scanners = service.getScanners();
        List<String> names = new ArrayList<String>();
        for (Scanner scanner:scanners) {
            try {
                names.add(scanner.name + " (" +DeviceType.getDisplayName(scanner.type)+ ")");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        scannersComboBox.setItems(FXCollections.observableList(names));
    }

    @FXML
    protected void onScanButtonClicked() {
        int selectedIndex = scannersComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Scanner scanner  = scanners.get(selectedIndex);
            try {
                DeviceConfiguration config = new DeviceConfiguration();
                config.IfShowUI = showUICheckBox.isSelected();
                config.IfDuplexEnabled = duplexCheckBox.isSelected();
                config.IfFeederEnabled = ADFCheckBox.isSelected();
                config.Resolution = (int) resolutionComboBox.getSelectionModel().getSelectedItem();
                String jobID = service.createScanJob(scanner,config);
                System.out.println("ID: "+jobID);
                byte[] image = service.nextDocument(jobID);
                while (image != null){
                    loadImage(image);
                    image = service.nextDocument(jobID);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void loadImage(byte[] image){
        System.out.println("image: "+image.length);
        System.out.println(image.length);
        Image img = new Image(new ByteArrayInputStream(image));
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitWidth(documentListView.getWidth());
        iv.setImage(img);
        documentListView.getItems().add(iv);
    }

}