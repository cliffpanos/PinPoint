package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Cliff on 7/4/17.
 *
 * USE THIS CLASS to load fxml views
 */
public class FXBuilder {

    private FXBuilder() { //Private constructor to prevent use
    }

    /**
     * Call this method to return a new Scene, likely to put in a new window
     *
     * @param fxmlFileName the exact name of the fxml file. Ex: "Main.fxml"
     * @return a new Scene containing the loginView specified by the fxml file
     */
    @SuppressWarnings("Loading FXML document with JavaFX API of version 8.0.112 by JavaFX runtime of version 8.0.102")
    public static Scene sceneForFXML(String fxmlFileName) {
        Parent node = (Parent) viewForFXML(fxmlFileName);
        return new Scene(node);
    }

    static Node viewForFXML(String fxmlFileName) {
        Node node;
        if (fxmlFileName == null || fxmlFileName.isEmpty()) {
            return new StackPane();
        }
        try {
            node = FXMLLoader.load(FXBuilder.class
                    .getResource("fxml/" + fxmlFileName));
        } catch (java.io.IOException ioe) {
            System.out.println("FXML file specified does not exist");
            return new StackPane();
        }
        return node;
    }

    @Deprecated //Bad to call since all styling should be done in SceneBuilder
    public static void style(Scene sceneToStyle) {
        System.out.println(sceneToStyle == null);
        sceneToStyle.getStylesheets().clear();
        sceneToStyle.getStylesheets().add(FXBuilder.class
                .getResource("fonts/style.css").toExternalForm());
    }

}
