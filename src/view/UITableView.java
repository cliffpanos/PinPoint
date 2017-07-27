package view;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Created by Cliff on 7/5/17.
 *
 * Java version of Apple's UITableViewController from UIKit as of iOS 10
 */
public class UITableView {

    private Delegate delegate = null;
    private int numberOfRowsInSection = 0;

    private VBox verticalLayout;
    private ScrollPane scrollView;

    @FXML
    public void initialize() {

    }

    public void reloadTable() {

        if (delegate == null) {
            return;
        }

        int numberOfRows = delegate.numberOfRowsInSection();

    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }







    interface Delegate {

        int numberOfRowsInSection();
        UITableView.Cell cellForRowAtIndexPath();

    }


    //Meant to be subclassed
    abstract class Cell {

    }

}
