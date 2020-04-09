package com.drawchat.converterservice;


//This class represents model for Files
public class FileInfo {
    private String filename;
    private String url;

    public FileInfo(String filename, String url) {
        this.filename = filename;
        this.url = url;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
