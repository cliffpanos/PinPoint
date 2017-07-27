package controller;

import model.Category;
import model.City;
import model.Relation;
import model.ReviewableEntity;
import model.Type;
import runner.Runner;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Cliff on 6/16/17.
 *
 * Used to interact with the SQL PinPoint Database
 */
public class Database {

    /**
     * The java.sql.Connection to the PinPoint MySQL Relational Database
     */
    private static Connection con = null;

    /**
     * Set a Connection object to be connected to the PinPoint database
     * @param connection the connection to be connected to PinPoint
     * @param sender an instance of Runner to ensure that only the application
     *               runner can call this method.
     */
    public static void setConnection(Connection connection) {
        Database.con = connection;
    }

    @Deprecated //Do not call since all calls in this app are being done with directRetrieval
    static boolean persistForQuery(SQLQuery query, R relation, Relation[] store) {
        Relation[] result = retrieveForQuery(query, relation);
        store = result;
        return store.length > 0;
    }

    @Deprecated //Do not call since all calls in this app are being done with directRetrieval
    static Relation[] retrieveForQuery(SQLQuery query, R relation) {
        return directRetrieval(query.prepared(), relation);
    }

    /**
     * Retrieve a set of tuples using a SELECT query and use the result to create
     * an array of instances of a given Relation type
     * @param preparedQuery A ready-to-execute SQL Query that will select tuples
     * @param relation the type of Relation that will be selected from the query
     *                 ex: for 'SELECT * FROM City', send R.City
     * @return an array of the specified Relation type populated using the query
     */
    public static Relation[] directRetrieval(String preparedQuery, R relation) {
        restartConnection();
        Relation[] result = {};
        ArrayList<Relation> resultList = new ArrayList<>();

        PreparedStatement statement = null;

        if (con == null) { return relation.toArray(resultList); }

        try {
            con.setAutoCommit(false);
            statement = con.prepareStatement(preparedQuery);
            ResultSet retrieved = statement.executeQuery();

//            Testing.print(preparedQuery);
            testConnection();

            if (!retrieved.isBeforeFirst()) {
                Testing.print("No data retrieved in ResultSet");
            }
            int index = 0;
            while (retrieved.next()) {
                //Note that this will cause a NullPointerException if R == ReviewableEntity
                resultList.add(index++, relation.toObject().createFrom(retrieved));
            }

        } catch (SQLException e) {
            Testing.print(e.getMessage());
        } finally {
            closeStatement(statement);
        }

        return relation.toArray(resultList);
    }

    /**
     * Insert a tuple into the database and immediately commit the changes.
     * @param insertStatement the sql query beginning with 'INSERT INTO ...'
     * @return an SQLResult indicating whether the insert was successful or
     * unsuccessful due to an exception (possibly a duplicate primary key)
     */
    public static SQLResult insert(String insertStatement) {
        restartConnection();
        Statement stmt = null;
        try {
            con.setAutoCommit(true);
            stmt = con.createStatement();
            stmt.execute(insertStatement);
            Testing.print(insertStatement);
        } catch (SQLException e) {
            Testing.print(e.getMessage());
            if (e.getErrorCode() == 1061 || e.getErrorCode() == 1062) {
                return SQLResult.DuplicatePK;
            } else if (e.getErrorCode() == 1048) {
                return SQLResult.MissingField;
            } else {
                return SQLResult.Error;
            }
        } finally {
            closeStatement(stmt);
        }
        return SQLResult.Success;
    }

    /**
     * Update a tuple into the databsae and immediately commit the changes.
     * @param updateStatement a ready-to-execute String beginning with 'UPDATE ...'
     * @return a SQLResult indicating whether or not the update was successful.
     */
    public static SQLResult update(String updateStatement) {
        restartConnection();
        PreparedStatement statement = null;
        try {
            con.setAutoCommit(true);
            statement = con.prepareStatement(updateStatement);
            statement.executeUpdate();
            Testing.print(updateStatement);
        } catch (SQLException e) {
            Testing.print(e.getMessage() + " | " + e.getErrorCode());
            return SQLResult.Error;
        } finally {
            closeStatement(statement);
        }
        return SQLResult.Success;
    }


    /**
     * Delete a tuple from the database and immediately commit the changes.
     * @param deleteStatement a ready-to-execute delete statement beginning with
     *                        'DELETE FROM ...'
     * @return a SQLResult indicating whether or not the deletion was successful
     */
    public static SQLResult delete(String deleteStatement) {
        restartConnection();
        Statement statement = null;
        try {
            con.setAutoCommit(true);
            statement = con.createStatement();
            statement.execute(deleteStatement);
            Testing.print(deleteStatement);
        } catch (SQLException e) {
            Testing.print(e.getMessage() + " | " + e.getErrorCode());
            return SQLResult.Error;
        } finally {
            closeStatement(statement);
        }
        return SQLResult.Success;
    }


    /**
     * Used to populate fields with their appropriate values if they exist and null if they do not
     * @param resultSet the data source
     * @param type the type of the data to be filled in
     * @param columnName the name of the property to be retrieved from the result set
     */
    public static Object propertyFill(ResultSet resultSet, String columnName, Type type) {

        try {
            switch (type) {
                case STRING:
                    return resultSet.getString(columnName);
                case INT:
                    return resultSet.getInt(columnName);
                case DOUBLE:
                    return resultSet.getDouble(columnName);
                case DATE:
                    return resultSet.getDate(columnName);
                case BOOLEAN:
                    return resultSet.getBoolean(columnName);
                case TIME:
                    return resultSet.getTime(columnName);
                default:
                    Testing.print("PROPERTY NOT MATCHED");
            }
        } catch (SQLException propertyDoesNotExist) {
            switch (type) {
                case STRING:
                    return null;
                case INT:
                    return 0;
                case DOUBLE:
                    return 0.0;
                case DATE:
                    return null;
                case BOOLEAN:
                    return false;
                case TIME:
                    return null;
                default:
                    Testing.print("PROPERTY NOT MATCHED");
            }
        }
        return null;
    }

