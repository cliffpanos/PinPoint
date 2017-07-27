package view;

import controller.Accounts;
import controller.Controller;
import controller.Database;
import controller.Testing;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class HomeView {

    private static String citySearchPanePath = "fxml/CitySearchPane.fxml";
    private static String attractionSearchPanePath = "fxml/AttractionSearchPane.fxml";
    private static String addCityPanePath = "fxml/AddCityPane.fxml";
    private static String addAttractionPanePath = "fxml/AddAttractionPane.fxml";
    private static String viewAllCitiesPanePath = "fxml/ViewAllCitiesPane.fxml";
    private static String viewAllAttractionsPanePath = "fxml/ViewAllAttractionsPane.fxml";
    private static String myReviewsPanePath = "fxml/MyReviewsPane.fxml";
    private static String myProfilePanePath = "fxml/MyProfilePane.fxml";
    @FXML private Hyperlink logout;
    @FXML private Button cityButton;
    @FXML private Button attrButton;
    @FXML private Button profileButton;
    @FXML private VBox linkBox;
    @FXML private AnchorPane mainPane;
    @FXML private BorderPane borderPane;
    @FXML private Label emailLabel;

    public static Scene scene() { return FXBuilder.sceneForFXML("HomeView.fxml"); }

    @FXML
    public void initialize() {
        emailLabel.setText(Accounts.getCurrentUser().getEmail());
        handleProfile();
        linkBox.getChildren().get(0).fireEvent(new ActionEvent());
    }

    /**
     * Redirects the user to the login screen.
     */
    @FXML
    public void handleLogout() {
        boolean result = UIAlert.show("Confirm Logout", "Do you want to log out of PinPoint?", Alert.AlertType.CONFIRMATION, true);
        if (!result) {
            Controller.logout();
        }
    }

    /**
     * Sets the functionality of the hyperlinks in the middle pane.
     */
    @FXML
    public void handleCity() {
        setLinks();
        replacePane(citySearchPanePath);
        ObservableList<Node> nodeList = linkBox.getChildren();

        //Search
        ((Hyperlink) nodeList.get(0)).setOnAction((ActionEvent event) -> {
            replacePane(citySearchPanePath);
        });

        //View All
        ((Hyperlink) nodeList.get(2)).setOnAction((ActionEvent event) -> {
            replacePane(viewAllCitiesPanePath);
        });

        //Add
        ((Hyperlink) nodeList.get(4)).setOnAction((ActionEvent event) -> {
            replacePane(addCityPanePath);
        });
    }

    /**
     * Sets the functionality of the hyperlinks in the middle pane.
     */
    @FXML
    public void handleAttraction() {
        setLinks();
        replacePane(attractionSearchPanePath);
        ObservableList<Node> nodeList = linkBox.getChildren();

        //Search
        ((Hyperlink) nodeList.get(0)).setOnAction((ActionEvent event) -> {
            replacePane(attractionSearchPanePath);
        });

        //View All
        ((Hyperlink) nodeList.get(2)).setOnAction((ActionEvent event) -> {
            replacePane(viewAllAttractionsPanePath);
        });

        //Add
        ((Hyperlink) nodeList.get(4)).setOnAction((ActionEvent event) -> {
            replacePane(addAttractionPanePath);
        });
    }

    /**
     * Sets the functionality of the hyperlinks in the middle pane.
     */
    @FXML
    public void handleProfile() {
        setProfileLinks();
        replacePane(myProfilePanePath);
        ObservableList<Node> nodeList = linkBox.getChildren();

        //Profile
        ((Hyperlink) nodeList.get(0)).setOnAction((ActionEvent event) -> {
            replacePane(myProfilePanePath);
        });

        //Reviews
        ((Hyperlink) nodeList.get(2)).setOnAction((ActionEvent event) -> {
            replacePane(myReviewsPanePath);
        });

        //Delete
        ((Hyperlink) nodeList.get(4)).setOnAction((ActionEvent event) -> {
            handleDeleteProfile();
        });
    }

    /**
     * Replaces the main pane contents with the city search pane
     */
    @SuppressWarnings("Loading FXML document with JavaFX API of version 8.0.112 by JavaFX runtime of version 8.0.102")
    @FXML
    public void replacePane(String path) {
        if (Accounts.getCurrentUser().isSuspended() && Controller.restrictedPanes.contains(path)) {
            UIAlert.show("You are Suspended", "Users who are suspended may not create new\n"
                    + "reviews, cities, attractions, or categories.", Alert.AlertType.INFORMATION);
            return;
        }
        HBox box = (HBox) mainPane.getChildren().get(0);
        box.getChildren().clear();
        try {
            box.getChildren().add(FXMLLoader.load(getClass().getResource(path)));
        } catch (IOException ex) {
            //TODO: handle gracefully
        }
    }

    /**
     * Alerts the user of their choice and allows them to continue or cancel the request
     */
    @FXML
    public void handleDeleteProfile() {
        deleteCurrentUser();
    }
    public static void deleteCurrentUser() {
        if (Database.getCount("SELECT COUNT(email) AS result FROM user WHERE (isManager = 1)") <= 1) {
            UIAlert.show("Cannot Delete Manager", "At least one manager must exist at all times.",
                    Alert.AlertType.ERROR);
            return;
        }
        boolean result = UIAlert.show("Delete Account", "Are you sure you want to permanently "
                + "delete your account?", Alert.AlertType.WARNING, true, true);

        if (!result || !Accounts.isLoggedIn()) { return; }
        if (Accounts.getCurrentUser().delete()) {
            UIAlert.show("Account Deletion Successful", "Your PinPoint account has successfully\n"
                    + "been deleted. Hope to see you soon!", Alert.AlertType.CONFIRMATION);
            Controller.logout();
        }
    }

    @FXML
    private void setLinks() {
        ObservableList<Node> nodeList = linkBox.getChildren();
        ((Hyperlink) nodeList.get(0)).setText("SEARCH");
        ((Hyperlink) nodeList.get(2)).setText("VIEW ALL");
        ((Hyperlink) nodeList.get(4)).setText("ADD");
    }

    @FXML
    private void setProfileLinks() {
        ObservableList<Node> nodeList = linkBox.getChildren();
        ((Hyperlink) nodeList.get(0)).setText("PROFILE");
        ((Hyperlink) nodeList.get(2)).setText("REVIEWS");
        ((Hyperlink) nodeList.get(4)).setText("DELETE");
    }

}
