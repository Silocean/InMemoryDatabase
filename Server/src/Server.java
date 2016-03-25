import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 和各个slave通信的服务类
 * Created by Silocean on 2016-03-14.
 */
public class Server {

    private boolean started = false;

    private ServerSocket serverSocket;

    private List<ClientMag> clientsList = new ArrayList<>();

    private ServerData serverData;

    private Timer timer;

    private static final int INTERVAL_TIME = 10000;

    public Server(ServerData serverData) {
        this.serverData = serverData;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(ServerPort.CLIENT_PORT);
            started = true;
            System.out.println("Server has been started!");
            startCheckSynchronized();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            while (started) { //启动后不断监听slave连接的消息，用线程分别处理每个已连接的slave
                Socket socket = serverSocket.accept();
                ClientMag client = new ClientMag(socket);
                new Thread(client).start();
                clientsList.add(client);
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

    /**
     * 每隔一段时间检查一次slaves是否同步
     */
    private void startCheckSynchronized() {
        timer = new Timer();
        timer.schedule(new SynchronizeTask(), INTERVAL_TIME, INTERVAL_TIME);
    }

    /**
     * 每隔一段时间向slaves发送消息检查各个slave的数据完整性
     */
    class SynchronizeTask extends TimerTask {
        @Override
        public void run() {
            try {
                for (ClientMag client : clientsList) {
                    Message message = new Message(MsgType.SYNCHRONIZE, serverData.getTime());
                    client.dos.writeUTF(ConvertMessage.convertMessageToJson(message));
                    client.dos.flush();
                }
                System.out.println("Server has send a request message!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 专门负责处理每个连接到master的slave的线程类
     */
    class ClientMag implements Runnable {

        private boolean beConnected = false;
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public ClientMag(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                this.beConnected = true;
                System.out.println("Slave:" + socket.getInetAddress() + " has connected!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (beConnected) { // 不断对server接收到的消息做相应处理
                    String str = dis.readUTF();
                    Message message = ConvertMessage.convertJsonToMessage(str);
                    switch (message.getType()) {
                        case MsgType.SYNCHRONIZE_CONFIRM: // slave正在请求更新数据，server应返回需要更新的数据
                            long slaveTime = Long.valueOf(message.getMsg().toString());
                            List<Student> recordsList = new ArrayList<>();
                            for (LogFile logFile : serverData.getLogFileList()) {
                                if (logFile.getTime() > slaveTime) {
                                    recordsList.add(ConvertStudent.convertJsonToStudent(logFile.getOperation()));
                                }
                            }
                            Message message1 = new Message(MsgType.UPDATE_DATA, recordsList);
                            dos.writeUTF(ConvertMessage.convertMessageToJson(message1));
                            dos.flush();
                            break;
                    }
                }
            } catch (EOFException | SocketException e) { // slave挂了,将其从clientsList里移除
                System.out.println("Slave:" + socket.getInetAddress() + " was dead!");
                clientsList.remove(this);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
