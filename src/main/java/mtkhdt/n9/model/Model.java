package mtkhdt.n9.model;

import mtkhdt.n9.annotation.Column;
import mtkhdt.n9.annotation.PrimaryKey;
import mtkhdt.n9.annotation.Table;
import mtkhdt.n9.connection.ConnectionProvider;
import mtkhdt.n9.query.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class Model {
    protected String tableName;
    private String pkColumn;
    private boolean pkAutoIncrement;
    private Map<String, Object> columnsData;

    private Set<String> selectColumns;
    private ArrayList<String> groupByColumns;
    private Set<Triplet<String, CompareOperator, Object>> whereParams;
    private Set<Triplet<String, CompareOperator, Object>> havingParams;
    private QueryClause whereClause;
    private Triplet<Pair<HavingFunction ,String>, CompareOperator, Object> havingClause;

    public Model() {
        columnsData = new LinkedHashMap<>();
        initQueryParams();
        setModelSchemaFromAnnotations();
    }

    public Model(String tableName) {
        columnsData = new LinkedHashMap<>();
        initQueryParams();
        this.tableName = tableName;
    }

    public Model(Class c) {
        columnsData = new LinkedHashMap<>();
        initQueryParams();
        setModelSchemaFromAnnotations(c);
    }

    public static Model table(String tableName) {
        return new Model(tableName);
    }

    // Fix lại hàm table ở trên.  Phải truyền  model vào thì mới có columnData được
    public static Model table(Class c) {
        return new Model(c);
    }

    //Get list object
    public <T extends Model> List<T> fetch() throws SQLException, ClassNotFoundException {
        SelectQuery query = buildSelectQuery();
        ResultSet rs = ConnectionProvider.getInstance().getConnection().executeSelectQuery(query);
        ArrayList<T> models = new ArrayList<>();

        while (rs.next()) {
            T model = newInstance();
            model.tableName = tableName;
            model.mapResultSetToColumnsData(rs);
            model.mapColumnsDataToField();
            models.add(model);
        }

        resetQueryParams();
        return models;
    }

    //Update list object
    public long update() throws SQLException, ClassNotFoundException {
        ModifyQuery query = buildModifyQuery();
        long rows = ConnectionProvider.getInstance().getConnection().executeModifyQuery(query);
        //TODO: Map rs to list object

        resetQueryParams();
        return rows;
    }

    //Delete list object
    public long delete() throws SQLException, ClassNotFoundException {
        DeleteQuery query = buildDeleteQuery();
        long rows = ConnectionProvider.getInstance().getConnection().executeDeleteQuery(query);
        resetQueryParams();
        return rows;
    }


//    //Get an object
//    public <T extends Model> T find(Object pkKeyValue) throws SQLException, ClassNotFoundException {
//        //TODO: Set condition to this ID;
//        SelectQuery query = buildSelectQuery();
//        ResultSet rs = ConnectionProvider.getInstance().getConnection().executeSelectQuery(query);
//        mapResultSetToColumnsData(rs);
//        mapColumnsDataToField();
//        return (T) this;
//    }


    //Insert current object
    public <T extends Model> T create() throws SQLException, ClassNotFoundException {
        mapFieldToColumnsData();
        InsertQuery query = buildInsertQuery();
        if (pkAutoIncrement) {
            long id = ConnectionProvider.getInstance().getConnection().executeInsertQuery(query, true);
            columnsData.put(pkColumn, id);
        } else {
            ConnectionProvider.getInstance().getConnection().executeInsertQuery(query, false);
        }

        resetQueryParams();
        return (T) this;
    }

    //Update current object from DB
    public <T extends Model> T refresh() throws SQLException, ClassNotFoundException {
        makeWhereParamForSelfUpdate();
        SelectQuery query = buildSelectQuery();
        ResultSet rs = ConnectionProvider.getInstance().getConnection().executeSelectQuery(query);
        mapResultSetToColumnsData(rs);
        mapColumnsDataToField();
        resetQueryParams();
        return (T) this;
    }

    //Update current object to DB
    public <T extends Model> T save() throws SQLException, ClassNotFoundException {
        makeWhereParamForSelfUpdate();
        mapFieldToColumnsData();
        update();
        resetQueryParams();
        return (T) this;
    }

    //Delete current object
    public void remove() throws SQLException, ClassNotFoundException {
        makeWhereParamForSelfUpdate();
        resetQueryParams();
        resetQueryParams();
        delete();
    }


    public <T extends Model> T select(String... columns) {
        for (String column : columns) {
            if (columnsData.containsKey(column)) {
                selectColumns.add(column);
            }
        }

        return (T) this;
    }

    public <T extends Model> T groupBy(String... columns) {
        for (String column : columns) {
            if (columnsData.containsKey(column)) {
                groupByColumns.add(column);
            }
        }

        return (T) this;
    }

    public <T extends Model> T where(String column, CompareOperator operator, Object value) {
        if (whereClause == null) {
            whereClause = new MonoQueryClause(column, operator, value);
        }
        else {
            QueryClause temp = new MonoQueryClause(column, operator, value);
            whereClause = new BinaryQueryClause(temp, CompareOperator.AND,whereClause);
        }
//        if (columnsData.containsKey(column)) {
//            whereParams.add(new Triplet<>(column, operator, value));
//        }

        return (T) this;
    }

    public <T extends Model> T orWhere(String column, CompareOperator operator, Object value) {
        if (whereClause == null) {
            whereClause = new MonoQueryClause(column, operator, value);
        }
        else {
            QueryClause temp = new MonoQueryClause(column, operator, value);
            whereClause = new BinaryQueryClause(temp, CompareOperator.OR,whereClause);
        }
//        if (columnsData.containsKey(column)) {
//            whereParams.add(new Triplet<>(column, operator, value));
//        }

        return (T) this;
    }

    public <T extends Model> T where(QueryClause queryClause) {
        whereClause = queryClause;
        return (T) this;
    }

//    public <T extends Model> T having(String column, CompareOperator operator, Object value) {
//        if (columnsData.containsKey(column)) {
//            havingParams.add(new Triplet<>(column, operator, value));
//        }
//        return (T) this;
//    }

    public <T extends Model> T having(HavingFunction havingFunction, String column, CompareOperator operator, Object value) {
        if (columnsData.containsKey(column)) {
            havingClause = new Triplet<>(new Pair<HavingFunction, String>(havingFunction, column), operator, value);
        }
        return (T) this;
    }


    private InsertQuery buildInsertQuery() {
        //TODO: Build insert query
        return new InsertQuery(tableName, columnsData);
    }

    private SelectQuery buildSelectQuery() {
        //TODO: Build select query
        return new SelectQuery(tableName, columnsData, selectColumns, groupByColumns, whereClause, havingClause);
    }

    private ModifyQuery buildModifyQuery() {
        //TODO: Build update, delete query
        return new ModifyQuery(tableName, columnsData);
    }

    private DeleteQuery buildDeleteQuery() {
        //TODO: Build update, delete query
        return new DeleteQuery(tableName, whereClause);
    }

    private String getDbTableName(String objectTableName) {
        //TODO: Return plural form
        return objectTableName.toLowerCase();
    }

    private <T> T newInstance() throws ConstructionException {
        Constructor<? extends Model> declaredConstructor;

        try {
            declaredConstructor = getClass().getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ConstructionException("cannot find constructor for this class");
        }

        try {
            return (T) declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConstructionException("cannot make new instance of this class");
        }
    }

    private void initQueryParams() {
        groupByColumns = new ArrayList<>();
        selectColumns = new HashSet<>();
        whereParams = new HashSet<>();
        havingParams = new HashSet<>();
        havingClause = null;
        whereClause = null;
    }

    private void resetQueryParams() {
        groupByColumns.clear();
        selectColumns.clear();
        whereParams.clear();
        havingParams.clear();
        havingClause = null;
        whereClause = null;
    }

    private void makeWhereParamForSelfUpdate() {
        whereParams.clear();
        whereParams.add(new Triplet<>(pkColumn, CompareOperator.EQUAL, columnsData.get(pkColumn)));
    }

    private void setModelSchemaFromAnnotations(Class c) {
        for (; c != null && c != Object.class; c = c.getSuperclass()) {
            if (c.isAnnotationPresent(Table.class)) {
                Annotation annotation = c.getAnnotation(Table.class);
                Table bc = (Table) annotation;
                this.tableName = getDbTableName(bc.name());
            }

            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String column = field.getName().toLowerCase();

                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        this.pkColumn = column;
                    }

                    this.columnsData.put(column, null);
                }
            }
        }

        if (pkColumn == null) {
            this.pkAutoIncrement = true;
            this.pkColumn = "id";
            this.columnsData.put(pkColumn, 0);
        }
    }


    private void setModelSchemaFromAnnotations() {
        for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            if (c.isAnnotationPresent(Table.class)) {
                Annotation annotation = c.getAnnotation(Table.class);
                Table bc = (Table) annotation;
                this.tableName = getDbTableName(bc.name());
            }

            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String column = field.getName().toLowerCase();

                    if (field.isAnnotationPresent(PrimaryKey.class)) {
                        this.pkColumn = column;
                    }

                    this.columnsData.put(column, null);
                }
            }
        }

        if (pkColumn == null) {
            this.pkAutoIncrement = true;
            this.pkColumn = "id";
            this.columnsData.put(pkColumn, 0);
        }
    }

    private void mapFieldToColumnsData() {
        for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getName().toLowerCase();
                    try {
                        boolean accessible = field.canAccess(this);
                        field.setAccessible(true);
                        Object val = field.get(this);
                        columnsData.put(columnName, val);
                        field.setAccessible(accessible);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
        }
    }

    protected void mapColumnsDataToField() {
        String[] columns = columnsData.keySet().toArray(new String[0]);

        for (String columnName : columns) {
            for (Class c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    Field field = c.getDeclaredField(columnName);

                    boolean accessible = field.canAccess(this);
                    field.setAccessible(true);
                    field.set(this, columnsData.get(columnName));
                    field.setAccessible(accessible);
                    break;
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void mapResultSetToColumnsData(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        for (int col = 1; col <= cols; col++) {
            String table = meta.getTableName(col).toLowerCase();
            String key = meta.getColumnName(col).toLowerCase();
            int type = meta.getColumnType(col);
            Object val;

            // TODO need more
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

            if (table.equals(tableName)) {
                columnsData.put(key, val);
            }
        }
    }
}
