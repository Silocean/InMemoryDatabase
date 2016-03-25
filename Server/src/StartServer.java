/**
 * 启动服务器
 * Created by Silocean on 2016-03-21.
 */
public class StartServer {

    private static ServerData serverData;

    public static void main(String[] args) {
        serverData = ServerData.getInstance();
        startUserClientServer(serverData);
        startServer(serverData);
    }

    /**
     * 启动UserClientServer
     */
    private static void startUserClientServer(final ServerData serverData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserClientServer userClientServer = new UserClientServer(serverData);
                userClientServer.start();
            }
        }).start();

    }

    /**
     * 启动Server
     */
    private static void startServer(final ServerData serverData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server server = new Server(serverData);
                server.start();
            }
        }).start();

    }

}
