package model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/18/17.
 *
 * The superclass for all Relation classes
 * Used polymorphically and to require that all relations implement createFrom()
 * and delete()
 */
public abstract class Relation {

    /**
     * Declares the maximum allowed length for most String attribute types such
     * as email, all names, country and state, contact information, etc
     */
    public static final int MAX_ATTR_LENGTH = 50;

    /**
     * Declares the maximum allowed length for the three long String attributes:
     * 1. Review.comments
     * 2. Attraction.address
     * 3. Attraction.description
     */
    public static final int MAX_EXTENDED_LENGTH = 255;

    /**
     * Call this method to use a java.sql.ResultSet to construct a physical
     * Relation object. The essence of this method is to turn a set of key-value
     * pairs into an Object with fields and functionality.
     *
     * @param attributes the ResultSet that contains key-value pairs for attribute
     *                   names and their values
     * @return a fully-constructed object that is a subclass of Relation
     * @throws SQLException the possible exception thrown when a SQL issue occurs
     */
    public abstract Relation createFrom(ResultSet attributes) throws SQLException;

//    /**
//     * Each Relation subclass should implement this in order to delete a specific
//     * tuple of its type from the database
//     *
//     * @return whether or not the delete was successful
//     */
    //public abstract boolean delete();

}
