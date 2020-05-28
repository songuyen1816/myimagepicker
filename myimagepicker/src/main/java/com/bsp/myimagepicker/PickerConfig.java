package com.bsp.myimagepicker;

import android.graphics.Color;

import java.io.Serializable;

public class PickerConfig implements Serializable {
    public static int IMAGE_PICKER_REQUEST = 1816;
    public static String CONFIG_BUNDLE_KEY = "PICKER_CONFIG";
    public static String FILE_PATH_DATA = "FILE_PATH_DATA";

    private String pickerTitle;
    private int styleColor;
    private boolean isMultiPicker;
    private boolean isCompressed;
    private int maxCount;

    public String getPickerTitle() {
        return pickerTitle;
    }

    public int getStyleColor() {
        return styleColor;
    }

    public boolean isMultiPicker() {
        return isMultiPicker;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    private PickerConfig(Builder builder) {
        this.pickerTitle = builder.pickerTitle;
        this.styleColor = builder.pickerToolbarColor;
        this.isMultiPicker = builder.isMultiPicker;
        this.maxCount = builder.maxCount;
        this.isCompressed = builder.isCompressed;
    }

    public static class Builder{
        private String pickerTitle = "Pick an image";
        private int pickerToolbarColor = Color.parseColor("#3498db");
        private boolean isMultiPicker = false;
        private boolean isCompressed = false;
        private int maxCount = 1;

        public Builder setPickerTitle(String pickerTitle){
            this.pickerTitle = pickerTitle;
            return this;
        }

        public Builder setStyleColor(int pickerToolbarColor){
            this.pickerToolbarColor = pickerToolbarColor;
            return this;
        }

        public Builder setMultiPicker(boolean isMultiPicker){
            this.isMultiPicker = isMultiPicker;
            return this;
        }

        public Builder setCompressed(boolean isCompressed){
            this.isCompressed = isCompressed;
            return this;
        }

        public Builder setMaxCount(int maxCount){
            this.maxCount = maxCount;
            return this;
        }

        public PickerConfig build(){
            return new PickerConfig(this);
        }
    }
}
