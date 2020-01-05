import mtkhdt.n9.connection.ConnectionProvider;
import mtkhdt.n9.connection.ConnectionType;
import mtkhdt.n9.model.BinaryQueryClause;
import mtkhdt.n9.model.Model;
import mtkhdt.n9.model.MonoQueryClause;
import mtkhdt.n9.model.QueryClause;
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
        student.mssv = "1";
        student.name = "Hai Au";
        student.create();
        System.out.println(student.toString());

        Student2 student2 = new Student2();
        student2.mssv = "2";
        student2.name = "Hai Anh";;
        student2.create();
        System.out.println(student2.toString());


        //demo 1 cau query cho delete voi where co and va or
        QueryClause a = new MonoQueryClause("mssv", CompareOperator.EQUAL, 2);
        QueryClause b = new MonoQueryClause("name", CompareOperator.EQUAL, "Hai Anh");
        QueryClause c = new MonoQueryClause("name", CompareOperator.EQUAL, "Hai Au");

        QueryClause d = new BinaryQueryClause(a, CompareOperator.AND, b);
        QueryClause e = new BinaryQueryClause(d, CompareOperator.OR, c);

        //vi du xoa theo dieu kien where
        Model.table(Student2.class)
                .where(e)
                .delete();

        //vi du xoa het bang
        Model.table(Student2.class)
                    .delete();
    }
}
