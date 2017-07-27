package controller;


import java.util.ArrayList;

/**
 * Created by Cliff on 7/5/17.
 *
 * Used to craft a SQL query
 *
 */
class SQLQuery {

    private String prepared = null;
    private ArrayList<String> attributesList = new ArrayList<>();
    private ArrayList<String> tablesList = new ArrayList<>();
    private ArrayList<String> condition = new ArrayList<>();

    public static SQLQuery direct(String executableString) {
        SQLQuery query = new SQLQuery();
        query.prepared = executableString;
        return query;
    }

    public static SQLQuery select(Attributed... relationAttributes) {

        SQLQuery query = new SQLQuery();        //To reduce syntax elsewhere

        ArrayList<Attributed> attributes = new ArrayList<>();
        for (Attributed attribute : relationAttributes) {
            attributes.add(attribute);
            query.attributesList.add(attribute.toString());
        }

        if (attributes.contains(Attributed.ALL)) {
            query.attributesList = new ArrayList<>();
            query.attributesList.add("*");
        }

        /*TEST --------------------------------
        System.out.print("SELECT: ");
        for (String attribute : query.attributesList) {
            System.out.print(attribute + " ");
        }
        System.out.println(); */

        return query;

    }


    public SQLQuery from(R... relations) {
        for (R relation : relations) {
            this.tablesList.add(relation.toString());
        }

        /*TEST --------------------------------
        System.out.print("FROM: ");
        for (String relation : tablesList) {
            System.out.print(relation + " ");
        }
        System.out.println(); */

        return this;
    }

    public SQLQuery where(Object attrONE, Condition condition, Object object) {

        this.condition.add(attrONE.toString());
        this.condition.add(condition.toString());

        if (object instanceof String) {
            this.condition.add("'" + object.toString() + "'");
        } else {
            this.condition.add(object.toString());
        }

        /*TEST --------------------------------
        System.out.print("WHERE: ");
        for (String whereBool : condition) {
            System.out.print(whereBool + " ");
        }
        System.out.println();*/

        return this;
    }

    public SQLQuery where(String directInput) {

        this.condition = new ArrayList<>();
        condition.add(directInput);

        //System.out.println("WHERE: " + directInput);

        return this;
    }


    public String prepared() {
        return this.toString();
    }

    @Override
    public String toString() {

        if (prepared != null) { return prepared; } //For direct executables

        String selectString = "SELECT ";
        String fromString = " FROM ";
        String whereString = " WHERE ";

        selectString += spaced(attributesList, ", ");
        fromString += spaced(tablesList, ", ");

        //System.out.println("\n\n" + selectString);
        //System.out.println(fromString);

        if (condition.isEmpty()) {
            whereString = "";
        } else {
            whereString += "(" + spaced(condition, " ") + ")";
        }

        //System.out.println(whereString);

        String result = selectString + fromString + whereString;
        System.out.println(result);
        return result;
    }

    private <E> String spaced(ArrayList<E> list, String inBetween) {
        String result = "";
        for (int i = 0; i < list.size() - 1; i++) {
            result += list.get(i).toString() + inBetween;
        }
        result += list.get(list.size() - 1);
        return result;
    }



    enum Condition {

        EQUAL, LESS_THAN, GREATER_THAN,
        GREATERTHAN_EQUALTO, LESSTHAN_EQUALTO, LIKE, IS;

        @Override
        public String toString() {
            switch (this) {
                case EQUAL: return "=";
                case GREATER_THAN: return ">";
                case GREATERTHAN_EQUALTO: return ">=";
                case LESS_THAN: return "<";
                case LESSTHAN_EQUALTO: return "<=";
                case LIKE: return "LIKE";
                case IS: return "IS";
            }
            return "=";
        }

    }

}