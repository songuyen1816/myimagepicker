package com.bsp.myimagepicker;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.view.Window;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.zelory.compressor.Compressor;


@SuppressWarnings("unchecked")
public final class PickerUtils {

    public static List<String> getExternalStorageImages(Context context) {
        List<String> listImage = new ArrayList<>();

        Uri uri;
        Cursor cursor;
        int column_index_data;

        String absolutePathOfImage;

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};

        cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listImage.add(absolutePathOfImage);
            }
            cursor.close();
        }

        Collections.reverse(listImage);
        return listImage;
    }

    private static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static <T> List<T> createClone(List<T> oldList) {
        return (List<T>) deepClone(oldList);
    }

    public static File compressImage(Context context, File file){
        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(context.getApplicationContext()).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImageFile;
    }

    public static Dialog createLoadingDialog(Context context) {
        if (context == null) return null;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
