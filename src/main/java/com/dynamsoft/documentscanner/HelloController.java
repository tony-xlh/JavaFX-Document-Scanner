package com.dynamsoft.documentscanner;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @FXML
    private ComboBox pixelTypeComboBox;
    private List<Scanner> scanners = new ArrayList<Scanner>();
    private DynamsoftService service = new DynamsoftService("http://127.0.0.1:18622","t0068MgAAAEm8KzOlKD/AG56RuTf2RSTo4ajLgVpDBfQkmIJYY7yrDj3jbzQpRfQRzGnACr7S1F/7Da6REO20jmF3QR4VDXI=");
    private Map<Object,byte[]> rawBytesOfImages = new HashMap<Object,byte[]>();
    public void initialize(){
        try {
            this.loadResolutions();
            this.loadPixelTypes();
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

    private void loadPixelTypes(){
        List<String> pixelTypes = new ArrayList<String>();
        pixelTypes.add("Black & White");
        pixelTypes.add("Gray");
        pixelTypes.add("Color");
        pixelTypeComboBox.setItems(FXCollections.observableList(pixelTypes));
        pixelTypeComboBox.getSelectionModel().select(0);
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
                Capabilities caps = new Capabilities();
                caps.exception = "ignore";
                caps.capabilities = new ArrayList<CapabilitySetup>();
                CapabilitySetup pixelTypeSetup = new CapabilitySetup();
                pixelTypeSetup.capability = 257;
                pixelTypeSetup.curValue = pixelTypeComboBox.getSelectionModel().getSelectedIndex();
                caps.capabilities.add(pixelTypeSetup);
                String jobID = service.createScanJob(scanner,config,caps);
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
        rawBytesOfImages.put(iv,image);
        documentListView.getItems().add(iv);
    }

    @FXML
    protected void onSaveButtonClicked() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File fileToSave = fileChooser.showSaveDialog(null);
        if (fileToSave != null) {
            PDDocument document = new PDDocument();
            int index = 0;
            for (Object iv: documentListView.getItems()) {
                if (iv.getClass().toString().indexOf("ImageView") != -1){
                    index = index + 1;

                    ImageView imageView = (ImageView) iv;
                    PDRectangle rect = new PDRectangle((float) imageView.getImage().getWidth(),(float) imageView.getImage().getHeight());
                    System.out.println(rect);
                    PDPage page = new PDPage(rect);
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    PDImageXObject image
                            = PDImageXObject.createFromByteArray(document,rawBytesOfImages.get(imageView),String.valueOf(index));
                    contentStream.drawImage(image, 0, 0);
                    contentStream.close();
                }
            }
            document.save(fileToSave.getAbsolutePath());
            document.close();
        }
    }

}