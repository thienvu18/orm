package mtkhdt.n9.query;

import mtkhdt.n9.model.QueryClause;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Map;

public class SelectQuery extends Query {
    QueryClause whereClause;
    private Triplet<Pair<HavingFunction ,String>, CompareOperator, Object> havingClause;
    private ArrayList<String> groupByColumns;
//    private Set<Triplet<String, CompareOperator, Object>> whereParams;
//    private Set<Triplet<String, CompareOperator, Object>> havingParams;


    public SelectQuery(String tableName, Map<String, Object> columnsData,
                       ArrayList<String> groupByColumns, QueryClause whereClause,
                       Triplet<Pair<HavingFunction ,String>, CompareOperator, Object> havingClause) {
        super(tableName, columnsData);
        this.groupByColumns = groupByColumns;
        this.whereClause = whereClause;
        this.havingClause = havingClause;
    }

    public QueryClause getWhereClause() {
        return whereClause;
    }

    public Triplet<Pair<HavingFunction ,String>, CompareOperator, Object> getHavingClause() {
        return havingClause;
    }

    public ArrayList<String> getGroupByColumns() {
        return groupByColumns;
    }

//    public Set<Triplet<String, CompareOperator, Object>> getWhereParams() {
//        return whereParams;
//    }
//
//    public Set<Triplet<String, CompareOperator, Object>> getHavingParams() {
//        return havingParams;
//    }

}
