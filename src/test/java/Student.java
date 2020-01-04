import mtkhdt.n9.annotation.Column;
import mtkhdt.n9.annotation.PrimaryKey;
import mtkhdt.n9.annotation.Table;
import mtkhdt.n9.model.Model;

@Table(name = "students")
public class Student extends Model {
    @Column
    String mssv;

    @Column
    String name;

    @Override
    public String toString() {
        return "MSSV: "+ mssv + ", Name: " + name;
    }
}
