package mtkhdt.n9.connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnection extends Connection {
    private String connectionString;
    private String username;
    private String password;

    public MySqlConnection(String host, int port, String dbname, String username, String password) {
        this.username = username;
        this.password = password;
        this.connectionString = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
    }

    @Override
    public void open() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(connectionString, username, password);
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
