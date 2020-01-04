package mtkhdt.n9.query;

import java.util.HashMap;
import java.util.Map;

public abstract class Query {
    protected String tableName;
    protected Map<String, Object> columnsData;

    public Query(String tableName, Map<String, Object> columnsData) {
        this.tableName = tableName;
        this.columnsData = columnsData;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Map<String, Object> getColumnsData() {
        Map<String, Object> map = new HashMap<>();
        columnsData.forEach(map::put);
        return map;
    }
}
