package mtkhdt.n9.database;

import mtkhdt.n9.dialect.Dialect;
import mtkhdt.n9.dialect.MySqlDialect;
import mtkhdt.n9.query.Query;

import java.sql.*;

public class Connector {
    private static String url;
    private static String user;
    private static String password;
    private static Dialect dialect;

    private static Connection getDbConnection() throws ConnectorNotConfigException, SQLException {
        if (dialect == null) {
            throw new ConnectorNotConfigException("Dialect is null");
        }

        if ( url == null) {
            throw new ConnectorNotConfigException("Url is null");
        }

        Connection connection = null;

        if (dialect.getClass().equals(MySqlDialect.class)) {
            if (user == null) {
                throw new ConnectorNotConfigException("SQL connection require username");
            }

            connection = DriverManager.getConnection(url, user, password);
        } //TODO: Check other dialect require

        return connection;
    }

    public static void setConnectionInfo(String url, String user, String password, Dialect dialect) {
        Connector.url = url;
        Connector.user = user;
        Connector.password = password;
        Connector.dialect = dialect;
    }

    public static ResultSet execute(Query query, boolean returnGeneratedKeys) throws SQLException, ConnectorNotConfigException {
        query.setDialect(dialect);
        String sql = query.toSqlString();
        Connection conn = getDbConnection();
        PreparedStatement pst = conn.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        ResultSet resultSet = pst.executeQuery();
        conn.close();
        return resultSet;
    }
}
