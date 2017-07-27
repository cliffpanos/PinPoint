package model;

import controller.Accounts;
import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Cliff on 7/4/17.
 *
 * The Review relation
 */
public class Review extends Relation {

    private int rating;
    private String comments;
    private Date dateSubmitted;
    private String name = "";
    @ForeignKey private int entityID; @PrimaryKey //unique with email
    @ForeignKey private String email; @PrimaryKey //unique with entityID

    public Review() {
    }


    public ReviewableEntity getReferencedReviewable() {
        return null;
    }


    public Review createFrom(ResultSet attributes) throws SQLException {

        Review review = new Review();
        review.rating = (int) Database.propertyFill(attributes, "rating", Type.INT);
        review.comments = (String) Database.propertyFill(attributes, "comments", Type.STRING);
        review.dateSubmitted = (Date) Database.propertyFill(attributes, "dateSubmitted", Type.DATE);
        review.entityID = (int) Database.propertyFill(attributes, "entityID", Type.INT);
        review.email = (String) Database.propertyFill(attributes, "email", Type.STRING);
        review.name = (String) Database.propertyFill(attributes, "name", Type.STRING);
        return review;
    }

    public boolean delete() {
        String query = String.format("DELETE FROM review WHERE " +
                "email='%s' AND entityID='%d';",
                Accounts.getCurrentUser().getEmail(), entityID);
        return Database.delete(query).toBoolean();
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getEntityID() {
        return entityID;
    }
}
