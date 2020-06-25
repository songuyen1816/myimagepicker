package com.bsp.myimagepicker.listener;

import android.net.Uri;

public interface ImageAdapterListener {
    void onImagePicked(Uri uri);
    void onImageUnPicked(Uri uri);
}
