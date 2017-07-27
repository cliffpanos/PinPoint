package view;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import java.net.URL;

public class ManagerProfilePaneView {

    @FXML private Button viewUsers;
    @FXML private Button addUser;
    @FXML private Button viewCategories;
    @FXML private Button addCategory;
    @FXML private Button viewPendingCities;
    @FXML private Button viewPendingAttractions;
    @FXML private Hyperlink back;


    @FXML
    public void handleViewAllUsers() {
        URL path = getClass().getResource("fxml/ViewAllUsersPane.fxml");
        Controller.replacePane(viewUsers, path);
    }

    @FXML
    public void handleAddUser() {
        URL path = getClass().getResource("fxml/AddUserPane.fxml");
        Controller.replacePane(addUser, path);
    }

    @FXML
    public void handleViewAllCategories() {
        URL path = getClass().getResource("fxml/ViewAllCategoriesPane.fxml");
        Controller.replacePane(viewCategories, path);
    }

    @FXML
    public void handleAddCategory() {
        URL path = getClass().getResource("fxml/AddCategoryPane.fxml");
        Controller.replacePane(addCategory, path);
    }

    @FXML
    public void handleViewPendingCities() {
        URL path = getClass().getResource("fxml/ViewPendingCitiesPane.fxml");
        Controller.replacePane(viewPendingCities, path);
    }

    @FXML
    public void handleViewPendingAttractions() {
        URL path = getClass().getResource("fxml/ViewPendingAttractionsPane.fxml");
        Controller.replacePane(viewPendingAttractions, path);
    }

    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/MyProfilePane.fxml");
        Controller.replacePane(back, path);
    }
}
