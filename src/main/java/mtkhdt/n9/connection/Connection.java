package mtkhdt.n9.connection;

import mtkhdt.n9.query.InsertQuery;
import mtkhdt.n9.query.ModifyQuery;
import mtkhdt.n9.query.SelectQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Connection {
    protected java.sql.Connection connection;
    protected abstract void open() throws ClassNotFoundException, SQLException;
    protected abstract void close() throws SQLException;
    protected abstract String compileInsertQuery(InsertQuery query);
    protected abstract String compileSelectQuery(SelectQuery query);
    protected abstract String compileModifyQuery(ModifyQuery query);

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
