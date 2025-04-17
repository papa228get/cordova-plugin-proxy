package com.example.cordova.plugin.proxy;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.Context;

public class ProxyPlugin extends CordovaPlugin {
    private static final String TAG = "ProxyPlugin";
    private Intent serviceIntent;

    @Override
    public void initialize(org.apache.cordova.CordovaInterface cordova, org.apache.cordova.CordovaWebView webView) {
        super.initialize(cordova, webView);
        serviceIntent = new Intent(cordova.getActivity(), ProxyService.class);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            switch (action) {
                case "startProxy":
                    JSONObject config = args.getJSONObject(0);
                    startProxy(config, callbackContext);
                    return true;
                case "stopProxy":
                    stopProxy(callbackContext);
                    return true;
                case "setConfig":
                    JSONObject newConfig = args.getJSONObject(0);
                    setConfig(newConfig, callbackContext);
                    return true;
                case "getLog":
                    getLog(callbackContext);
                    return true;
                default:
                    callbackContext.error("Invalid action: " + action);
                    return false;
            }
        } catch (Exception e) {
            callbackContext.error("Error executing action: " + e.getMessage());
            return false;
        }
    }

    private void startProxy(JSONObject config, CallbackContext callbackContext) {
        try {
            serviceIntent.putExtra("config", config.toString());
            cordova.getActivity().startForegroundService(serviceIntent);
            
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Proxy service started");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } catch (Exception e) {
            callbackContext.error("Failed to start proxy: " + e.getMessage());
        }
    }

    private void stopProxy(CallbackContext callbackContext) {
        try {
            cordova.getActivity().stopService(serviceIntent);
            callbackContext.success("Proxy service stopped");
        } catch (Exception e) {
            callbackContext.error("Failed to stop proxy: " + e.getMessage());
        }
    }

    private void setConfig(JSONObject config, CallbackContext callbackContext) {
        try {
            serviceIntent.putExtra("config", config.toString());
            cordova.getActivity().startService(serviceIntent);
            callbackContext.success("Config updated");
        } catch (Exception e) {
            callbackContext.error("Failed to update config: " + e.getMessage());
        }
    }

    private void getLog(CallbackContext callbackContext) {
        try {
            JSONArray logs = LogManager.getInstance().getRecentLogs(100);
            PluginResult result = new PluginResult(PluginResult.Status.OK, logs);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        } catch (Exception e) {
            callbackContext.error("Failed to get logs: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        try {
            cordova.getActivity().stopService(serviceIntent);
        } catch (Exception e) {
            // Log error but don't crash
        }
        super.onDestroy();
    }
}
