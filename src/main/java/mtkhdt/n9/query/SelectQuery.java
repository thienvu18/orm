package mtkhdt.n9.query;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SelectQuery extends Query {
    private Set<String> selectColumns;
    private ArrayList<String> groupByColumns;
    private Set<Triplet<String, CompareOperator, Object>> whereParams;
    private Set<Triplet<String, CompareOperator, Object>> havingParams;

    public SelectQuery(String tableName, Map<String, Object> columnsData, Set<String> selectColumns,
                       ArrayList<String> groupByColumns, Set<Triplet<String, CompareOperator, Object>> whereParams,
                       Set<Triplet<String, CompareOperator, Object>> havingParams) {
        super(tableName, columnsData);
        this.selectColumns = selectColumns;
        this.groupByColumns = groupByColumns;
        this.whereParams = whereParams;
        this.havingParams = havingParams;
    }

    public Set<String> getSelectColumns() {
        return selectColumns;
    }

    public ArrayList<String> getGroupByColumns() {
        return groupByColumns;
    }

    public Set<Triplet<String, CompareOperator, Object>> getWhereParams() {
        return whereParams;
    }

    public Set<Triplet<String, CompareOperator, Object>> getHavingParams() {
        return havingParams;
    }
}
