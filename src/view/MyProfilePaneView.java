package view;

import controller.Accounts;
import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.ContactInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class MyProfilePaneView implements Initializable {

    @FXML private Hyperlink managerLink;
    @FXML private Label emailLabel;
    @FXML private Label dateLabel;
    @FXML private Label instructionsLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Accounts.isLoggedIn()) {
            Controller.logout();    //Safeguard that a user is logged in
            return;
        }
        managerLink.setVisible(Accounts.getCurrentUser().isManager());
        emailLabel.setText(Accounts.getCurrentUser().getEmail());
        dateLabel.setText("You have been a member of PinPoint since "
                + Accounts.getCurrentUser().getDateJoined() + ".");
        if (Accounts.getCurrentUser().isSuspended()) {
            instructionsLabel.setText("Your account is currently suspended.");
        }
    }

    @FXML
    public void handleManager() {
        String managerPanePath = "fxml/ManagerProfilePane.fxml";
        URL path = getClass().getResource(managerPanePath);
        Controller.replacePane(managerLink, path);
    }

}

