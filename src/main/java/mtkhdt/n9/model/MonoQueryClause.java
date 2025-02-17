package mtkhdt.n9.model;

import mtkhdt.n9.query.CompareOperator;

public class MonoQueryClause implements QueryClause {
    private String  column;
    private Object value;
    private CompareOperator compareOperator;

    public MonoQueryClause(String column, CompareOperator compareOperator,  Object value) {
        this.column = column;
        this.value = value;
        this.compareOperator = compareOperator;
    }

    @Override
    public String buildSqlClause() {
        String operator = "";

        switch (compareOperator) {
            case EQUAL:
            {
                operator = "=";
                break;
            }
            case NOT_EQUAL: {
                operator = "<>";
                break;
            }
            case IN:
                break;
            case GREATER: {
                operator = ">";
                break;
            }
            case GREATER_OR_EQUAL: {
                operator = ">=";
                break;
            }
            case LESS_THAN: {
                operator = "<";
                break;
            }
            case LESS_THAN_OR_EQUAL: {
                operator = "<=";
                break;
            }
        }

        if (!operator.equals("")) {
            String result = "(" + column + " " + operator + " ";
            if (value instanceof Number) {
                return result + value + ")";
            } else {
                return result + "'" + value + "')";
            }
        }
        return "";
    }
}