    public static void submitReviewableEntity(int entityId, String email, String date) {
        restartConnection();
        int pendingOrNot = (Accounts.isLoggedIn() && Accounts.getCurrentUser().isManager()) ? 0 : 1;
        String query = String.format("INSERT INTO reviewableentity "
                        + "VALUES ('%d', '%d', '%s', '%s');",
                entityId, pendingOrNot, email, date);
        insert(query);
    }

    public static void submitPendingCity(int entityId, String cityName, String country, String state) {
        restartConnection();
        String query = String.format("INSERT INTO city "
                        + "VALUES ('%d', '%s', '%s', '%s');",
                entityId, cityName, country, state);
        insert(query);
    }

    public static void submitPendingAttr(int entityId, String attrName, String address,
            String description, int cityId) {
        restartConnection();
        String query = String.format("INSERT INTO attraction "
                        + "VALUES ('%d', '%s', '%s', '%s', '%d');",
                entityId, attrName, address, description, cityId);
        insert(query);
    }

    public static void submitAttractionCategory(int attrId, String catName) {
        restartConnection();
        String query = String.format("INSERT INTO undercategory " +
                "VALUES ('%d', '%s');", attrId, catName);
        insert(query);
    }

    public static void submitReview(String user, int rating, String comment, int entityId, String date) {
        String query = String.format("INSERT INTO review " +
                        "VALUES ('%s', '%d', '%s', '%d', '%s');",
                user, rating, comment, entityId, date);
        Database.insert(query);
    }

    public static void submitHours(int attrID, String openDay, String openTime, String closeTime) {
        restartConnection();
        String query = String.format("INSERT INTO operationHours " +
                        "VALUES ('%d', '%s', '%s', '%s');",
                attrID, openDay, openTime, closeTime);
        Database.insert(query);
    }

    public static void submitContactInfo(int attrID, String contactMethod, String contactValue) {
        restartConnection();
        String query = String.format("INSERT INTO contactInfo " +
                        "VALUES ('%d', '%s', '%s');",
                attrID, contactMethod, contactValue);
        Database.insert(query);
    }

    public static int getNewEntityId() {
        restartConnection();
        String getMax = "SELECT MAX(entityID) AS entityID FROM reviewableentity;";
        ReviewableEntity[] entity = (ReviewableEntity[]) Database.directRetrieval(getMax, R.City);
        if (entity.length == 0) {
            return 1;
        }
        return entity[0].getEntityID() + 1;
    }

    public static boolean alreadyReviewed(String email, int entityId) {
        restartConnection();
        String preparedQuery = String.format("SELECT EXISTS(SELECT * FROM review " +
                "WHERE email='%s' AND entityID='%d') AS result;", email, entityId);
        PreparedStatement statement = null;
        if (con == null) {
            return false;
        }
        try {
            con.setAutoCommit(false);
            statement = con.prepareStatement(preparedQuery);
            ResultSet retrieved = statement.executeQuery();
            testConnection();
            retrieved.next();
            return retrieved.getBoolean("result");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
        public static City[] getAllCities() {
        String query = "SELECT cityName FROM nonpendingCities;";
        return (City[]) Database.directRetrieval(query, R.City);
    }

    public static int getCount(String preparedQuery) {
        restartConnection();
        PreparedStatement statement = null;
        if (con == null) {
            return 1;
        }
        try {
            con.setAutoCommit(false);
            statement = con.prepareStatement(preparedQuery);
            ResultSet retrieved = statement.executeQuery();
            testConnection();
            retrieved.next();
            return retrieved.getInt("result");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 1;
    }

    public static Category[] getAllCategories() {
        restartConnection();
        String query = "SELECT catName FROM category;";
        return (Category[]) Database.directRetrieval(query, R.Category);
    }

    /**
     * Get the current date in the format of a java.sql.Date
     * @return the Date as of the time when the method is called
     */
    public static Date now() {
        restartConnection();
        java.util.Date now = new java.util.Date();
        return new java.sql.Date(now.getTime());
    }

    /**
     * Call this method to test if the database connection is active & working.
     * If it is active, it will print nothing; if it is not working, it will print so.
     */
    public static void testConnection() {
        try {
            if (con == null || con.isClosed() || !con.isValid(500)) {
                Testing.print("INVALID CONNECTION!");
            }
        } catch (SQLException e) {
            Testing.print(e.getMessage());
        }
    }

    private static void restartConnection() {
        try {
            Runner.initializeConnection();
        } catch (SQLException sqle) {
            Testing.print(sqle.getMessage());
            System.out.println("No SQL Connection\n");
        }    }

    /**
     * Call this to close and release the resources of any java.sql.Statement
     * that is currently in use. EVERY Statement should be closed at some point.
     * @param stmt the Statement to be closed
     */
    private static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                Testing.print(e.getMessage());
            }
        }
    }

    /**
     * Call this method to disconnect from the SQL Server database at the time
     * when the application is about to close
     *
     * The sender argument ensures that only the application runner can close
     * the database connection.
     */
    public static void disconnect(Runner sender) {
        if (con != null) {
            try {
                con.close();
                con = null;
                Testing.print("\n*** Closing Database Connection! ***");
            } catch (SQLException e) {
                Testing.print(e.getMessage());
            }
        }
    }

}
