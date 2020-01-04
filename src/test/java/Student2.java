import mtkhdt.n9.model.Model;
import mtkhdt.n9.annotation.Column;
import mtkhdt.n9.annotation.PrimaryKey;
import mtkhdt.n9.annotation.Table;

@Table(name = "students2")
public class Student2 extends Model {
    @Column
    @PrimaryKey
    String mssv;

    @Column
    String name;

    @Override
    public String toString() {
        return "MSSV: " + mssv + ", Name: " + name;
    }
}
