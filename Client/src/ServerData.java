import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silocean on 2016-03-21.
 */
public class ServerData {

    private static ServerData serverData = null;

    private BTree bTree;
    private long time; // 执行最后一次插入操作的时间
    private List<LogFile> logFileList;

    private ServerData() {
        bTree = new BTree();
        time = 0L;
        logFileList = new ArrayList<>();
    }

    public static ServerData getInstance() {
        if (serverData == null) {
            serverData = new ServerData();
        }
        return serverData;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<LogFile> getLogFileList() {
        return logFileList;
    }

    /**
     * 插入信息并更新日志文件
     *
     * @param student
     */
    public void insert(Student student) {
        bTree.insert(student.getNumber(), student);
        log(System.currentTimeMillis(), ConvertStudent.convertStudentToJson(student));
    }

    /**
     * 查询信息
     *
     * @param number
     * @return
     */
    public Student search(String number) {
        return bTree.search(number);
    }

    /**
     * 记录日志文件
     *
     * @param time 操作时间戳
     * @param json 插入的学生数据
     */
    public void log(long time, String json) {
        this.setTime(time); // 更新时间戳
        LogFile logFile = new LogFile(time, json);
        logFileList.add(logFile);
    }

}
