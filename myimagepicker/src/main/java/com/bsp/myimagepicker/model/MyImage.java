package com.bsp.myimagepicker.model;

import android.net.Uri;

public class MyImage {
    private Uri uri;
    private boolean isPicked;


    public MyImage(Uri uri, boolean isPicked) {
        this.uri = uri;
        this.isPicked = isPicked;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }
}
