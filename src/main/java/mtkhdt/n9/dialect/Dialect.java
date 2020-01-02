package mtkhdt.n9.dialect;

import mtkhdt.n9.query.Query;

public interface  Dialect {
    default String select(Query query) {
        StringBuilder sql = new StringBuilder();
        //Get value from query to build string
        return sql.toString();
    }

    default String insert(Query query) {
        StringBuilder sql = new StringBuilder();

        return sql.toString();
    }

    default String update(Query query) {
        StringBuilder sql = new StringBuilder();

        return sql.toString();
    }

    default String delete(Query query) {
        StringBuilder sql = new StringBuilder();

        return sql.toString();
    }
}
