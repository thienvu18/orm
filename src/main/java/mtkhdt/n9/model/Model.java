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
import java.sql.SQLException;
import java.util.*;

public class Model {
    protected String tableName;
    private String pkColumn;
    private boolean pkAutoIncrement;
    private Map<String, Object> columnsData;
    private Map<String, Object> oldColumnsData;

    private ArrayList<String> groupByColumns;
    //    private Set<Triplet<String, CompareOperator, Object>> whereParams;
//    private Set<Triplet<String, CompareOperator, Object>> orWhereParams;
//    private Set<Triplet<String, CompareOperator, Object>> havingParams;
//    private Set<Triplet<String, CompareOperator, Object>> orHavingParams;
    private QueryClause whereClause;
    private Triplet<Pair<HavingFunction, String>, CompareOperator, Object> havingClause;


    public Model() {
        columnsData = new LinkedHashMap<>();
        initQueryParams();
        setModelSchemaFromAnnotations();
    }

    //Get list object
    public <T extends Model> List<T> fetch() throws SQLException, ClassNotFoundException {
        SelectQuery query = buildSelectQuery();
        List<Map<String, Object>> list = ConnectionProvider.getInstance().getConnection().executeSelectQuery(query);
        ArrayList<T> models = new ArrayList<>();

        for (Map<String, Object> row : list) {
            T model = newInstance();
            model.tableName = tableName;
            model.mapListResultToColumnsData(row);
            model.mapColumnsDataToField();
            models.add(model);
        }

        resetQueryParams();
        return models;
    }

    //Update list object
    public long update() throws SQLException, ClassNotFoundException {
        mapFieldToColumnsData();
        ModifyQuery query = buildModifyQuery();
        long rows = ConnectionProvider.getInstance().getConnection().executeUpdateQuery(query);
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

        if (columnsData.size() == 0) throw new RuntimeException("No data to create");

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
        List<Map<String, Object>> list = ConnectionProvider.getInstance().getConnection().executeSelectQuery(query);
        if (list.size() >= 1) {
            mapListResultToColumnsData(list.get(0));
        }
        mapColumnsDataToField();
        resetQueryParams();
        return (T) this;
    }

    //Update current object to DB
    public <T extends Model> T save() throws SQLException, ClassNotFoundException {
        makeWhereParamForSelfUpdate();
        update();
        return (T) this;
    }

    //Delete current object
    public void remove() throws SQLException, ClassNotFoundException {
        resetQueryParams();
        makeWhereParamForSelfUpdate();
        delete();
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
        } else {
            QueryClause temp = new MonoQueryClause(column, operator, value);
            whereClause = new BinaryQueryClause(temp, CompareOperator.AND, whereClause);
        }

        return (T) this;
    }

    public <T extends Model> T orWhere(String column, CompareOperator operator, Object value) {
        if (whereClause == null) {
            whereClause = new MonoQueryClause(column, operator, value);
        } else {
            QueryClause temp = new MonoQueryClause(column, operator, value);
            whereClause = new BinaryQueryClause(temp, CompareOperator.OR, whereClause);
        }

        return (T) this;
    }

//    public <T extends Model> T orWhere(String column, CompareOperator operator, Object value) {
//        if (columnsData.containsKey(column)) {
//            orWhereParams.add(new Triplet<>(column, operator, value));
//        }
//
//        return (T) this;
//    }

//    public <T extends Model> T having(String column, CompareOperator operator, Object value) {
//        if (columnsData.containsKey(column)) {
//            havingParams.add(new Triplet<>(column, operator, value));
//        }
//        return (T) this;
//    }

//    public <T extends Model> T orHaving(String column, CompareOperator operator, Object value) {
//        if (columnsData.containsKey(column)) {
//            orHavingParams.add(new Triplet<>(column, operator, value));
//        }
//
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
        return new SelectQuery(tableName, columnsData, groupByColumns, whereClause, havingClause);
    }

    private ModifyQuery buildModifyQuery() {
        //TODO: Build update, delete query
        return new ModifyQuery(tableName, getModifiedColumnsValue(), whereClause);
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
//        whereParams = new HashSet<>();
//        havingParams = new HashSet<>();
//        orWhereParams = new HashSet<>();
//        orHavingParams = new HashSet<>();
        havingClause = null;
        whereClause = null;
    }

    private void resetQueryParams() {
        groupByColumns.clear();
//        whereParams.clear();
//        havingParams.clear();
//        orWhereParams.clear();
//        orHavingParams.clear();
        havingClause = null;
        whereClause = null;
    }

    private void makeWhereParamForSelfUpdate() {
//        whereParams.clear();
//        whereParams.add(new Triplet<>(pkColumn, CompareOperator.EQUAL, columnsData.get(pkColumn)));
        this.where(pkColumn, CompareOperator.EQUAL, columnsData.get(pkColumn));
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

    private Map<String, Object> getModifiedColumnsValue() {
        if (oldColumnsData == null) return new LinkedHashMap<>(columnsData);

        Map<String, Object> modifiedColumnsValue = new LinkedHashMap<>();

        columnsData.forEach((column, data) -> {
            Object oldData = oldColumnsData.get(column);
            if (data != null && !data.equals(oldData)) {
                modifiedColumnsValue.put(column, data);
            }
        });

        return modifiedColumnsValue;
    }

    private void mapFieldToColumnsData() {
        oldColumnsData = new LinkedHashMap<>(columnsData);
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

    protected void mapListResultToColumnsData(Map<String, Object> row) throws SQLException {
        columnsData = new HashMap<>(row);
        oldColumnsData = new LinkedHashMap<>(columnsData);
    }
}
