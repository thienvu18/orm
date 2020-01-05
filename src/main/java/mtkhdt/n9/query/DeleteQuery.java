package mtkhdt.n9.query;

import mtkhdt.n9.model.QueryClause;
import org.javatuples.Triplet;

import java.util.Map;
import java.util.Set;

public class DeleteQuery extends Query {
    QueryClause whereClause;

    public DeleteQuery(String tableName, QueryClause whereClause) {
        super(tableName, null);
        this.whereClause = whereClause;
    }

    public QueryClause getWhereClause() {
        return whereClause;
    }
}
