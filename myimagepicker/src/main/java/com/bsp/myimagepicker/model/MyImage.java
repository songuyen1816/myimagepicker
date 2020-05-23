package com.bsp.myimagepicker.model;

import java.io.File;

public class MyImage {
    private String filepath;
    private boolean isPicked;

    public MyImage(String filepath, boolean isPicked) {
        this.filepath = filepath;
        this.isPicked = isPicked;
    }

    public String getFilePath() {
        return filepath;
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
