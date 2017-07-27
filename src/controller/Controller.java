package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.ReviewableEntity;
import resources.AVAsset;
import resources.Resources;
import view.HomeView;
import view.LoginView;
import view.RootView;
import view.UIAlert;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Cliff on 7/5/17.
 *
 * Contains high-level methods to be called
 */
public class Controller {

    public static ArrayList<String> restrictedPanes = new ArrayList<>();

    static {
        String[] panes = {"fxml/AddCityPane.fxml", "fxml/AddAttractionPane.fxml",
        "fxml/AddCategoryPane.fxml", "fxml/ReviewAttractionPane.fxml",
        "fxml/ReviewCityPane.fxml"};
        for (String pane : panes) { restrictedPanes.add(pane); }
    }

    public static boolean login(String email, String password) {

        if (email.length() == 0 || password.length() == 0) {
            UIAlert.show("Enter Login Information", "Please enter both an email\n"
                    + "and a password.", Alert.AlertType.CONFIRMATION);
            return false;
        }
        if (Accounts.loginWithCredentials(email, password)) {

            //Check if the user is suspended now that they have logged in successfully
            if (Accounts.getCurrentUser().isSuspended()) {
                UIAlert.show("Notice: Account Suspended", "Your PinPoint account has been suspended "
                        + "until further notice. You may not create reviews or entities.", Alert.AlertType.INFORMATION);
            }
            Stage stage = RootView.stage;
            Scene rootScene = HomeView.scene();
            stage.setScene(rootScene);
            stage.setWidth(RootView.screenBounds.getWidth() * 0.85);
            stage.setHeight(RootView.screenBounds.getHeight() * 0.9);
            stage.setTitle("PinPoint");
            stage.centerOnScreen();
            stage.show();
            LoginView.loginStage.hide();
            return true;
        }
        UIAlert.show("Invalid Login", "The email and password that you"
                + " entered do not match\nan existing PinPoint account. Please"
                + " try again.", Alert.AlertType.ERROR);
        return false;

    }

    /**
     * Replaces the contents of the main pane with the contents of the
     * FXML file represented by the given URL
     */
    public static <T> T replacePane(Node node, URL path) {
        if (Accounts.isLoggedIn() && Accounts.getCurrentUser().isSuspended()) {
            for (String restricted : restrictedPanes) {
                if (path.toExternalForm().contains(restricted)) {
                    UIAlert.show("You are Suspended", "Users who are suspended may not create new\n"
                            + "reviews, cities, attractions, or categories.", Alert.AlertType.INFORMATION);
                    return null;
                }
            }
        }
        Scene scene = node.getScene();
        Pane mainPane = (Pane) scene.lookup("#mainPane");
        HBox box = (HBox) mainPane.getChildren().get(0);
        box.getChildren().clear();
        //mainPane.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(path);
            box.getChildren().add(loader.load());
            return loader.getController();

        } catch (IOException ex) {
            Testing.print("IOException when trying to load new view");
        }
        return null;
    }

    public static void logout() {
        Accounts.logout();
        RootView.stage.hide();
        LoginView.loginStage.show();
        LoginView.loginStage.setScene(LoginView.loginScene);
        Resources.playAudio(AVAsset.Whoosh);
    }


    public static boolean registerNewUser(String email, String password) {

        boolean successful = Accounts.registerNewUser(email, password);
        if (successful) {
            Controller.login(email, password);
        }
        return successful;
    }

    public static boolean create(ReviewableEntity reviewable) {

        return false;
    }

    public static boolean delete(ReviewableEntity reviewableEntity) {

        return false;
    }
    
}
