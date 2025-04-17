package com.example.cordova.plugin.proxy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import org.json.JSONObject;

public class ProxyService extends Service {
    private static final String CHANNEL_ID = "ProxyServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String WAKE_LOCK_TAG = "ProxyService::WakeLock";

    private Socks5Server socks5Server;
    private ConfigManager configManager;
    private PowerManager.WakeLock wakeLock;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LogManager.getInstance().info("ProxyService: Creating service");
        socks5Server = new Socks5Server();
        configManager = new ConfigManager(this);
        createNotificationChannel();
        
        // Start WebSocket server for logs
        LogWebSocketServer.getInstance().start();
        
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("config")) {
            try {
                String configStr = intent.getStringExtra("config");
                JSONObject config = new JSONObject(configStr);
                start(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void start(JSONObject config) {
        if (!isRunning) {
            LogManager.getInstance().info("ProxyService: Starting proxy server");
            configManager.setConfig(config);
            startForeground(NOTIFICATION_ID, createNotification());
            
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
            }
            
            socks5Server.start(configManager);
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            LogManager.getInstance().info("ProxyService: Stopping proxy server");
            socks5Server.stop();
            
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            
            stopForeground(true);
            stopSelf();
            isRunning = false;
        }
    }

    public void setConfig(JSONObject config) {
        configManager.setConfig(config);
        if (isRunning) {
            updateNotification();
        }
    }

    public String getLog() {
        return socks5Server.getLog();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Proxy Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("SOCKS5 proxy service notification channel");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(); // Intent для открытия приложения
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Proxy Service")
            .setContentText("Running on port " + configManager.getPort())
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setContentIntent(pendingIntent)
            .build();
    }

    private void updateNotification() {
        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }
}
