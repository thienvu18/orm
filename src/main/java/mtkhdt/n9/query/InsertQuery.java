package mtkhdt.n9.query;

import java.util.Map;

public class InsertQuery extends Query {
    public InsertQuery(String tableName, Map<String, Object> columnsData) {
        super(tableName, columnsData);
    }
}
