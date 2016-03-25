/**
 * Created by Silocean on 2016-03-18.
 */
public class Student {
    private String number;
    private String name;
    private int balance;

    public Student(String number, String name, int balance) {
        this.number = number;
        this.name = name;
        this.balance = balance;
    }

    public Student() {
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Student{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
