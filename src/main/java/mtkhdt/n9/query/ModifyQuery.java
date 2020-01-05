package mtkhdt.n9.query;

import org.javatuples.Triplet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModifyQuery extends Query {
    private Set<Triplet<String, CompareOperator, Object>> whereParams;
    private Set<Triplet<String, CompareOperator, Object>> orWhereParams;

    public ModifyQuery(String tableName, Map<String, Object> columnsData, Set<Triplet<String, CompareOperator, Object>> whereParams, Set<Triplet<String, CompareOperator, Object>> orWhereParams) {
        super(tableName, columnsData);
        this.whereParams = whereParams;
        this.orWhereParams = orWhereParams;
    }

    public Set<Triplet<String, CompareOperator, Object>> getWhereParams() {
        return new HashSet<>(whereParams);
    }

    public Set<Triplet<String, CompareOperator, Object>> getOrWhereParams() {
        return new HashSet<>(orWhereParams);
    }
}
