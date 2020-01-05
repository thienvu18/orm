import mtkhdt.n9.connection.ConnectionProvider;
import mtkhdt.n9.connection.ConnectionType;

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

        List<Student> students = (new Student())
                .fetch();
        System.out.println(students.toString());

//        Student student = new Student();
//        student.mssv = "3";
//        student.name = "Thien Vu";
//        student.create();
//        System.out.println(student.toString());
//
////        Student student = new Student();
////        student.mssv = "2";
////        student.name = "Hai Anh";;
////        student.create();
////        System.out.println(student.toString());
//
//        //vi du xoa theo dieu kien where
//        List<Student> students = (new Student())
//                .where("name", CompareOperator.EQUAL, "Hai Au")
//                .orWhere("name", CompareOperator.EQUAL, "Hai Anh")
//                .fetch();

    }
}

// Online MySQL
//Username: fV4cmPHiZn
//Database name: fV4cmPHiZn
//Password: o26hN4AJlc
//Server: remotemysql.com
//Port: 3306