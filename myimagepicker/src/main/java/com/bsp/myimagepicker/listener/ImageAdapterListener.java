package com.bsp.myimagepicker.listener;

import android.net.Uri;

import com.bsp.myimagepicker.model.MyImage;

public interface ImageAdapterListener {
    void onImagePicked(MyImage myImage);
    void onImageUnPicked(MyImage myImage);
}
