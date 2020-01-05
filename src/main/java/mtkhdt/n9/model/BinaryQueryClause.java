package mtkhdt.n9.model;

import mtkhdt.n9.query.CompareOperator;

public class BinaryQueryClause implements QueryClause {
    QueryClause left;
    QueryClause right;
    CompareOperator operator;

    public BinaryQueryClause(QueryClause left, CompareOperator operator, QueryClause right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public QueryClause getLeft() {
        return left;
    }

    @Override
    public String buildSqlClause() {
        switch (operator) {
            case AND:
                return "(" + left.buildSqlClause() + " AND " + right.buildSqlClause() + ")";
            case OR:
                return "(" + left.buildSqlClause() + " OR " + right.buildSqlClause() + ")";
        }

        return "";
    }
}
