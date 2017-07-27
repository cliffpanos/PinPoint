package view;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import resources.AVAsset;
import resources.Resources;

import java.awt.GraphicsEnvironment;

/**
 * Created by Cliff on 7/4/17.
 *
 * Root scene for the loginView
 */
public class RootView {

    public static java.awt.Rectangle screenBounds =
            GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getMaximumWindowBounds();

    public static Scene scene = HomeView.scene();
    public static Stage stage = new Stage();
        static { stage.setScene(scene); }

    public static double width = scene.getWidth();
    public static double height = scene.getHeight();

    @FXML private VBox vBox;
    @FXML private AnchorPane anchorPane;

}
