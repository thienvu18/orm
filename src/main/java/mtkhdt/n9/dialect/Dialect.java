package mtkhdt.n9.dialect;

import mtkhdt.n9.query.Query;

import java.util.Map;

public interface  Dialect {
    String getConnectionString(String host, int port, String dbname);

    String getDriverClass();

    default String select(Query query) {
        StringBuilder sql = new StringBuilder();
        //Get value from query to build string
        return sql.toString();
    }

    default String insert(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(query.getTableName());

        Map<String, Object> columnsValue = query.getUpdateColumns();

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

    default String update(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(query.getTableName()).append(" SET ");
        Map<String, Object> updateColumns = query.getUpdateColumns();

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

    default String delete(Query query) {
        StringBuilder sql = new StringBuilder();

        return sql.toString();
    }
}
