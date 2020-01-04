package mtkhdt.n9.connection;

import mtkhdt.n9.query.InsertQuery;
import mtkhdt.n9.query.ModifyQuery;
import mtkhdt.n9.query.SelectQuery;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

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

    @Override
    protected String compileInsertQuery(InsertQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(query.getTableName());

        Map<String, Object> columnsValue = query.getColumnsData();

        sql.append("(");
        columnsValue.forEach((column, value) -> {
            sql.append(column);
            sql.append(",");
        });
        sql.setLength(sql.length() - 1);
        sql.append(") ");

        sql.append("VALUES (");
        columnsValue.forEach((column, value) -> {
            sql.append("'");
            sql.append(value);
            sql.append("',");
        });
        sql.setLength(sql.length() - 1);
        sql.append(");");

        System.out.println(sql.toString());

        return sql.toString();
    }

    @Override
    protected String compileSelectQuery(SelectQuery query) {
        return null;
    }

    @Override
    protected String compileModifyQuery(ModifyQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(query.getTableName()).append(" SET ");
        Map<String, Object> updateColumns = query.getColumnsData();

        updateColumns.forEach((column, value) -> {
            sql.append(column);
            sql.append("=");
            sql.append("'");
            sql.append(value);
            sql.append("', ");
        });
        sql.setLength(sql.length() - 2);
        sql.append(";");

        System.out.println(sql.toString());
        return sql.toString();
    }
}
