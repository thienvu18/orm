import mtkhdt.n9.database.Connector;
import mtkhdt.n9.dialect.DialectType;

public class Main {

    public static void main(String[] args) throws Exception {
        Connector.getInstance().setConnectionInfo("localhost", 3306, "test", "root", "", DialectType.MYSQL);
        Connector.getInstance().open();

        Student student = new Student();
        student.put("mssv", "1612827");
        student.put("name", "Thai Thien Vu");
        System.out.println(student.create());

        System.out.println(student.toString());

        Connector.getInstance().close();
    }
}
