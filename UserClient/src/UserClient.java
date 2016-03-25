import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 客户端，可以插入和查询数据
 * Created by Silocean on 2016-03-19.
 */
public class UserClient extends JFrame implements ActionListener {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    JTextField tfInsert = new JTextField();
    JButton btInsert = new JButton("Insert");
    JTextField tfSearch = new JTextField();
    JButton btSearch = new JButton("Search");

    public static void main(String[] args) {
        UserClient client = new UserClient();
        client.launchFrame();
    }

    /**
     * 创建并启动窗口
     */
    private void launchFrame() {
        this.setLocationRelativeTo(null);
        this.setSize(500, 300);
        this.setLayout(new GridLayout(4, 1));
        this.add(tfInsert);
        this.add(btInsert);
        this.add(tfSearch);
        this.add(btSearch);
        this.setTitle("UserClient");
        this.setVisible(true);
        connect();
        btInsert.addActionListener(this);
        btSearch.addActionListener(this);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
    }

    /**
     * 连接master
     */
    private void connect() {
        try {
            socket = new Socket(ServerIP.SERVER_IP, ServerPort.USERCLIENT_PORT);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与master断开连接
     */
    private void disconnect() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入信息
     *
     * @param message
     */
    private void insert(Message message) {
        try {
            sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws Exception
     */
    private void sendMessage(Message message) throws Exception {
        dos.writeUTF(ConvertMessage.convertMessageToJson(message));
        dos.flush();
    }

    /**
     * 查询信息
     *
     * @param message
     * @return
     */
    private String search(Message message) {
        String result = "";
        try {
            sendMessage(message);
            result = receiveMessage(dis.readUTF());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 接收UserClientServer返回的数据
     *
     * @param json
     * @return
     */
    private String receiveMessage(String json) {
        Message message = ConvertMessage.convertJsonToMessage(json);
        Student student = ConvertStudent.convertJsonToStudent(message.getMsg().toString());
        if (student.getNumber().equals("") && student.getName().equals("") && student.getBalance() == 0) {
            return "This value cannot be found!";
        } else {
            return student.toString();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btInsert) {
            String json = tfInsert.getText().trim();
            if (!json.equals("")) {
                Message message = new Message(MsgType.INSERT, ConvertStudent.convertJsonToStudent(json));
                insert(message);
            } else {
                JOptionPane.showMessageDialog(this, "This value cannot be null!");
            }
        } else if (e.getSource() == btSearch) {
            String number = tfSearch.getText().trim();
            if (!number.equals("")) {
                Message message = new Message(MsgType.SEARCH, number);
                JOptionPane.showMessageDialog(this, search(message));
            } else {
                JOptionPane.showMessageDialog(this, "This value cannot be null!");
            }
        }
    }

}
