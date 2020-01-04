package mtkhdt.n9.connection;

public class ConnectionProvider {
    private static ConnectionProvider instance = null;

    private ConnectionType connectionType;
    private String host;
    private int port;
    private String dbname;
    private String username;
    private String password;

    public static ConnectionProvider getInstance() {
        if (instance == null) {
            instance = new ConnectionProvider();
        }
        return instance;
    }

    public ConnectionProvider setHost(String host) {
        getInstance().host = host;
        return instance;
    }

    public ConnectionProvider setPort(int port) {
        getInstance().port = port;
        return instance;
    }

    public ConnectionProvider setDbName(String dbname) {
        getInstance().dbname = dbname;
        return instance;
    }

    public ConnectionProvider setUsername(String username) {
        getInstance().username = username;
        return instance;
    }

    public ConnectionProvider setPassword(String password) {
        getInstance().password = password;
        return instance;
    }

    public ConnectionProvider setConnectionType(ConnectionType connectionType) {
        getInstance().connectionType = connectionType;
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        switch (connectionType) {
            case MYSQL:
                connection = new MySqlConnection(host, port, dbname, username, password);
                break;
            case MSSQL:
                break;
            case POSTGRES:
                break;
        }

        return connection;
    }
}
