package mtkhdt.n9.query;

import mtkhdt.n9.dialect.Dialect;

import java.util.LinkedHashMap;
import java.util.Map;

public class Query {
    private final Dialect dialect;
    private final String selectColumns;
    private final String tableName;

    private final StringBuilder conditionSQL;
    private final Map<String, Object> updateColumns;
    private final String orderBy;
    private final String customSQL;

    private Query(Dialect dialect, String selectColumns, String tableName, StringBuilder conditionSQL, Map<String, Object> updateColumns, String orderBy, String customSQL) {
        this.dialect = dialect;
        this.selectColumns = selectColumns;
        this.tableName = tableName;
        this.conditionSQL = conditionSQL;
        this.updateColumns = updateColumns;
        this.orderBy = orderBy;
        this.customSQL = customSQL;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public StringBuilder getConditionSQL() {
        return conditionSQL;
    }

    public Map<String, Object> getUpdateColumns() {
        Map<String, Object> newMap = new LinkedHashMap<>(updateColumns.size());
        updateColumns.forEach(newMap::put);

        return newMap;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getCustomSQL() {
        return customSQL;
    }

    public String buildInsertQuery() {
        return dialect.insert(this);
    }

    public String buildSelectQuery() {
        return dialect.select(this);
    }

    public String buildUpdateQuery() {
        return dialect.update(this);
    }

    public String buildDeleteQuery() {
        return dialect.delete(this);
    }

    public static class QueryBuilder {
        private Dialect dialect;
        private String selectColumns;
        private String tableName;
        private StringBuilder conditionSQL;
        private Map<String, Object> updateColumns;
        private String orderBy;
        private String customSQL;

        public QueryBuilder(Dialect dialect, String tableName) {
            this.dialect = dialect;
            this.tableName = tableName;
        }

        public QueryBuilder setSelectColumns(String selectColumns) {
            this.selectColumns = selectColumns;
            return this;
        }

        public QueryBuilder setConditionSQL(StringBuilder conditionSQL) {
            this.conditionSQL = conditionSQL;
            return this;
        }

        public QueryBuilder setUpdateColumns(Map<String, Object> updateColumns) {
            this.updateColumns = updateColumns;
            return this;
        }

        public QueryBuilder setOrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public QueryBuilder setCustomSQL(String customSQL) {
            this.customSQL = customSQL;
            return this;
        }

        public Query build() {
            return new Query(dialect, selectColumns, tableName, conditionSQL, updateColumns, orderBy, customSQL);
        }
    }
}
