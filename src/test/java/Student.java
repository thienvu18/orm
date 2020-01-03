import mtkhdt.n9.Model;
import mtkhdt.n9.annotation.Column;
import mtkhdt.n9.annotation.Table;

@Table(name = "students")
public class Student extends Model {
    @Column(name="mssv")
    String mssv;

    @Column(name="name")
    String name;

    @Override
    public String toString() {
        return "MSSV: "+ mssv + ", Name: " + name;
    }
}
