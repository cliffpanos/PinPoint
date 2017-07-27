package controller;

import javafx.scene.control.Alert;
import model.User;
import view.UIAlert;

/**
 * Created by Cliff on 7/4/17.
 *
 * Contains methods called by Controller.java relating to user account management
 */
public class Accounts {

    private static User currentUser = null;

    public static User getCurrentUser() {
        if (!isLoggedIn()) { Testing.print("No user is logged in!"); }
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }


    public static boolean registerNewUser(String email, String password) {

        String insertQuery = String.format("INSERT INTO user VALUES ('%s', '%s', %s, 0, 0);",
                email, Database.now(), password.hashCode());
        Testing.print(String.format("Attempting registration with: %s, %s", email, password.hashCode()));
        SQLResult result = Database.insert(insertQuery);

        if (result.toBoolean()) {
            return true;
        } else if (result == SQLResult.DuplicatePK){
            UIAlert.show("Email Already In Use", "Sorry, but another PinPoint user "
                    + "has already\nregistered with the email " + email + ".\n"
                    + "Please choose another email address.", Alert.AlertType.ERROR);
        } else {
            UIAlert.show("Registration Error", "Sorry, but PinPoint encountered an error "
                    + "while trying to register your new account. Please try again.", Alert.AlertType.ERROR);
        }
        return false;
    }

    static boolean loginWithCredentials(String email, String password) {

        Testing.print(String.format("Attempting login with: %s, %s", email, password.hashCode()));

        User[] userResult = (User[]) Database.directRetrieval(String.format(
                "SELECT email, dateJoined, isSuspended, isManager FROM user"
                        + " WHERE (email='%s' AND password=%s);", email, password.hashCode())
                , R.User);

        if (userResult.length == 0) {
            return false;   //No user exists with the email and password combo
        } else if (userResult.length == 1) {
            Accounts.currentUser = userResult[0];
            return true;
        } else {
            Testing.print("!!! More than one user has been found with the same"
                    + " email and password combination. The database has a schema"
                    + " issue. !!!\n");
            return false;
        }

    }

    static void logout() {
        Testing.print("Logging Out.");
        Accounts.currentUser = null;
    }

    public static boolean deleteAccountForUser(User user) {

        SQLResult result = Database.delete(String.format(
                "DELETE FROM User WHERE email='%s';", user.getEmail()));
        return result.toBoolean();
    }

    static boolean suspendUser(User user) {

        return false;
    }

    static boolean promoteUser(User user) {

        return false;
    }

    static boolean demoteUser(User user) {

        return false;
    }

}
