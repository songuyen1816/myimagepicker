package com.bsp.myimagepicker.features.imagepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bsp.myimagepicker.DisposableManager;
import com.bsp.myimagepicker.PickerConfig;
import com.bsp.myimagepicker.PickerUtils;
import com.bsp.myimagepicker.R;
import com.bsp.myimagepicker.base.BaseActivity;
import com.bsp.myimagepicker.databinding.ActivityImagePickerBinding;
import com.bsp.myimagepicker.listener.ImageAdapterListener;
import com.bsp.myimagepicker.listener.ImagePickerListener;
import com.bsp.myimagepicker.model.MyImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("UnstableApiUsage")
public class PickerActivity extends BaseActivity implements ImageAdapterListener, ImagePickerListener {

    private final int PERMISSION_REQUEST = 111;
    private PickerAdapter adapter;
    private ActivityImagePickerBinding binding;
    private ArrayList<Uri> filePathPicked = new ArrayList<>();
    private PickerConfig currentConfig;
    private boolean permissionGrantFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentConfig = (PickerConfig) getIntent()
                .getSerializableExtra(PickerConfig.CONFIG_BUNDLE_KEY);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker);
        binding.setHandler(this);

        getWindow().setStatusBarColor(currentConfig.getStyleColor());
        binding.toolbar.setBackgroundColor(currentConfig.getStyleColor());

        fillConfig();
        initEvents();
    }

    private void fillConfig() {
        binding.tvToolbarTitle.setText(filePathPicked.size() > 0 ?
                String.format(getString(R.string.multi_pick_title),
                        String.valueOf(filePathPicked.size()),
                        String.valueOf(currentConfig.getMaxCount()))
                : currentConfig.getPickerTitle());
    }

    private Drawable createImagePickedDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke((int) getResources().getDimension(R.dimen._2sdp), currentConfig.getStyleColor());
        return drawable;
    }

    private void initEvents() {
    }

    void loadImagesFromSDCard() {
        permissionGrantFlag = true;

        adapter = new PickerAdapter(this, new PickerDiffCallback());
        adapter.setImagePickedDrawable(createImagePickedDrawable());
        adapter.setListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        binding.rvImages.setLayoutManager(gridLayoutManager);
        binding.rvImages.setItemAnimator(null);
        binding.rvImages.setAdapter(adapter);

        DisposableManager.add(
                Single.fromCallable(() -> PickerUtils.getExternalStorageImagesQ(getApplicationContext()))
                        .map(paths -> {
                            List<MyImage> imageList = new ArrayList<>();
                            for (Uri uri : paths) {
                                imageList.add(new MyImage(uri, false));
                            }
                            return imageList;
                        })
                        .doOnSubscribe(d -> showLoading())
                        .doOnTerminate(this::hideLoading)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(images -> {
                            adapter.submitList(images);
                        }, throwable -> {
                            Log.e("THROWABLE", throwable.getLocalizedMessage());
                        }));
    }

    private void checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        } else {
            loadImagesFromSDCard();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImagesFromSDCard();
        } else {
            checkPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!permissionGrantFlag) {
            checkPermissions();
        }
    }

    @Override
    public void onImagePicked(Uri uri) {
        if (filePathPicked.size() >= currentConfig.getMaxCount()) {
            filePathPicked.remove(0);
            adapter.notifyMaxCountItem();
        }
        filePathPicked.add(uri);
        fillConfig();
    }

    @Override
    public void onImageUnPicked(Uri uri) {
        removePickedImage(uri);
        fillConfig();
    }

    private void removePickedImage(Uri uri) {
        int removePos = 0;
        for (int i = 0; i < filePathPicked.size(); i++) {
            if (filePathPicked.get(i).equals(uri)) {
                removePos = i;
                break;
            }
        }
        filePathPicked.remove(removePos);
    }

    @Override
    public void onDone() {
        if (filePathPicked.isEmpty()) {
            return;
        }
        DisposableManager.add(Single.fromCallable(this::getImagePathAndReturnData).doOnSubscribe(d -> showLoading())
                .doOnTerminate(this::hideLoading)
                .subscribe(paths -> {
                    Intent i = new Intent();
                    i.putStringArrayListExtra(PickerConfig.FILE_PATH_DATA, paths);
                    setResult(RESULT_OK, i);
                    finish();
                }, throwable -> {
                    Log.e("THROWABLE", throwable.getLocalizedMessage());
                }));

    }

    private ArrayList<String> getImagePathAndReturnData() {
        ArrayList<String> listPath = new ArrayList<>();

        for (Uri uri : filePathPicked) {
            String path = PickerUtils.createCopyAndReturnRealPath(getApplicationContext(), uri);
            listPath.add(path);
        }

        if (currentConfig.isCompressed()) {
            ArrayList<String> filePathTemp = new ArrayList<>();

            for (String filePath : listPath) {
                File unCompressed = new File(filePath);
                File compressed;
                if (unCompressed.length() > 1024 * 1024) {
                    compressed = PickerUtils.compressImage(getApplicationContext(), unCompressed);
                } else {
                    compressed = unCompressed;
                }
                filePathTemp.add(compressed.getAbsolutePath());
            }

            listPath.clear();
            listPath.addAll(filePathTemp);
        }
        return listPath;
    }

    @Override
    public void onCancel() {
        finish();
    }
}
