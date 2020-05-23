package com.bsp.myimagepicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bsp.myimagepicker.base.BaseActivity;
import com.bsp.myimagepicker.databinding.ActivityImagePickerBinding;
import com.bsp.myimagepicker.listener.ImageAdapterListener;
import com.bsp.myimagepicker.listener.ImagePickerListener;
import com.bsp.myimagepicker.model.MyImage;

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends BaseActivity implements ImageAdapterListener, ImagePickerListener {

    private final int PERMISSION_REQUEST = 111;
    private PickerAdapter adapter;
    private ActivityImagePickerBinding binding;
    private ArrayList<String> filePathPicked = new ArrayList<>();
    private PickerConfig currentConfig;

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
        initControls();
        initEvents();
    }

    private void fillConfig() {
        binding.tvToolbarTitle.setText(filePathPicked.size() > 0 ?
                String.format(getString(R.string.multi_pick_title),
                        String.valueOf(filePathPicked.size()),
                        String.valueOf(currentConfig.getMaxCount()))
                : currentConfig.getPickerTitle());
    }

    private void initControls() {
        adapter = new PickerAdapter(this, new PickerDiffCallback());
        adapter.setImagePickedDrawable(createImagePickedDrawable());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        binding.rvImages.setLayoutManager(gridLayoutManager);
        binding.rvImages.setItemAnimator(null);
        binding.rvImages.setAdapter(adapter);
    }

    private Drawable createImagePickedDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke((int) getResources().getDimension(R.dimen._2sdp), currentConfig.getStyleColor());
        return drawable;
    }

    private void initEvents() {
        adapter.setListener(this);
    }

    void loadImagesFromSDCard() {
        List<String> listPath = PickerUtils.getExternalStorageImages(getApplication());
        List<MyImage> imageList = new ArrayList<>();
        for (String filePath : listPath) {
            imageList.add(new MyImage(filePath, false));
        }
        adapter.submitList(imageList);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
        checkPermissions();
    }

    @Override
    public void onImagePicked(String path) {
        if (filePathPicked.size() >= currentConfig.getMaxCount()) {
            filePathPicked.remove(0);
            adapter.notifyMaxCountItem();
        }
        filePathPicked.add(path);
        fillConfig();
    }

    @Override
    public void onImageUnPicked(String path) {
        removePickedImage(path);
        fillConfig();
    }

    private void removePickedImage(String path) {
        int removePos = 0;
        for (int i = 0; i < filePathPicked.size(); i++) {
            if (filePathPicked.get(i).equals(path)) {
                removePos = i;
                break;
            }
        }
        filePathPicked.remove(removePos);
    }

    @Override
    public void onDone() {
        Intent i = new Intent();
        i.putStringArrayListExtra(PickerConfig.FILE_PATH_DATA, filePathPicked);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onCancel() {
        finish();
    }
}
