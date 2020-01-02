package mtkhdt.n9.query;

import mtkhdt.n9.dialect.Dialect;

public class Query {
    private Dialect dialect;
    private QueryType type;

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public String toSqlString() {
        String sql = null;
        switch (type)  {
            case DELETE:
                sql = dialect.delete(this);
                break;
            case INSERT:
                sql = dialect.insert(this);
                break;
            case SELECT:
                sql = dialect.select(this);
                break;
            case UPDATE:
                sql = dialect.update(this);
                break;
            default:
                return null;
        }

        return sql;
    }
}
