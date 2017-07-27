package view;

import controller.Accounts;
import controller.Controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserPaneView {

    @FXML private Hyperlink back;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField confirmField;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @FXML
    public void handleSubmit() {
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()
                || confirmField.getText().isEmpty()) {
            UIAlert.show("Enter All Information", "Please enter all required\n"
                    + "information for all fields.", Alert.AlertType.ERROR);
            return;
        }
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);   //Check email pattern
        Matcher matcher = pattern.matcher(emailField.getText());
        if (!matcher.matches()) {
            UIAlert.show("Invalid Email Address", "The email address that you entered is invalid.\n"
                    + "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }
        if (!passwordField.getText().equals(confirmField.getText())) {
            UIAlert.show("Password Mismatch", "The passwords that you\n"
                    + "entered do not match.", Alert.AlertType.ERROR);
            return;
        }
        if (passwordField.getText().length() < 3) {
            UIAlert.show("Password Too Short", "Please enter a password that is\n"
                    + "a minumum of 3 characters.", Alert.AlertType.INFORMATION);
            return;
        }
        if (Accounts.registerNewUser(emailField.getText(), passwordField.getText())) {
            UIAlert.show("User Creation Successsful", "The new user has been added to PinPoint!", Alert.AlertType.INFORMATION);
            URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
            Controller.replacePane(back, path);
        }


    }

    @FXML
    public void handleBack() {
        boolean decidedToCancel = true;
        if (!emailField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            decidedToCancel = UIAlert.show("Exit New User Creation?", "Are you sure that you want\n"
                    + "to exit new user creation?", Alert.AlertType.WARNING, true, true);
        }
        if (decidedToCancel) {
            URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
            Controller.replacePane(back, path);
        }
    }
}
