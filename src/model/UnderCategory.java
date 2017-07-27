package model;

import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/4/17.
 */
public class UnderCategory extends Relation {

    @PrimaryKey @ForeignKey private String catName;
    @PrimaryKey @ForeignKey private int attrID;
    //These two attributes together make the primary key


    public UnderCategory createFrom(ResultSet attributes) throws SQLException {

        UnderCategory underCategory = new UnderCategory();
        underCategory.catName = (String) Database.propertyFill(attributes, "catName", Type.STRING);
        underCategory.attrID = (int) Database.propertyFill(attributes, "attrID", Type.INT);
        return underCategory;
    }

    public boolean delete() {
        return false;
    }

    public String getCatName() {
        return catName;
    }
}
