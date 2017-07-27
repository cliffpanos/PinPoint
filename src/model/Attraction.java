package model;

import controller.Database;

import java.sql.ResultSet;
import java.sql.Time;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/4/17.
 *
 * The Attraction relation
 */
public class Attraction extends ReviewableEntity {

    private String attrName; //UNIQUE attrName and address
    private String address;
    private String description;
    private String category;
    private String cityName;
    private String pendingHours;
    private String pendingComment;  //The comment entered when a user creates a new Attraction

    @ForeignKey private int cityLocatedIn;
    @PrimaryKey private int attrID; @ForeignKey


    public Attraction createFrom(ResultSet attributes) throws SQLException {
        Attraction attraction = new Attraction();
        attraction.attrName = (String) Database.propertyFill(attributes, "attrName", Type.STRING);
        attraction.category = (String) Database.propertyFill(attributes, "catName", Type.STRING);
        attraction.cityName = (String) Database.propertyFill(attributes, "cityName", Type.STRING);
        attraction.averageRating = (double) Database.propertyFill(attributes, "avgRating", Type.DOUBLE);
        attraction.numberOfReviews = (int) Database.propertyFill(attributes, "countRating", Type.INT);
        attraction.attrID = (int) Database.propertyFill(attributes, "attrID", Type.INT);
        attraction.entityID = (int) Database.propertyFill(attributes, "entityID", Type.INT);
        attraction.address = (String) Database.propertyFill(attributes, "address", Type.STRING);
        attraction.description = (String) Database.propertyFill(attributes, "description", Type.STRING);

        attraction.pendingComment = (String) Database.propertyFill(attributes, "comments", Type.STRING);
        attraction.submitterEmail = (String) Database.propertyFill(attributes, "submitterEmail", Type.STRING);

        Time open = (Time) Database.propertyFill(attributes, "openTime", Type.TIME);
        Time close = (Time) Database.propertyFill(attributes, "closeTime", Type.TIME);
        if (open != null && close != null) {
            attraction.pendingHours = open.toString() + "-" + close.toString();
        }

        return attraction;

    }

    public String getCityName() {
        return cityName;
    }

    public String getCategory() {
        return category;
    }

    public String getAttrName() {
        return attrName;
    }

    public int getAttrID() {
        return attrID;
    }

    public String getAddress() { return address; }

    public String getDescription() {
        return description;
    }

    public int getCityLocatedIn() {
        return cityLocatedIn;
    }


    public String getPendingComment() {
        return pendingComment;
    }

    public String getPendingHours() {
        if (pendingHours == null) { return "None listed"; }
        return pendingHours;
    }
}
