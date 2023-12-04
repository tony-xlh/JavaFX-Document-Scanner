package com.dynamsoft.documentscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class DynamsoftService {
    public String endPoint = "http://127.0.0.1:18622";
    public String license = "";
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public DynamsoftService(){

    }
    public DynamsoftService(String endPoint, String license){
        this.endPoint = endPoint;
        this.license = license;
    }

    public List<Scanner> getScanners() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(endPoint+"/DWTAPI/Scanners")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            List<Scanner> scanners = new ArrayList<Scanner>();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String,Object>> parsed = objectMapper.readValue(body,new TypeReference<List<Map<String,Object>>>() {});

            for (Map<String,Object> item:parsed) {
                int type = (int) item.get("type");
                String name = (String) item.get("name");
                String device = (String) item.get("device");
                Scanner scanner = new Scanner(name,type,device);
                scanners.add(scanner);
            }
            return scanners;
        }
    }

    public String createScanJob(Scanner scanner) throws Exception {
        return createScanJob(scanner,null,null);
    }

    public String createScanJob(Scanner scanner,DeviceConfiguration config,Capabilities capabilities) throws Exception {
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

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(endPoint+"/DWTAPI/ScanJobs?timeout=120")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 201) {
                return response.body().string();
            }else{
                throw new Exception(response.body().string());
            }
        }
    }

    public byte[] nextDocument(String jobID) throws Exception {
        return getImage(jobID);
    }

    private byte[] getImage(String jobID) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(endPoint+"/DWTAPI/ScanJobs/"+jobID+"/NextDocument?timeout=120")
                .build();
        String body = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                return response.body().bytes();
            }else{
                return null;
            }

        }
    }
}
