package view;

import resources.AVAsset;
import resources.Resources;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

public class UIAlert {


    public static void show(String title, String primaryText,
        Alert.AlertType alertType) {

        show(title, primaryText, alertType, false);

    }

    public static boolean show(String title, String primaryText,
                               Alert.AlertType alertType, boolean requiresUserInput) {
        return show(title, primaryText, alertType, requiresUserInput, false);
    }

    public static boolean show(String title, String primaryText,
        Alert.AlertType alertType, boolean requiresUserInput, boolean isDestructive) {

        Alert newAlert = new Alert(alertType);
        newAlert.setTitle(title);
        newAlert.setHeaderText(primaryText);

        Resources.playAudio(AVAsset.ErrorSound);

        if (requiresUserInput) {

            ButtonType cancel = new ButtonType(isDestructive ? "Cancel & Go Back" : "Cancel",
                isDestructive ? ButtonBar.ButtonData.OK_DONE : ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType ok = new ButtonType(isDestructive ? "Yes" : "OK",
                    isDestructive ? ButtonBar.ButtonData.CANCEL_CLOSE : ButtonBar.ButtonData.YES);

            if (isDestructive) {
                newAlert.getButtonTypes().setAll(ok, cancel);
            } else {
                newAlert.getButtonTypes().setAll(cancel, ok);
            }

            Optional<ButtonType> result = newAlert.showAndWait();

            if (result.get() == cancel) {
                return !isDestructive;
            }
            if (result.get() == ok) {
                return isDestructive;
            }
        } else {
            newAlert.showAndWait();
        }

        return false;
    }

}
