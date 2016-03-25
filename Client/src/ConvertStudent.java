import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silocean on 2016-03-20.
 */
public class ConvertStudent {

    public static Student convertJsonToStudent(String json) {
        JSONObject object = JSONObject.fromObject(json);
        Student student = new Student();
        student.setNumber(object.getString("number"));
        student.setName(object.getString("name"));
        student.setBalance(object.getInt("balance"));
        return student;
    }

    public static String convertStudentToJson(Student student) {
        return JSONObject.fromObject(student).toString();
    }

    public static String convertStudentsToJson(List<Student> list) {
        return JSONArray.fromObject(list).toString();
    }

    public static List<Student> convertJsonToStudents(String json) {
        JSONArray array = JSONArray.fromObject(json);
        List<Student> list = JSONArray.toList(array, Student.class);
        return list;
    }

    /**
     * 从文件读取json字符串并解析成student对象
     *
     * @param path
     * @return
     */
    public static List<Student> getStudentsFromJsonFile(String path) {
        List<Student> list = new ArrayList<>();
        try {
            BufferedReader bis = new BufferedReader(new FileReader("students.txt"));
            String line;
            while ((line = bis.readLine()) != null) {
                JSONObject object = JSONObject.fromObject(line);
                Student student = new Student();
                student.setNumber(object.getString("number"));
                student.setName(object.getString("name"));
                student.setBalance(object.getInt("balance"));
                list.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
