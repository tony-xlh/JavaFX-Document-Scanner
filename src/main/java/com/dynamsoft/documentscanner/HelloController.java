package com.dynamsoft.documentscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:18622/DWTAPI/Scanners"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println(body);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String,Object>> parsed = objectMapper.readValue(body,new TypeReference<List<Map<String,Object>>>() {});
        for (Map<String,Object> item:parsed) {
            int type = (int) item.get("type");
            String name = (String) item.get("name");
            String device = (String) item.get("device");
            Scanner scanner = new Scanner(name,type,device);
            scanners.add(scanner);
        }
        List<String> names = new ArrayList<String>();
        for (Scanner scanner:scanners) {
            names.add(scanner.getName());
        }
        scannersComboBox.setItems(FXCollections.observableList(names));
    }

    @FXML
    protected void onScanButtonClicked() {
        System.out.println("test");
    }

}