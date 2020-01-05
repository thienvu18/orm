package mtkhdt.n9.query;

import mtkhdt.n9.model.QueryClause;

import java.util.Map;

public class ModifyQuery extends Query {
    QueryClause whereClause;

    public ModifyQuery(String tableName, Map<String, Object> modifiedColumns, QueryClause whereClause) {
        super(tableName, modifiedColumns);
        this.whereClause = whereClause;
    }

    public QueryClause getWhereClause() {
        return whereClause;
    }
}
