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
                .setHost("remotemysql.com")
                .setPort(3306)
                .setDbName("fV4cmPHiZn")
                .setUsername("fV4cmPHiZn")
                .setPassword("o26hN4AJlc");

        Student2 student = new Student2();
//        student.mssv = "aaa123478";
//        student.name = "Thai Thien Vu";
//        System.out.println(student.create());
//        System.out.println(student.toString());
//
//        Student2 student2 = new Student2();
//        student2.mssv = "aaaaaaaa";
//        student2.name = "Vu Thai";
//        System.out.println(student2.create());
//        System.out.println(student2.toString());

        List<Student2> list = student.table("students2").where("mssv", CompareOperator.EQUAL, "aaaaaaaa").groupBy("mssv").fetch();
        System.out.println(list.toString());
    }
}

// Online MySQL
//Username: fV4cmPHiZn
//Database name: fV4cmPHiZn
//Password: o26hN4AJlc
//Server: remotemysql.com
//Port: 3306