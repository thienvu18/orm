import mtkhdt.n9.connection.ConnectionProvider;
import mtkhdt.n9.connection.ConnectionType;
import mtkhdt.n9.model.Model;
import mtkhdt.n9.query.CompareOperator;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        ConnectionProvider
                .getInstance()
                .setConnectionType(ConnectionType.MYSQL)
                .setHost("localhost")
                .setPort(3306)
                .setDbName("test")
                .setUsername("root")
                .setPassword("");

        Student2 student = new Student2();
        student.mssv = "aaa";
        student.name = "Thai Thien Vu";
        System.out.println(student.create());
        System.out.println(student.toString());

        Student2 student2 = new Student2();
        student2.mssv = "aaaaaaaa";
        student2.name = "Vu Thai";
        System.out.println(student2.create());
        System.out.println(student2.toString());

        List<Student2> list = Model.table("students").where("mssv", CompareOperator.EQUAL, "aaaaaaaa").fetch();
        System.out.println(list.toString());
    }
}
