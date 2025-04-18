
+------------------+         JS-API         +----------------------+
|  Cordova App     | <--------------------> |  proxyplugin.js      |
|  (JavaScript)    |                        |  (JS bridge)         |
+------------------+                        +----------+-----------+
                                                     |
                                                     v
                                           +---------+----------+
                                           |    ProxyPlugin     |
                                           |  (Cordova plugin)  |
                                           +---------+----------+
                                                     |
                                           calls start/stop/etc.
                                                     v
                                         +-----------+-----------+
                                         |     ProxyService      |
                                         |  (ForegroundService)  |
                                         +-----------+-----------+
                                                     |
                           +-------------------------+---------------------------+
                           |                         |                           |
                           v                         v                           v
              +------------------+     +------------------------+     +------------------+
              |   Socks5Server   |     |     ConfigManager      |     |    LogManager     |
              | (ServerSocket)   |     | (Prefs + JS-accessible)|     | (logs + ws hook)  |
              +--------+---------+     +-----------+------------+     +--------+----------+
                       |                           |                           |
             accepts client sockets        saves/loads settings       push logs to JS/UI
                       |
                       v
              +------------------+
              |  Socks5Session   |
              |  (per connection)|
              +--------+---------+
                       |
              handles SOCKS5 handshake
              connect & data tunneling
                       |
                       v
              +-------------------+
              |     Tunnel        |
              | (2-way pipe)      |
              +--------+----------+
                       |
               encrypted pipe (opt.)
                       |
              +-------------------+
              |   AuthManager     |
              |  (user/pass/IP)   |
              +-------------------+

         ↑            ↑
         |            |
+-------------------+  +------------------+
|   NetworkUtils    |  |  ConnectionTester|
| (get IP, status)  |  |  (test via proxy)|
+-------------------+  +------------------+

         ↑
         |
+-------------------+
|  BootReceiver     |
| (on BOOT_COMPLETED|
|   + autostart     |
+-------------------+
