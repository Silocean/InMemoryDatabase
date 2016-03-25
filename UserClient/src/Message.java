/**
 * Created by Silocean on 2016-03-19.
 */
public class Message {
    private int type;
    private Object msg;

    public Message() {
    }

    public Message(int type, Object msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", msg=" + msg +
                '}';
    }

}
