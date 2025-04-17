package com.example.cordova.plugin.proxy;

import android.util.Log;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogManager {
    private static final String TAG = "ProxyPlugin:LogManager";
    private static final int MAX_LOG_ENTRIES = 1000;
    
    private static LogManager instance;
    private final List<LogEntry> logEntries;
    private final List<LogListener> listeners;
    
    private LogManager() {
        logEntries = new ArrayList<>();
        listeners = new CopyOnWriteArrayList<>();
    }
    
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }
    
    public void log(String level, String message) {
        LogEntry entry = new LogEntry(level, message);
        
        synchronized (logEntries) {
            if (logEntries.size() >= MAX_LOG_ENTRIES) {
                logEntries.remove(0);
            }
            logEntries.add(entry);
        }
        
        // Системное логирование
        switch (level.toLowerCase()) {
            case "debug": Log.d(TAG, message); break;
            case "info":  Log.i(TAG, message); break;
            case "warn":  Log.w(TAG, message); break;
            case "error": Log.e(TAG, message); break;
        }
        
        // Оповещение слушателей
        notifyListeners(entry);
    }
    
    public void debug(String message) { log("debug", message); }
    public void info(String message)  { log("info", message);  }
    public void warn(String message)  { log("warn", message);  }
    public void error(String message) { log("error", message); }
    
    public void addListener(LogListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(LogListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(LogEntry entry) {
        for (LogListener listener : listeners) {
            listener.onNewLog(entry);
        }
    }
    
    public JSONArray getRecentLogs(int limit) {
        JSONArray logs = new JSONArray();
        synchronized (logEntries) {
            int start = Math.max(0, logEntries.size() - limit);
            for (int i = start; i < logEntries.size(); i++) {
                logs.put(logEntries.get(i).toJSON());
            }
        }
        return logs;
    }
    
    private static class LogEntry {
        final long timestamp;
        final String level;
        final String message;
        
        LogEntry(String level, String message) {
            this.timestamp = System.currentTimeMillis();
            this.level = level;
            this.message = message;
        }
        
        JSONObject toJSON() {
            JSONObject json = new JSONObject();
            try {
                json.put("timestamp", timestamp);
                json.put("level", level);
                json.put("message", message);
            } catch (Exception e) {
                Log.e(TAG, "Error converting log to JSON", e);
            }
            return json;
        }
    }
    
    public interface LogListener {
        void onNewLog(LogEntry entry);
    }
}