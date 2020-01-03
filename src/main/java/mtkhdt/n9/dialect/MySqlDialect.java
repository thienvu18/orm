package mtkhdt.n9.dialect;

public class MySqlDialect implements Dialect{
    @Override
    public String getConnectionString(String host, int port, String dbname) {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbname;
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
