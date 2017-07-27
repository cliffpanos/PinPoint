package view;

import controller.Accounts;
import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import resources.AVAsset;
import resources.Resources;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterView {

    public static Node view = FXBuilder.viewForFXML("RegisterView.fxml");
    public static Scene scene = new Scene((Parent) view);

    @FXML private Button submit;
    @FXML private Button cancel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Directs the user to the home page upon successful
     * account creation.
     */
    @FXML
    public void handleSubmit() {
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()
                || confirmPasswordField.getText().isEmpty()) {
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
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            UIAlert.show("Password Mismatch", "The passwords that you\n"
                    + "entered do not match.", Alert.AlertType.ERROR);
            return;
        }
        if (passwordField.getText().length() < 3) {
            UIAlert.show("Password Too Short", "Please enter a password that is\n"
                    + "a minumum of 3 characters.", Alert.AlertType.INFORMATION);
            return;
        }
        if (Controller.registerNewUser(emailField.getText(), passwordField.getText())) {
            UIAlert.show("Registration Successful!", "You have successfully registered for PinPoint.", Alert.AlertType.CONFIRMATION);
            LoginView.loginStage.setTitle("PinPoint Login");
            LoginView.loginStage.setScene(LoginView.loginScene);
            Resources.playAudio(AVAsset.Done);
            resetFields();
        }
    }

    @FXML
    public void cancelRegistration() {
        boolean decidedToCancel = true;
        if (!emailField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            decidedToCancel = UIAlert.show("Exit Registration?", "Are you sure that you want\n"
                    + "to exit registration?", Alert.AlertType.WARNING, true, true);
        }
        if (decidedToCancel) {
            resetFields();
            LoginView.loginStage.setTitle("PinPoint Login");
            LoginView.loginStage.setScene(LoginView.loginScene);
        }
    }

    @FXML
    public void resetFields() {
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }


}
