package com.example.cordova.plugin.proxy;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import org.json.JSONObject;

public class LogWebSocketServer extends WebSocketServer implements LogManager.LogListener {
    private static final int WS_PORT = 8887;
    private static LogWebSocketServer instance;
    
    private LogWebSocketServer() {
        super(new InetSocketAddress(WS_PORT));
        LogManager.getInstance().addListener(this);
    }
    
    public static synchronized LogWebSocketServer getInstance() {
        if (instance == null) {
            instance = new LogWebSocketServer();
        }
        return instance;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LogManager.getInstance().debug("New WebSocket connection: " + conn.getRemoteSocketAddress());
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogManager.getInstance().debug("WebSocket closed: " + reason);
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        // Handle incoming messages if needed
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogManager.getInstance().error("WebSocket error: " + ex.getMessage());
    }
    
    @Override
    public void onStart() {
        LogManager.getInstance().info("WebSocket server started on port " + WS_PORT);
    }
    
    @Override
    public void onNewLog(LogManager.LogEntry entry) {
        broadcast(entry.toJSON().toString());
    }
}