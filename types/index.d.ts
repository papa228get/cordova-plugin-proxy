interface ProxyConfig {
    port: number;
    authEnabled: boolean;
    username?: string;
    password?: string;
    allowedIps?: string;
    autoStart?: boolean;
}

interface ProxyLog {
    timestamp: number;
    level: string;
    message: string;
}

interface CordovaPluginProxy {
    startProxy(config: ProxyConfig, success?: () => void, error?: (err: string) => void): void;
    stopProxy(success?: () => void, error?: (err: string) => void): void;
    setConfig(config: ProxyConfig, success?: () => void, error?: (err: string) => void): void;
    getLog(success?: (logs: ProxyLog[]) => void, error?: (err: string) => void): void;
    connectToLogs(success?: (msg: string) => void, error?: (err: string) => void): WebSocket;
}

interface CordovaPlugins {
    ProxyPlugin: CordovaPluginProxy;
}