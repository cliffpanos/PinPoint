package model;

import controller.Accounts;
import controller.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import view.UIAlert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

/**
 * Created by Cliff on 7/4/17.
 *
 * User relation
 */
public class User extends Relation {

    @PrimaryKey private String email;
    private Date dateJoined;
    private boolean isSuspended = false;
    private boolean isManager = false;
    /* private String password should not exist! */


    public User() {
    }

    public boolean create(Review review) {

        return false;
    }

    public boolean submit(ReviewableEntity reviewable) {

        return false;
    }


    public User createFrom(ResultSet attributes) throws SQLException {

        User user = new User();
        user.email = (String) Database.propertyFill(attributes, "email", Type.STRING);
        user.dateJoined = (Date) Database.propertyFill(attributes, "dateJoined", Type.DATE);
        user.isSuspended = (Boolean) Database.propertyFill(attributes, "isSuspended", Type.BOOLEAN);
        user.isManager = (Boolean) Database.propertyFill(attributes, "isManager", Type.BOOLEAN);

        return user;
    }

    public boolean delete() {
        return Accounts.deleteAccountForUser(this);
    }

    public String getEmail() {
        return email;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getUserClass() {
        return isManager ? "Manager" : "User";
    }

    public void setSuspended(boolean suspended) {
        this.isSuspended = suspended;
    }

    public void setManager(boolean manager) {
        this.isManager = manager;
    }

    public String getSuspendedName() {
        return isSuspended ? "Suspended" : "Active";
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) { return false; }
        if (this == null) { return false; }
        if (!(o instanceof User)) { return false; }
        User user = (User) o;
        return this.email.equals(user.email);   //Since email is a PrimaryKey
    }

}
