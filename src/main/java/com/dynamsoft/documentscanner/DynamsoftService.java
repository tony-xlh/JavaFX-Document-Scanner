package com.dynamsoft.documentscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamsoftService {
    private String endPoint = "http://127.0.0.1:18622";
    private String license = "";
    public DynamsoftService(){

    }
    public DynamsoftService(String endPoint, String license){
        this.endPoint = endPoint;
        this.license = license;
    }

    public List<Scanner> getScanners() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endPoint+"/DWTAPI/Scanners"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println(body);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String,Object>> parsed = objectMapper.readValue(body,new TypeReference<List<Map<String,Object>>>() {});
        List<Scanner> scanners = new ArrayList<Scanner>();
        for (Map<String,Object> item:parsed) {
            int type = (int) item.get("type");
            String name = (String) item.get("name");
            String device = (String) item.get("device");
            Scanner scanner = new Scanner(name,type,device);
            scanners.add(scanner);
        }
        return scanners;
    }

    public String createScanJob(Scanner scanner) throws Exception {
        return createScanJob(scanner,null,null);
    }

    public String createScanJob(Scanner scanner,DeviceConfiguration config,Capabilities capabilities) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        Map<String,Object> body = new HashMap<String,Object>();
        body.put("license",this.license);
        body.put("device",scanner.device);
        if (config != null) {
            body.put("config",config);
        }
        if (capabilities != null) {
            body.put("caps",capabilities);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endPoint+"/DWTAPI/ScanJobs"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
           return response.body();
        }else{
           throw new Exception(response.body());
        }
    }

    public byte[] nextDocument(String jobID) throws Exception {
        return getImage(jobID);
    }

    private byte[] getImage(String jobID) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endPoint+"/DWTAPI/ScanJobs/"+jobID+"/NextDocument"))
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }
}
