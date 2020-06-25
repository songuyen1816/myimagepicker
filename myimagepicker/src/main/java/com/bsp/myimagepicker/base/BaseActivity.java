package com.bsp.myimagepicker.base;

import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsp.myimagepicker.DisposableManager;
import com.bsp.myimagepicker.PickerUtils;

public class BaseActivity extends AppCompatActivity {
    private Dialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initControls();
    }

    private void initControls() {
        loadingDialog = PickerUtils.createLoadingDialog(this);
    }

    protected void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        super.onDestroy();
    }
}
