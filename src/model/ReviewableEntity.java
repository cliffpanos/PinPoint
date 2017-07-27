package model;

import java.sql.Date;

/**
 * Created by Cliff on 7/4/17.
 */
public abstract class ReviewableEntity extends Relation {

    @PrimaryKey protected int entityID;
    @ForeignKey public String submitterEmail;
    protected boolean isPending = true;
    protected Date dateSubmitted;
    protected double averageRating;
    protected int numberOfReviews;

    public ReviewableEntity() {
    }

    public void checkEmailConstraint() throws Exception {

        //TODO cliff will add code to check regex pattern

        if (submitterEmail.length() > MAX_ATTR_LENGTH) {
            throw new java.lang.IllegalStateException("Email length too long");
        }
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public int getEntityID() {
        return entityID;
    }

    public String getSubmitterEmail() { return submitterEmail; }


}
