package mtkhdt.n9.connection;

import mtkhdt.n9.query.InsertQuery;
import mtkhdt.n9.query.ModifyQuery;
import mtkhdt.n9.query.SelectQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Connection {
    protected java.sql.Connection connection;

    protected abstract void open() throws ClassNotFoundException, SQLException;

    protected abstract void close() throws SQLException;

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

    protected String compileSelectQuery(SelectQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (query.getSelectColumns().size() == 0) {
            sql.append('*');
        }
        else {
            String selectValue = String.join(", ", query.getSelectColumns());
            sql.append(selectValue);
        }
        // From
        sql.append(" FROM ").append(query.getTableName());

        // Group by
        if (query.getGroupByColumns().size() != 0) {
            String groupByValue = String.join(", ", query.getGroupByColumns());
            sql.append(" GROUP BY ").append(groupByValue);

        }
        return sql.toString();
    }

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

    public long executeInsertQuery(InsertQuery query, boolean returnGeneratedKeys) throws SQLException, ClassNotFoundException {
        open();

        String sql = compileInsertQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        long id = 0;

        if (returnGeneratedKeys) {
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        }

        close();
        return id;
    }

    public ResultSet executeSelectQuery(SelectQuery query) throws SQLException, ClassNotFoundException {
        open();

        String sql = compileSelectQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();

        close();
        return rs;
    }

    public long executeModifyQuery(ModifyQuery query) throws SQLException, ClassNotFoundException {
        open();

        String sql = compileModifyQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        long rows = preparedStatement.executeUpdate();

        close();
        return rows;
    }
}
