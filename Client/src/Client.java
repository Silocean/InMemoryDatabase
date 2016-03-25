import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * slave，负责和master上的数据保持一致
 * Created by Silocean on 2016-03-14.
 */
public class Client {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean beConnected = false;

    private ServerData serverData;

    private long time = 0L;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    /**
     * 开始
     */
    private void start() {
        connect();
        serverData = ServerData.getInstance();
        new Thread(new ReceiveThread()).start();
    }

    /**
     * 连接master服务器
     */
    private void connect() {
        try {
            socket = new Socket(ServerIP.SERVER_IP, ServerPort.CLIENT_PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            beConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 专门负责监听server发来的消息的线程
     */
    class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                while (beConnected) {
                    String str = dis.readUTF();
                    Message message = ConvertMessage.convertJsonToMessage(str);
                    switch (message.getType()) {
                        case MsgType.SYNCHRONIZE: // server询问slave是否需要更新数据
                            time = Long.parseLong(message.getMsg().toString());
                            System.out.println("local time:" + serverData.getTime() + ";update time" + time);
                            if (time > serverData.getTime()) { // 本地数据不完整，需要向server发送消息请求更新
                                Message message1 = new Message(MsgType.SYNCHRONIZE_CONFIRM, serverData.getTime());
                                dos.writeUTF(ConvertMessage.convertMessageToJson(message1));
                                dos.flush();
                            }
                            break;
                        case MsgType.UPDATE_DATA: // server把需要更新的数据发送过来，更新slave本地数据
                            JSONObject object = JSONObject.fromObject(message);
                            List<Student> list = JSONArray.toList(JSONArray.fromObject(object.getString("msg")), Student.class);
                            for (Student student : list) {
                                serverData.insert(student);
                            }
                            System.out.println(list + "=========");
                            serverData.setTime(time); // 更新本地slave时间戳（注意这个time是server轮询的时候发过来的，可能和取数据后的时间不一致）
                            break;
                    }
                }
            } catch (SocketException | EOFException e) {
                System.out.println("Server has been shut down!");
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
