package mtkhdt.n9.database;

import mtkhdt.n9.dialect.Dialect;
import mtkhdt.n9.dialect.DialectType;
import mtkhdt.n9.dialect.MySqlDialect;

import java.sql.*;

public class Connector {
    private String connectionString;
    private String username;
    private String password;
    private String driverClass;
    private Dialect dialect;
    Connection connection;

    private static Connector instance;

    private Connector() {
        this.connectionString = "";
        driverClass = "com.mysql.cj.jdbc.Driver";
        this.dialect = new MySqlDialect();
    }

    private Connection getDbConnection() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        Class.forName(driverClass);
        connection = DriverManager.getConnection(connectionString, username, password);
        return connection;
    }

    public static synchronized Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    public void setConnectionInfo(String host, int port, String dbname, String user, String password, DialectType dialectType) {
        switch (dialectType) {
            case MYSQL: {
                this.connectionString = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
                this.username = user;
                this.password = password;
                this.driverClass = "com.mysql.cj.jdbc.Driver";
                this.dialect = new MySqlDialect();
                break;
            }
        }
    }

    public void open() throws SQLException, ClassNotFoundException {
        connection = getDbConnection();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(sql);
        ResultSet resultSet = pst.executeQuery();
        return resultSet;
    }

    public long executeUpdate(String sql, boolean returnGeneratedKeys) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        pst.executeUpdate();
        pst.executeUpdate();
        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            long generatedId = rs.getLong(1);
            return generatedId;
        }
        return 0;
    }
}
