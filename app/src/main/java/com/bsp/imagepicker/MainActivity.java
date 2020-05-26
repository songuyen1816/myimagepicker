package com.bsp.imagepicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.bsp.myimagepicker.MyImagePicker;
import com.bsp.myimagepicker.PickerConfig;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PickerConfig config = new PickerConfig.Builder()
                .setCompressed(true)
                .setPickerTitle("Chọn Ảnh")
                .setStyleColor(Color.parseColor("#3498db")).build();

        MyImagePicker.getInstance().setPickerConfig(config).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickerConfig.IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                List<String> filePath = data.getStringArrayListExtra(PickerConfig.FILE_PATH_DATA);
                for (String path : Objects.requireNonNull(filePath)){
                    Log.e("PATH", path);
                }
            }
        }
    }
}
