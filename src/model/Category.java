package model;

import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/4/17.
 *
 * Represents the relation that holds possible attraction category types
 */
public class Category extends Relation {

    @PrimaryKey private String catName;
    private int numberOfAttractions;

    public Category createFrom(ResultSet attributes) throws SQLException {

        Category category = new Category();
        category.catName = (String) Database.propertyFill(attributes, "catName", Type.STRING);
        category.numberOfAttractions = (int) Database.propertyFill(attributes, "countAttr", Type.INT);
        return category;
    }

    public String getCatName() {
        return catName;
    }

    public boolean delete() {
        String query = String.format("DELETE FROM Category WHERE "
                        + "catName='%s';", catName);
        return Database.delete(query).toBoolean();
    }

    public int getNumberOfAttractions() {
        return numberOfAttractions;
    }
}
