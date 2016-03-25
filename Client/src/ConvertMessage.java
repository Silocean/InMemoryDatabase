import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by Silocean on 2016-03-20.
 */
public class ConvertMessage {

    public static String convertMessageToJson(Message message) {
        return JSONObject.fromObject(message).toString();
    }

    public static Message convertJsonToMessage(String json) {
        JSONObject object = JSONObject.fromObject(json);
        int type = object.getInt("type");
        Message message = null;
        switch (type) {
            case MsgType.INSERT:
                message = new Message(type, object.getJSONObject("msg"));
                break;
            case MsgType.SEARCH:
                message = new Message(type, object.getString("msg"));
                break;
            case MsgType.SEARCH_RESULT:
                message = new Message(type, object.getJSONObject("msg"));
                break;
            case MsgType.SYNCHRONIZE:
                message = new Message(type, object.getLong("msg"));
                break;
            case MsgType.SYNCHRONIZE_CONFIRM:
                message = new Message(type, object.getLong("msg"));
                break;
            case MsgType.UPDATE_DATA:
                message = new Message(type, JSONArray.toList(JSONArray.fromObject(object.getString("msg")), Student.class));
                break;
        }
        return message;
    }

}
