import mtkhdt.n9.connection.ConnectionProvider;
import mtkhdt.n9.connection.ConnectionType;
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

        List<Student> students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());

        //Demo insert
        Student student = new Student();
        student.mssv = "4";
        student.name = "Thien Vu";
        student.create();
        System.out.println("Recently inserted: " + student.toString());
        students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());

        //Demo self update
        student.name = "Bich Van";
        student.save();
        System.out.println("Recently updated: " + student.toString());
        students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());

        //Demo patch update
        Student demoUpdateModel = new Student();
        demoUpdateModel.name = "Demo update guy";
        long rowsAffected = demoUpdateModel
                .where("mssv", CompareOperator.EQUAL, "4")
                .orWhere("mssv", CompareOperator.EQUAL, "1").update();
        System.out.println("Rows affected: " + rowsAffected);
        students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());

        //Demo self destroy
        (new Student()).where("mssv", CompareOperator.EQUAL, "4").remove();
        students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());

        Student demoDeleteModel = new Student();
        rowsAffected = demoUpdateModel.where("mssv", CompareOperator.NOT_EQUAL, "").delete();
        System.out.println("Rows affected: " + rowsAffected);
        students = (new Student())
                .fetch();
        System.out.println("Current student list: " + students.toString());
    }
}

// Online MySQL
//Username: fV4cmPHiZn
//Database name: fV4cmPHiZn
//Password: o26hN4AJlc
//Server: remotemysql.com
//Port: 3306