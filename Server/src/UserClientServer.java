import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * 和普通普通用户通信的服务类
 * Created by Silocean on 2016-03-21.
 */
public class UserClientServer {

    private ServerSocket serverSocket;
    private Socket socket;

    private boolean started = false;

    private ServerData serverData;

    public UserClientServer(ServerData serverData) {
        this.serverData = serverData;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(ServerPort.USERCLIENT_PORT);
            started = true;
            serverData = ServerData.getInstance();
            System.out.println("UserClientServer has been started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            while (started) { // 启动后不断监听userClient的连接，用线程分别处理每个已连接的userClient
                socket = serverSocket.accept();
                UserClientMag userClientMag = new UserClientMag(socket);
                new Thread(userClientMag).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UserClientMag implements Runnable {

        private boolean beConnected = false;
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public UserClientMag(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                this.beConnected = true;
                System.out.println("UserClient:" + socket.getInetAddress() + " has connected!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (beConnected) { // 不断对master接收到的查询或插入消息做相应处理
                    String str = dis.readUTF();
                    Message message = ConvertMessage.convertJsonToMessage(str);
                    switch (message.getType()) {
                        case MsgType.INSERT: // 插入消息
                            serverData.insert(ConvertStudent.convertJsonToStudent(message.getMsg().toString()));
                            System.out.println("Insert Successfully!");
                            System.out.println("insert time:" + serverData.getTime());
                            break;
                        case MsgType.SEARCH: // 查询消息
                            Student student = serverData.search(message.getMsg().toString());
                            Message message1 = new Message(MsgType.SEARCH_RESULT, new Student());
                            if (student != null) {
                                message1 = new Message(MsgType.SEARCH_RESULT, student);
                            }
                            dos.writeUTF(ConvertMessage.convertMessageToJson(message1));
                            dos.flush();
                            break;
                    }
                }
            } catch (EOFException | SocketException e) { // userClient关闭
                System.out.println("UserClient:" + socket.getInetAddress() + " was closed!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally { // 清理资源
                try {
                    if (dis != null) dis.close();
                    if (dos != null) dos.close();
                    if (socket != null) socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
