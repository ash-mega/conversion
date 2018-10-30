package com.example.aw.conversion;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.Arrays;

public class ConversionService extends Service implements VideoCompressionCallback {

    public static final String CONVERSION_NOTI_CHANNEL_ID = "conversion_noti_id";
    public static final String CONVERSION_NOTI_CHANNEL_NAME = "conversion_noti_name";
    public static final int CONVERSION_NOTI_ID = 221;

    public static final String VIDEO_LIST = "video_list";

    private NotificationManager nManager;

    private VideoCompressor compressor;
    private int iconId = R.mipmap.ic_launcher_round;

    private static ConversionService service;

    @Override
    public void onCreate() {
        super.onCreate();
        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        compressor = new VideoCompressor(this,this);
        service = this;
    }

    public static ConversionService getService() {
        return service;
    }

    public void stopProcess() {
        compressor.stop();
    }

    private static String getFileNameFromPath(String path) {
        String[] paths = path.split(File.separator);
        return paths[paths.length - 1];
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {
        final String[] extra = intent.getStringArrayExtra(VIDEO_LIST);
        compressor.setPendingList(Arrays.asList(extra));
        compressor.setOutputRoot(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator);

        showNotification();
        new Thread() {

            @Override
            public void run() {
                compressor.start();
            }
        }.start();
        return START_NOT_STICKY;
    }

    private void showNotification() {
        NotificationCompat.Builder compatBuilder;
        Notification.Builder nBuilder;
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CONVERSION_NOTI_CHANNEL_ID,CONVERSION_NOTI_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null,null);
            nManager.createNotificationChannel(channel);
            compatBuilder = new NotificationCompat.Builder(getApplicationContext(),CONVERSION_NOTI_CHANNEL_ID);
            iconId = R.drawable.ic_launcher_foreground;
            compatBuilder.setSmallIcon(iconId)
                    .setOngoing(false)
                    .setContentTitle("Prepare for conversion.")
                    .setOnlyAlertOnce(true);
            notification = compatBuilder.build();
        } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
            nBuilder = new Notification.Builder(getApplicationContext());
            nBuilder.setSmallIcon(iconId)
                    .setOngoing(false)
                    .setContentTitle("Prepare for conversion.")
                    .setOnlyAlertOnce(true);
            notification = nBuilder.build();
        }
        startForeground(CONVERSION_NOTI_ID,notification);
    }

    private void updateProgressNotification(int progressPercent,String currentIndexString) {
        String message = progressPercent + "% has been completed";
        NotificationCompat.Builder compatBuilder;
        Notification.Builder nBuilder;
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CONVERSION_NOTI_CHANNEL_ID,CONVERSION_NOTI_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null,null);
            nManager.createNotificationChannel(channel);
            compatBuilder = new NotificationCompat.Builder(getApplicationContext(),CONVERSION_NOTI_CHANNEL_ID);
            compatBuilder.setSmallIcon(iconId)
                    .setProgress(100,progressPercent,false)
                    .setOngoing(true)
                    .setContentText(currentIndexString)
                    .setContentTitle(message)
                    .setOnlyAlertOnce(true);
            notification = compatBuilder.build();
        } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)) {
            nBuilder = new Notification.Builder(getApplicationContext());
            nBuilder.setSmallIcon(iconId)
                    .setProgress(100,progressPercent,false)
                    .setOngoing(true)
                    .setContentText(currentIndexString)
                    .setContentTitle(message)
                    .setOnlyAlertOnce(true);
            notification = nBuilder.build();
        }
        nManager.notify(CONVERSION_NOTI_ID,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TL.log(this,"onDestroy");
        stopProcess();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompressUpdateProgress(int progress,String currentIndexString) {
        updateProgressNotification(progress,currentIndexString);
    }

    @Override
    public void onCompressSuccessful(String path) {

    }

    @Override
    public void onCompressFailed(String path) {

    }

    @Override
    public void onCompressFinished(String currentIndexString) {
        updateProgressNotification(100,currentIndexString);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopForeground(true);
        nManager.cancel(CONVERSION_NOTI_ID);
    }
}
