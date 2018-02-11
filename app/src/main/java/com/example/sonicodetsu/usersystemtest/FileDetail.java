package com.example.sonicodetsu.usersystemtest;

/**
 * Created by Sonicodetsu on 10/2/2561.
 */

public class FileDetail {
    private String filename;
    private String filelocation;

    public FileDetail(String filename, String filelocation) {
        this.filename = filename;
        this.filelocation = filelocation;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilelocation() {
        return filelocation;
    }

    public void setFilelocation(String filelocation) {
        this.filelocation = filelocation;
    }
}
