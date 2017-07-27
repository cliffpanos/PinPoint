package model;

import com.sun.deploy.security.ValidationState;
import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/4/17.
 */
public class City extends ReviewableEntity {

    @PrimaryKey private int cityID; @ForeignKey
    private String cityName;
    private String country;
    private String state;
    private int numberOfAttr;
    private String pendingComment;  //The comment entered when a user creates a new City

    public City() {
        super();
    }


    public City createFrom(ResultSet attributes) throws SQLException {
        City city = new City();
        city.cityName = (String) Database.propertyFill(attributes, "cityName", Type.STRING);
        city.averageRating = (double) Database.propertyFill(attributes, "avgRating", Type.DOUBLE);
        city.numberOfReviews = (int) Database.propertyFill(attributes, "countRating", Type.INT);
        city.numberOfAttr = (int) Database.propertyFill(attributes, "countAttr", Type.INT);
        city.cityID = (int) Database.propertyFill(attributes, "cityID", Type.INT);
        city.entityID = (int) Database.propertyFill(attributes, "entityID", Type.INT);
        city.pendingComment = (String) Database.propertyFill(attributes, "pendingComment", Type.STRING);
        city.submitterEmail = (String) Database.propertyFill(attributes, "submitterEmail", Type.STRING);
        city.state = (String) Database.propertyFill(attributes, "state", Type.STRING);
        city.country = (String) Database.propertyFill(attributes, "country", Type.STRING);

        /*city.cityID = attributes.getInt("cityID");
        city.cityName = attributes.getString("cityName");
        city.country = attributes.getString("country");
        city.state = attributes.getString("state");
        city.entityID = attributes.getInt("entityID");
        city.submitterEmail = attributes.getString("submitterEmail");
        city.isPending = attributes.getBoolean("isPending");
        city.dateSubmitted = attributes.getDate("dateSubmitted");
        city.averageRating = attributes.getDouble("averageRating");
        city.numberOfReviews = attributes.getInt("numberOfReviews");*/
        return city;
    }

    public int getCityId() {
        return cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public int getNumberOfAttr() {
        return numberOfAttr;
    }
    
    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getPendingComment() {
        return pendingComment;
    }

}
