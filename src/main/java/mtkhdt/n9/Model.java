package mtkhdt.n9;

import mtkhdt.n9.annotation.Column;
import mtkhdt.n9.annotation.Table;
import mtkhdt.n9.database.Connector;
import mtkhdt.n9.query.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    private String tableName;
    private Map<String, Object> columnsValue;
    private Query.QueryBuilder queryBuilder;

    public Model(String tableName) {
        this.tableName = tableName.toLowerCase();
        columnsValue = new HashMap<>();
        queryBuilder = new Query.QueryBuilder(Connector.getInstance().getDialect(), tableName);
    }

    public Model() {
        for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            if (c.isAnnotationPresent(Table.class)) {
                Annotation annotation = c.getAnnotation(Table.class);
                Table bc = (Table) annotation;
                this.tableName = bc.name().toLowerCase();
                break;
            }
        }
        columnsValue = new HashMap<>();
        queryBuilder = new Query.QueryBuilder(Connector.getInstance().getDialect(), tableName);
    }

    public static Model table(String tableName) {
        return new Model(tableName);
    }

    private void getValueFromResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        for (int col = 1; col <= cols; col++) {
            String table = meta.getTableName(col).toLowerCase();
            String key = meta.getColumnName(col).toLowerCase();
            int type = meta.getColumnType(col);
            Object val;

            switch (type) {
                case Types.BIT:
                case Types.TINYINT:
                case Types.BOOLEAN:
                    val = rs.getBoolean(col);
                    break;

                case Types.SMALLINT:
                case Types.INTEGER:
                    val = rs.getInt(col);
                    break;

                case Types.BIGINT:
                    val = rs.getLong(col);
                    break;

                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    val = rs.getString(col);
                    break;

                case Types.TIMESTAMP:
                    val = rs.getTimestamp(col);
                    break;

                default:
                    throw new RuntimeException(String.format("Unknown column type! %s(%d) %s on column %s",
                            meta.getColumnTypeName(col), type, meta.getColumnClassName(col), key));
            }

            if (!table.equals(tableName) && table.length() > 0) {
                key = String.format("%s.%s", table, key);
            }
            columnsValue.put(key, val);
        }
    }

    private void columnValuesFromAnnotation() {
        for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column bc = field.getAnnotation(Column.class);
                    String name = bc.name();
                    if (name.equals("")) name = field.getName();

                    name = name.toLowerCase();

                    try {
                        Object val = field.get(this);
                        put(name, val);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }
    }

    private void columnValuesToAnnotation() {
        for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {

                    Column bc = field.getAnnotation(Column.class);
                    String name = bc.name();
                    if (name.equals("")) name = field.getName();

                    name = name.toLowerCase();
                    try {
                        Object val = get(name);
                        field.set(this, val);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }
    }


    /**
     * @return generatedId if exists, otherwise 0
     */
    public long create() throws Exception {
        columnValuesToAnnotation();
        String sql = queryBuilder.setUpdateColumns(columnsValue).build().buildInsertQuery();
        long id = Connector.getInstance().executeUpdate(sql, true);
        return id;
    }

    /**
     * @return affected row count
     */
    public long update() throws Exception {
        return 0;
    }

    /**
     * @return affected row count
     */
    public long delete() throws Exception {
        return 0;
    }

    public <T extends Model> T select(String... columnsName) {
        return (T) this;
    }

    public <T extends Model> T where(String clause, Object... args) {
        return (T) this;
    }

    public <T extends Model> T groupBy(String clause, Object... args) {
        return (T) this;
    }

    public <T extends Model> T having(String clause, Object... args) {
        return (T) this;
    }

    public <T extends Model> T orderBy(String clause, Object... args) {
        return (T) this;
    }

//    public <T extends Model> T raw(String clause) {
//        return (T) this;
//    }

    public <T extends Model> List<T> get() throws Exception {
        return null;
    }

    public <T extends Model> T first() throws Exception {
        List<Model> data = get();
        return data.isEmpty() ? null : (T) data.get(0);
    }

    public Map<String, Object> getColumnValues() {
        return null;
    }

    public void setColumnValues(Map<String, Object> columnValues) {

    }

    public <T extends Model> T put(String key, Object value) {
        columnsValue.put(key, value);
        return (T) this;
    }

    public Object get(String columnName) {
        return columnsValue.get(columnName);
    }

    public String getString(String columnName) {
        return (String) get(columnName);
    }

    public int getInt(String columnName) {
        return (int) get(columnName);
    }

    public long getLong(String columnName) {
        return (long) get(columnName);
    }

    public String getTableName() {
        return tableName;
    }
}
