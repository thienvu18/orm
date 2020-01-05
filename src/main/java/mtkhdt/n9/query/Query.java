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

    public String getOperatorFromEnum(CompareOperator operatorEnum) {
        String operator = "";
        switch (operatorEnum) {
            case EQUAL:
                operator = "=";
                break;
            case IN:
                operator = "IN";
                break;
            case NOT_EQUAL:
                operator = "<>";
                break;
            case GREATER:
                operator = ">";
                break;
            case GREATER_OR_EQUAL:
                operator = ">=";
                break;
            case LESS_THAN:
                operator = "<";
                break;
            case LESS_THAN_OR_EQUAL:
                operator = "<=";
                break;
        }

        return operator;
    }
}
