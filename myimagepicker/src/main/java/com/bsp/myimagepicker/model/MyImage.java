package com.bsp.myimagepicker.model;

import android.net.Uri;

import java.io.File;

public class MyImage {
    private Uri uri;
    private String filepath;
    private boolean isPicked;

    public MyImage(String filepath, boolean isPicked) {
        this.filepath = filepath;
        this.isPicked = isPicked;
    }

    public MyImage(Uri uri, boolean isPicked) {
        this.uri = uri;
        this.isPicked = isPicked;
    }

    public String getFilePath() {
        return filepath;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setFilePath(File file) {
        this.filepath = filepath;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }
}
