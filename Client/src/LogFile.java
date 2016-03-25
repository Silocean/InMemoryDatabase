/**
 * Created by Silocean on 2016-03-20.
 */
public class LogFile {

    private long time;
    private String operation;

    public LogFile(long time, String operation) {
        this.time = time;
        this.operation = operation;
    }

    public LogFile() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "LogFile{" +
                "time=" + time +
                ", operation='" + operation + '\'' +
                '}';
    }
}
