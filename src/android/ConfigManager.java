package com.example.cordova.plugin.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigManager {
    private static final String PREFS_NAME = "ProxyPluginPrefs";
    private static final String KEY_PORT = "port";
    private static final String KEY_AUTH_ENABLED = "authEnabled";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ALLOWED_IPS = "allowedIps";
    private static final String KEY_AUTO_START = "autoStart";

    private final SharedPreferences preferences;
    private JSONObject currentConfig;

    public ConfigManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadConfig();
    }

    private void loadConfig() {
        try {
            currentConfig = new JSONObject();
            currentConfig.put(KEY_PORT, preferences.getInt(KEY_PORT, 1080));
            currentConfig.put(KEY_AUTH_ENABLED, preferences.getBoolean(KEY_AUTH_ENABLED, false));
            currentConfig.put(KEY_USERNAME, preferences.getString(KEY_USERNAME, ""));
            currentConfig.put(KEY_PASSWORD, preferences.getString(KEY_PASSWORD, ""));
            currentConfig.put(KEY_ALLOWED_IPS, preferences.getString(KEY_ALLOWED_IPS, "*"));
            currentConfig.put(KEY_AUTO_START, preferences.getBoolean(KEY_AUTO_START, false));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setConfig(JSONObject config) {
        SharedPreferences.Editor editor = preferences.edit();
        try {
            if (config.has(KEY_PORT)) {
                editor.putInt(KEY_PORT, config.getInt(KEY_PORT));
            }
            if (config.has(KEY_AUTH_ENABLED)) {
                editor.putBoolean(KEY_AUTH_ENABLED, config.getBoolean(KEY_AUTH_ENABLED));
            }
            if (config.has(KEY_USERNAME)) {
                editor.putString(KEY_USERNAME, config.getString(KEY_USERNAME));
            }
            if (config.has(KEY_PASSWORD)) {
                editor.putString(KEY_PASSWORD, config.getString(KEY_PASSWORD));
            }
            if (config.has(KEY_ALLOWED_IPS)) {
                editor.putString(KEY_ALLOWED_IPS, config.getString(KEY_ALLOWED_IPS));
            }
            if (config.has(KEY_AUTO_START)) {
                editor.putBoolean(KEY_AUTO_START, config.getBoolean(KEY_AUTO_START));
            }
            editor.apply();
            loadConfig();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getConfig() {
        return currentConfig;
    }

    public int getPort() {
        try {
            return currentConfig.getInt(KEY_PORT);
        } catch (JSONException e) {
            return 1080;
        }
    }

    public boolean isAuthEnabled() {
        try {
            return currentConfig.getBoolean(KEY_AUTH_ENABLED);
        } catch (JSONException e) {
            return false;
        }
    }

    public String getUsername() {
        try {
            return currentConfig.getString(KEY_USERNAME);
        } catch (JSONException e) {
            return "";
        }
    }

    public String getPassword() {
        try {
            return currentConfig.getString(KEY_PASSWORD);
        } catch (JSONException e) {
            return "";
        }
    }

    public String getAllowedIps() {
        try {
            return currentConfig.getString(KEY_ALLOWED_IPS);
        } catch (JSONException e) {
            return "*";
        }
    }

    public boolean isAutoStartEnabled() {
        try {
            return currentConfig.getBoolean(KEY_AUTO_START);
        } catch (JSONException e) {
            return false;
        }
    }
}