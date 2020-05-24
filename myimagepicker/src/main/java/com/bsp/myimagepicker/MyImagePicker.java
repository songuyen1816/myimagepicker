package com.bsp.myimagepicker;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class MyImagePicker {
    private PickerConfig pickerConfig;

    public MyImagePicker setPickerConfig(PickerConfig pickerConfig) {
        this.pickerConfig = pickerConfig;
        return this;
    }

    private static volatile MyImagePicker mInstance;

    public static MyImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (MyImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new MyImagePicker();
                }
            }
        }
        return mInstance;
    }

    public void start(Activity activity, int requestCode) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(activity, PickerActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        activity.startActivityForResult(i, requestCode);
    }

    public void start(Activity activity) {
        start(activity, PickerConfig.IMAGE_PICKER_REQUEST);
    }

    public void start(Fragment fragment, int requestCode) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(fragment.getContext(), PickerActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        fragment.startActivityForResult(i, requestCode);
    }

    public void start(Fragment fragment) {
        start(fragment, PickerConfig.IMAGE_PICKER_REQUEST);
    }
}
