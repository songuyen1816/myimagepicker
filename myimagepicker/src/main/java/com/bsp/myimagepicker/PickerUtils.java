package com.bsp.myimagepicker;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsp.myimagepicker.model.MyImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import id.zelory.compressor.Compressor;


public final class PickerUtils {

    public static List<MyImage> getExternalStorageImagesQ(Context context) {
        List<MyImage> listImage = new ArrayList<>();

        Uri externalUri;
        Cursor cursor;

        externalUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


        cursor = context.getContentResolver().query(
                externalUri,
                new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATE_ADDED},
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED);
                long millis = cursor.getLong(dateTakenIndex);
                Uri uri = ContentUris.withAppendedId(externalUri, Long.parseLong(id));
                listImage.add(new MyImage(uri, millis, false, false));
            }
            cursor.close();
        }
        return listImage;
    }

    public static List<MyImage> getExternalStorageVideosQ(Context context) {
        List<MyImage> listVideo = new ArrayList<>();

        Uri externalUri;
        Cursor cursor;

        externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        cursor = context.getContentResolver().query(
                externalUri,
                new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATE_ADDED},
                null,
                null,
                MediaStore.Video.VideoColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED);
                long millis = cursor.getLong(dateTakenIndex);

//                String dateTaken = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
                Uri uri = ContentUris.withAppendedId(externalUri, Long.parseLong(id));
                listVideo.add(new MyImage(uri, millis, false, true));
            }
            cursor.close();
        }
        return listVideo;
    }

    public static List<MyImage> getExternalStorageVideoAndImages(Context context) {
        List<MyImage> imagesAndVideos = new ArrayList<>();
        imagesAndVideos.addAll(getExternalStorageVideosQ(context));
        imagesAndVideos.addAll(getExternalStorageImagesQ(context));

        boolean haveSwap;
        for (int i = 0; i < imagesAndVideos.size() - 1; i++) {

            haveSwap = false;

            for (int j = 0; j < imagesAndVideos.size() - i - 1; j++) {
                if (imagesAndVideos.get(j).getDateTaken() < imagesAndVideos.get(j + 1).getDateTaken()) {
                    MyImage temp1 = imagesAndVideos.get(j);
                    MyImage temp2 = imagesAndVideos.get(j + 1);
                    imagesAndVideos.set(j, temp2);
                    imagesAndVideos.set(j + 1, temp1);
                    haveSwap = true;
                }
            }

            if(!haveSwap){
                break;
            }
        }

        return imagesAndVideos;
    }

    @Nullable
    public static String createCopyAndReturnRealPathImage(
            @NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;

        File directory = getExternalStoragePath(context);

        if (!directory.exists()) {
            try {
                Log.e("MAKE DIR", directory.mkdir() + "");
            } catch (Exception e) {
                Log.e("APP", e.getLocalizedMessage());
            }
        }

        String filePath = getExternalStoragePath(context).getAbsolutePath() + File.separator +
                +System.currentTimeMillis() + ".jpeg";

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    @Nullable
    public static String createCopyAndReturnRealPathVideo(
            @NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;

        File directory = getExternalStoragePath(context);

        if (!directory.exists()) {
            try {
                Log.e("MAKE DIR", directory.mkdir() + "");
            } catch (Exception e) {
                Log.e("APP", e.getLocalizedMessage());
            }
        }

        String filePath = getExternalStoragePath(context).getAbsolutePath() + File.separator +
                +System.currentTimeMillis() + ".mp4";

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    public static File compressImage(Context context, File file) {
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

    public static Bitmap flipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bitmap.getWidth(), 0);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void saveBitmapToFile(String filePath, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        File f = new File(filePath);
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getExternalStoragePath(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static Bitmap checkRotateBitmapFrontCamera(String absolutePath, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(absolutePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
