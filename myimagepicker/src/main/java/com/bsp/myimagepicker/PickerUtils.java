package com.bsp.myimagepicker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unchecked")
class PickerUtils {

    static List<String> getExternalStorageImages(Context context) {
        List<String> listImage = new ArrayList<>();

        Uri uri;
        Cursor cursor;
        int column_index_data;

        String absolutePathOfImage;

        //get all images from external storage

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

        // Get all Internal storage images

//        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//
//        cursor = context.getContentResolver().query(uri, projection, null,
//                null, null);
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//
//        while (cursor.moveToNext()) {
//
//            absolutePathOfImage = cursor.getString(column_index_data);
//            listImage.add(absolutePathOfImage);
//        }

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

}
