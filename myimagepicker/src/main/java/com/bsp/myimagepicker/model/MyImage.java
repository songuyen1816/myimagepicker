package com.bsp.myimagepicker.model;

import android.net.Uri;

public class MyImage {
    private Uri uri;
    private Long dateTaken;
    private boolean isPicked;
    private boolean isVideo;


    public MyImage(Uri uri, Long dateTaken, boolean isPicked, boolean isVideo) {
        this.dateTaken = dateTaken;
        this.uri = uri;
        this.isPicked = isPicked;
        this.isVideo = isVideo;
    }

    public Long getDateTaken() {
        return dateTaken;
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

    public boolean isVideo() {
        return isVideo;
    }
}
