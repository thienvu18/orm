package mtkhdt.n9.connection;

import mtkhdt.n9.model.QueryClause;
import mtkhdt.n9.query.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

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
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        // FROM
        sql.append(query.getTableName());
        // WHERE
        QueryClause whereClause = query.getWhereClause();

        if (whereClause != null) {
            String  whereClauseSQL =  whereClause.buildSqlClause();
            if (whereClauseSQL != null && whereClauseSQL.isEmpty() == false) {
                sql.append(" WHERE ");
                sql.append(whereClauseSQL);
            }
        }
        // GROUP BY
        if (query.getGroupByColumns().size() != 0) {
            String groupByValue =  String.join(", ", query.getGroupByColumns());
            sql.append(" GROUP BY ").append(groupByValue);
        }

        // HAVING
        if (query.getHavingClause() != null) {
            Triplet<Pair<HavingFunction ,String>, CompareOperator, Object> havingValue = query.getHavingClause();
            String operator = "";
            switch (havingValue.getValue1()) {
                case EQUAL:
                    operator = "=";
            }
            Pair<HavingFunction, String> columnFunction = havingValue.getValue0();
            switch (columnFunction.getValue0()) {
                case AVG:
                    sql.append(" HAVING ").append("AVG(").append(columnFunction.getValue1()).append(")").append(operator).append(havingValue.getValue2());
                    break;
                case MAX:
                    sql.append(" HAVING ").append("MAX(").append(columnFunction.getValue1()).append(")").append(operator).append(havingValue.getValue2());
                    break;
                case MIN:
                    sql.append(" HAVING ").append("MIN(").append(columnFunction.getValue1()).append(")").append(operator).append(havingValue.getValue2());
                    break;
                case COUNT:
                    sql.append(" HAVING ").append("COUNT(").append(columnFunction.getValue1()).append(")").append(operator).append(havingValue.getValue2());
                    break;
                case SUM:
                    sql.append(" HAVING ").append("SUM(").append(columnFunction.getValue1()).append(")").append(operator).append(havingValue.getValue2());
                    break;
            }
        }
        return sql.toString();
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
    
    @Override
    protected String compileDeleteQuery(DeleteQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(query.getTableName());
        QueryClause whereClause = query.getWhereClause();

        if (whereClause != null) {
            String  whereClauseSQL =  whereClause.buildSqlClause();
            if (whereClauseSQL != null && whereClauseSQL.isEmpty() == false) {
                sql.append(" WHERE ");
                sql.append(whereClauseSQL);
            }
        }

        return sql.toString();
    }
}
