package com.example.aw.conversion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        checkStoragePermission();
        startConversion();
    }

    private void startConversion() {
        Intent intent = new Intent(getApplicationContext(),ConversionService.class);
        intent.putExtra(ConversionService.VIDEO_LIST,new String[] {
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181026_114714.mp4", //Failed -1010
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181025_095833.mp4",
//                Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID_20181030_134950632.mp4",
                Environment.getExternalStorageDirectory() + "/DCIM/Camera/20181030_154243.mp4",
        });
        startService(intent);
    }

    public void stop(View v) {
        ConversionService service = ConversionService.getService();
        service.stopSelf();
        service.stopForeground(true);
        service.stopProcess();
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

    public static void log(Object context,Object any) {
        String msg = (any == null) ? "NULL" : any.toString();
        if (context != null) {
            msg = context.getClass().getSimpleName() + "--->" + msg;
        }
        Log.e("@#@",msg);
    }
}
