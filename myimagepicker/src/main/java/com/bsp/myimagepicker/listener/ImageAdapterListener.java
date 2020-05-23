package com.bsp.myimagepicker.listener;

public interface ImageAdapterListener {
    void onImagePicked(String path);
    void onImageUnPicked(String path);
}
