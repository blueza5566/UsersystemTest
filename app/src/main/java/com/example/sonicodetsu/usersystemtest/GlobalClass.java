package com.example.sonicodetsu.usersystemtest;

import android.app.Application;

import java.io.File;
import java.util.Vector;

/**
 * Created by Sonicodetsu on 4/2/2561.
 */

public class GlobalClass extends Application {
    Vector<User> list = new Vector<User>();
    Vector<FileDetail> filelist = new Vector<FileDetail>();
    String userid;


    public GlobalClass() {
    }

    public Vector<User> getList() {
        return list;
    }

    public Vector<FileDetail> getFileList() {
        return filelist;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
