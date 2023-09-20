package com.dynamsoft.documentscanner;

public class Scanner {
    private String name;
    private int type;
    private String device;
    Scanner(String name, int type, String device){
        this.name = name;
        this.type = type;
        this.device = device;
    }

    public int getType() {
        return type;
    }

    public String getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }
}
