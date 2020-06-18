package com.bsp.myimagepicker;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bsp.myimagepicker.features.imagepicker.PickerActivity;
import com.bsp.myimagepicker.features.takephoto.CameraActivity;

import java.io.File;

public class MyImagePicker {
    private PickerConfig pickerConfig;
    private AlertDialog alertDialog;

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

    public void start(Activity activity) {
        clearData();
        showDialog(activity);
    }

    public void start(Fragment fragment) {
        clearData();
        showDialog(fragment);
    }

    private void showDialog(Activity context) {
        alertDialog = new AlertDialog.Builder(context).setMessage("Pick image from:")
                .setPositiveButton("Camera", (dialog, which) -> {
                    goTakePhoto(context);
                })
                .setNegativeButton("Gallery", (dialog, which) -> {
                    goPickImage(context);
                }).create();

        alertDialog.show();

    }

    private void showDialog(Fragment context) {
        alertDialog = new AlertDialog.Builder(context.getContext()).setMessage("Pick image from:")
                .setPositiveButton("Camera", (dialog, which) -> {
                    goTakePhoto(context);
                })
                .setNegativeButton("Gallery", (dialog, which) -> {
                    goPickImage(context);
                }).create();

        alertDialog.show();

    }

    private void goTakePhoto(Activity activity) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(activity, CameraActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        activity.startActivityForResult(i, PickerConfig.IMAGE_PICKER_REQUEST);
    }

    private void goTakePhoto(Fragment fragment) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(fragment.getContext(), CameraActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        fragment.startActivityForResult(i, PickerConfig.IMAGE_PICKER_REQUEST);
    }

    private void goPickImage(Activity activity) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(activity, PickerActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        activity.startActivityForResult(i, PickerConfig.IMAGE_PICKER_REQUEST);
    }

    private void goPickImage(Fragment fragment) {
        if (pickerConfig == null) pickerConfig = new PickerConfig.Builder().build();
        Intent i = new Intent(fragment.getContext(), PickerActivity.class);
        i.putExtra(PickerConfig.CONFIG_BUNDLE_KEY, pickerConfig);
        fragment.startActivityForResult(i, PickerConfig.IMAGE_PICKER_REQUEST);
    }

    private void clearData(){
        File directory = new File(PickerConfig.DEFAULT_DIRECTORY);
        if(!directory.exists()){
            return;
        } else if (directory.isDirectory()){
            for (File file : directory.listFiles()){
                if(!file.isDirectory()) file.delete();
            }
        }
    }
}
