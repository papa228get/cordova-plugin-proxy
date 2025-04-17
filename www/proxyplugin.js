var exec = require('cordova/exec');

var ProxyPlugin = {
    startProxy: function(config, success, error) {
        exec(success, error, 'ProxyPlugin', 'startProxy', [config]);
    },

    stopProxy: function(success, error) {
        exec(success, error, 'ProxyPlugin', 'stopProxy', []);
    },

    setConfig: function(config, success, error) {
        exec(success, error, 'ProxyPlugin', 'setConfig', [config]);
    },

    getLog: function(success, error) {
        exec(success, error, 'ProxyPlugin', 'getLog', []);
    },

    connectToLogs: function(success, error) {
        var ws = new WebSocket('ws://localhost:8887');
        
        ws.onopen = function() {
            success('Connected to log stream');
        };
        
        ws.onerror = function(err) {
            error('WebSocket error: ' + err);
        };
        
        ws.onmessage = function(event) {
            var log = JSON.parse(event.data);
            // Emit event with log data
            cordova.fireDocumentEvent('proxylog', log);
        };
        
        return ws;
    },
    
    getLogs: function(success, error) {
        exec(success, error, 'ProxyPlugin', 'getLog', []);
    }
};

module.exports = ProxyPlugin;
