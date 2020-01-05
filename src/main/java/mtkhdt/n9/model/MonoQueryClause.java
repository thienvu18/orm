package mtkhdt.n9.model;

import mtkhdt.n9.query.CompareOperator;
import org.javatuples.Triplet;

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
        String operator;

        switch (compareOperator) {
            case EQUAL:
            {
                String result = "(" + column + " = ";
                if(value instanceof Integer){
                    return  result + value + ")";
                }else{
                    return  result + "'" +  value + "')";
                }
            }
            case IN:
                break;
        }

        return "";
    }
}
