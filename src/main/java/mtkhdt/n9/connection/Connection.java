package mtkhdt.n9.connection;

import mtkhdt.n9.model.QueryClause;
import mtkhdt.n9.query.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return sql.toString();
    }

    protected String compileSelectQuery(SelectQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        // FROM
        sql.append(query.getTableName());
        // WHERE
        QueryClause whereClause = query.getWhereClause();

        if (whereClause != null) {
            String whereClauseSQL = whereClause.buildSqlClause();
            if (whereClauseSQL != null && !whereClauseSQL.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(whereClauseSQL);
            }
        }
        // GROUP BY
        if (query.getGroupByColumns().size() != 0) {
            String groupByValue = String.join(", ", query.getGroupByColumns());
            sql.append(" GROUP BY ").append(groupByValue);
        }

        // HAVING
        if (query.getHavingClause() != null) {
            Triplet<Pair<HavingFunction, String>, CompareOperator, Object> havingValue = query.getHavingClause();
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

    protected String compileUpdateQuery(ModifyQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(query.getTableName()).append(" SET ");
        Map<String, Object> updatedColumns = query.getColumnsData();

        updatedColumns.forEach((column, value) -> {
            sql.append(column);
            sql.append("=");
            sql.append("'");
            sql.append(value);
            sql.append("', ");
        });
        sql.setLength(sql.length() - 2);

//        Set<Triplet<String, CompareOperator, Object>> whereParams = query.getWhereParams();
//        Set<Triplet<String, CompareOperator, Object>> orWhereParams = query.getOrWhereParams();
//
//        sql.append(" WHERE 1 = 1");
//        if (!whereParams.isEmpty()) {
//            sql.append(" AND (");
//            whereParams.forEach((param) ->{
//                sql.append(param.getValue0());
//                sql.append(query.getOperatorFromEnum(param.getValue1()));
//                sql.append("'");
//                sql.append(param.getValue2());
//                sql.append("' AND ");
//            });
//            sql.setLength(sql.length() - 5);
//            sql.append(")");
//        }
//
//        if (!orWhereParams.isEmpty()) {
//            sql.append(" OR (");
//            orWhereParams.forEach((param) ->{
//                sql.append(param.getValue0());
//                sql.append(query.getOperatorFromEnum(param.getValue1()));
//                sql.append("'");
//                sql.append(param.getValue2());
//                sql.append("' AND ");
//            });
//            sql.setLength(sql.length() - 5);
//            sql.append(")");
//        }
//        sql.append(";");

        QueryClause whereClause = query.getWhereClause();

        if (whereClause != null) {
            String whereClauseSQL = whereClause.buildSqlClause();
            if (whereClauseSQL != null && !whereClauseSQL.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(whereClauseSQL);
            }
        }

        return sql.toString();
    }

//    protected String compileDeleteQuery(ModifyQuery query) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("UPDATE ").append(query.getTableName()).append(" SET ");
//        Map<String, Object> updateColumns = query.getColumnsData();
//
//        updateColumns.forEach((column, value) -> {
//            sql.append(column);
//            sql.append("=");
//            sql.append("'");
//            sql.append(value);
//            sql.append("', ");
//        });
//        sql.setLength(sql.length() - 2);
//        sql.append(";");
//
//        System.out.println(sql.toString());
//        return sql.toString();
//    }

    protected String compileDeleteQuery(DeleteQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(query.getTableName());
        QueryClause whereClause = query.getWhereClause();

        if (whereClause != null) {
            String whereClauseSQL = whereClause.buildSqlClause();
            if (whereClauseSQL != null && !whereClauseSQL.isEmpty()) {
                sql.append(" WHERE ");
                sql.append(whereClauseSQL);
            }
        }

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

    public List<Map<String, Object>> executeSelectQuery(SelectQuery query) throws SQLException, ClassNotFoundException {
        open();

        String sql = compileSelectQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(50);
        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }

        close();
        return list;
    }

    public long executeUpdateQuery(ModifyQuery query) throws SQLException, ClassNotFoundException {
        open();

        String sql = compileUpdateQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        long rows = preparedStatement.executeUpdate();

        close();
        return rows;
    }

//    public long executeDeleteQuery(ModifyQuery query) throws SQLException, ClassNotFoundException {
//        open();
//
//        String sql = compileDeleteQuery(query);
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        long rows = preparedStatement.executeUpdate();
//
//        close();
//        return rows;
//    }

    public long executeDeleteQuery(DeleteQuery query) throws SQLException, ClassNotFoundException {
        open();
        String sql = compileDeleteQuery(query);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        long rows = preparedStatement.executeUpdate();
        close();
        return rows;
    }
}
