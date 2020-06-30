package com.bsp.myimagepicker.features.takephoto;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.bsp.myimagepicker.PickerConfig;
import com.bsp.myimagepicker.PickerUtils;
import com.bsp.myimagepicker.R;
import com.bsp.myimagepicker.base.BaseActivity;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends BaseActivity {

    private final int PERMISSION_REQUEST = 111;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private View btnTakePhoto, btnFlipCamera;
    private Camera camera;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;

    private OrientationEventListener orientationEventListener;
    private String mCurrentFilePath = "";

    private PickerConfig currentConfig;
    private Dialog loadingDialog;
    private ProcessCameraProvider cameraProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setStatusBarColor(Color.BLACK);
        currentConfig = (PickerConfig) getIntent()
                .getSerializableExtra(PickerConfig.CONFIG_BUNDLE_KEY);

        initControls();
        initEvents();
        checkPermissions();
    }

    private void initControls() {
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnFlipCamera = findViewById(R.id.btn_flip_camera);
        loadingDialog = PickerUtils.createLoadingDialog(this);
        previewView = findViewById(R.id.preview_view);
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
    }

    private void openCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                //Ignore
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview() {
//        DisplayMetrics metrics = new DisplayMetrics();
//        Display display = getWindowManager().getDefaultDisplay();
//        display.getRealMetrics(metrics);

        Preview preview = new Preview.Builder().setTargetRotation(Surface.ROTATION_0)
//                .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
//                .setTargetAspectRatio(aspectRatio(metrics.widthPixels, metrics.heightPixels))
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
//                .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
//                .setTargetAspectRatio(aspectRatio(metrics.widthPixels, metrics.heightPixels))
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .build();


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        cameraProvider.unbindAll();

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
    }

    private void initEvents() {
        btnTakePhoto.setOnClickListener(v -> {
            captureImage();
        });

        btnFlipCamera.setOnClickListener(v -> {
            if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                lensFacing = CameraSelector.LENS_FACING_FRONT;
            } else {
                lensFacing = CameraSelector.LENS_FACING_BACK;
            }
            bindPreview();
        });
    }

    private void captureImage() {
        loadingDialog.show();

        File directory = PickerUtils.getExternalStoragePath(getApplicationContext());
        if (!directory.exists()) {
            directory.mkdir();
        }

        mCurrentFilePath = directory + "/" + UUID.randomUUID().toString() + ".jpg";

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(new File(mCurrentFilePath)).build();

        imageCapture.takePicture(outputFileOptions, executorService, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    File imageFile = new File(mCurrentFilePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    Bitmap rotatedBitmap = PickerUtils.checkRotateBitmapFrontCamera(imageFile.getAbsolutePath(), bitmap);
                    if (rotatedBitmap != null) {
                        Bitmap flippedBitmap = PickerUtils.flipBitmap(rotatedBitmap);
                        mCurrentFilePath = directory + "/" + System.currentTimeMillis() + ".jpg";
                        PickerUtils.saveBitmapToFile(mCurrentFilePath, flippedBitmap);
                    } else {
                        Toast.makeText(CameraActivity.this, "There is an error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                returnData();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("ERROR", Objects.requireNonNull(exception.getLocalizedMessage()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if ((orientation >= 70 && orientation <= 120)
                        || ((orientation >= 340 && orientation <= 360) || (orientation >= 0 && orientation <= 20))
                        || (orientation >= 160 && orientation <= 200)
                        || (orientation >= 250 && orientation <= 290)) {

                    if (imageCapture != null) {
                        imageCapture.setTargetRotation(getOrientation(orientation));
                    }
                }
            }
        };
        orientationEventListener.enable();
    }

    private int getOrientation(int orientation) {
        if (orientation >= 70 && orientation <= 120) {
            return Surface.ROTATION_270;
        }

        if ((orientation >= 340 && orientation <= 360) || (orientation >= 0 && orientation <= 20)) {
            return Surface.ROTATION_0;
        }

        if (orientation >= 160 && orientation <= 200) {
            return Surface.ROTATION_180;
        }

        if (orientation >= 250 && orientation <= 290) {
            return Surface.ROTATION_90;
        }
        return Surface.ROTATION_0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
    }

    private void returnData() {
        if (currentConfig.isCompressed()) {
            File returnFile;
            File unCompressedFile = new File(mCurrentFilePath);
            if (unCompressedFile.length() > 1024 * 1024) {
                returnFile = PickerUtils.compressImage(getApplicationContext(), unCompressedFile);
            } else {
                returnFile = unCompressedFile;
            }
            mCurrentFilePath = returnFile.getAbsolutePath();
            Log.e("FILE PATH", mCurrentFilePath);
            Log.e("FILE LENGTH", returnFile.length() + "");
        }

        loadingDialog.dismiss();

        Intent i = new Intent();
        i.putStringArrayListExtra(PickerConfig.FILE_PATH_DATA, new ArrayList<>(Collections.singletonList(mCurrentFilePath)));
        setResult(RESULT_OK, i);
        finish();
    }

    private int aspectRatio(Integer width, Integer height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        double RATIO_4_3_VALUE = 4.0 / 3.0;
        double RATIO_16_9_VALUE = 16.0 / 9.0;
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    PERMISSION_REQUEST);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(this::openCamera, 500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                checkPermissions();
            }
        }
    }
}
