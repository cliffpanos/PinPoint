package view;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import resources.AVAsset;
import resources.Resources;


public class LoginView {

    public static Node loginView = FXBuilder.viewForFXML("LoginView.fxml");
    public static Scene loginScene = new Scene((Parent) loginView);
    public static Stage loginStage = null; //Initialized by Runner.start()

    @FXML private TextField username;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button forgotButton;
    @FXML private Hyperlink register;


    @FXML
    public void initialize() {
        passwordField.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

    }

    /**
     * Directs the user to the home page if login is successful
     */
    @FXML
    public void handleLogin() {
        String email = username.getText();
        String pw = passwordField.getText();
        if (Controller.login(email, pw)) {
            resetFields();
            Resources.playAudio(AVAsset.Whoosh);
        }
    }

    /**
     * Directs the user to the account registration page
     */
    @FXML
    public void handleRegister() {
        LoginView.loginStage.setScene(RegisterView.scene);
        LoginView.loginStage.setTitle("Register for PinPoint");
        LoginView.loginStage.show();
        resetFields();
    }

    public void resetFields() {
        username.clear();
        passwordField.clear();
    }

}
