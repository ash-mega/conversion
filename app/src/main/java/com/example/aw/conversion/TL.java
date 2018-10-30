package com.example.aw.conversion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Logger for testing.
 */
public class TL extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStoragePermission();
//        mediaTable();
        startConversion();

    }

    private void fileModifyTime() {
        String path = "/storage/emulated/0/DCIM/settings.odg";
        File file = new File(path);
        log(this,file.exists());
        boolean f = file.setLastModified(System.currentTimeMillis() + 1000 * 60 * 60);
        log(this,f);
        log(this,new Date(file.lastModified()));
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasStoragePermission = (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasStoragePermission) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            log(this,"get permission failed");
        }
    }

    private void support4K() {

    }

    private void startConversion() {
        Intent intent = new Intent(getApplicationContext(),ConversionService.class);
        intent.putExtra(ConversionService.VIDEO_LIST,new String[] {
//                "/storage/emulated/0/DCIM/Camera/mkv.mkv"
//                "/storage/emulated/0/DCIM/Camera/avi.avi" //Failed
//                "/storage/emulated/0/DCIM/Camera/mov.mov"
//                "/storage/emulated/0/DCIM/Camera/4k.mp4"  //Failed
//                "/storage/emulated/0/DCIM/Camera/mp4.mp4"
//                "/storage/emulated/0/DCIM/Camera/VID_20181026_120202.mp4", //Failed -1010
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181026_113226.mp4",
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181026_114714.mp4", //Failed -1010
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181025_095833.mp4",
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181030_134950632.mp4",
                Environment.getExternalStorageDirectory() + "/DCIM/Camera/20181030_140608.mp4",
//                "/storage/emulated/0/DCIM/Camera/8k.mp4"
//                "/storage/emulated/0/DCIM/Camera/mpg.mpg" //Failed
//                "/storage/emulated/0/DCIM/Camera/flv.flv" //Failed
//                "/storage/emulated/0/DCIM/Camera/3gp.3gp" //Failed
//                "/storage/emulated/0/DCIM/Camera/wmv.wmv" //Failed
//                "/storage/emulated/0/DCIM/Camera/ts.ts"   //Failed
        });
        startService(intent);
    }

    private void testSort() {
        List<Long> longs = new ArrayList<>(5);
        longs.add(123311L);
        longs.add(223311L);
        longs.add(823332L);
        longs.add(713311L);
        longs.add(120111L);

        TL.log(this,longs.get(0));
        Comparator<Long> comparator = new Comparator<Long>() {
            @Override
            public int compare(Long o1,Long o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                return (o1 > o2) ? -1 : 1;
            }
        };
//        longs.sort(comparator);
        Collections.sort(longs,comparator);
        TL.log(this,longs.get(0));
    }

    private void testCompress() {
//        String OUTPUT_PATH = getFilesDir().toString() + File.separator + "output6.mp4";
//
//        VideoCompressor compressor = new VideoCompressor(this);
//        try {
//            compressor.changeResolution(new File(ORIGIN_PATH),OUTPUT_PATH);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
    }

    private void mediaTable() {
        String projection[] = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATE_MODIFIED
        };

        String order = MediaStore.MediaColumns.DATE_MODIFIED + " ASC";

        ArrayList<Uri> uris = new ArrayList<>();
//        uris.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        uris.add(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        uris.add(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        uris.add(MediaStore.Video.Media.INTERNAL_CONTENT_URI);

        for (int i = 0;i < uris.size();i++) {
            Cursor cursorCamera = getContentResolver().query(uris.get(i),projection,null,null,order);
            if (cursorCamera != null) {
                int dataColumn = cursorCamera.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                int mDateColumn = cursorCamera.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
                int aDateColumn = cursorCamera.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
                int j = 0;
                while (cursorCamera.moveToNext()) {
                    log(this,j++ + " = " + cursorCamera.getString(dataColumn));
                }
            }
        }
    }

    public static void log(Object context,Object any) {
        String msg = (any == null) ? "NULL" : any.toString();
        if (context != null) {
            msg = context.getClass().getSimpleName() + "--->" + msg;
        }
        Log.e("@#@",msg);
    }

    public static void main(String[] args) {
        System.out.println(0xfffffc0e);
    }
}
