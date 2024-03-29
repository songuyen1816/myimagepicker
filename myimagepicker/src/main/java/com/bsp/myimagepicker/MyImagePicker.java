package com.bsp.myimagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
        clearData(activity.getApplicationContext());
        showDialog(activity);
    }

    public void start(Fragment fragment) {
        clearData(fragment.getContext());
        showDialog(fragment);
    }

    public void takePhoto(Activity activity) {
        goTakePhoto(activity);
    }

    public void takePhoto(Fragment fragment) {
        goTakePhoto(fragment);
    }

    public void pickImage(Activity activity) {
        goPickImage(activity);
    }

    public void pickImage(Fragment fragment) {
        goPickImage(fragment);
    }

    private void showDialog(Activity context) {
        alertDialog = new AlertDialog.Builder(context).setMessage("Pick from:")
                .setPositiveButton("Camera", (dialog, which) -> {
                    goTakePhoto(context);
                })
                .setNegativeButton("Gallery", (dialog, which) -> {
                    goPickImage(context);
                }).create();

        alertDialog.show();

    }

    private void showDialog(Fragment context) {
        alertDialog = new AlertDialog.Builder(context.requireContext()).setMessage("Pick from:")
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

    private void clearData(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        File directory = PickerUtils.getExternalStoragePath(context.getApplicationContext());
        if (!directory.exists()) {
            return;
        } else if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (!file.isDirectory()) file.delete();
            }
        }
    }
}
