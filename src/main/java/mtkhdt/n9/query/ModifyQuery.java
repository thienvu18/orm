package mtkhdt.n9.query;

import java.util.Map;

public class ModifyQuery extends Query {

    public ModifyQuery(String tableName, Map<String, Object> columnsData) {
        super(tableName, columnsData);
    }
}
