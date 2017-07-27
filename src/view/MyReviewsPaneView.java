package view;

import controller.Accounts;
import controller.Controller;
import controller.Database;
import controller.R;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Review;
import model.User;

import java.net.URL;

public class MyReviewsPaneView {

    @FXML private TableView reviewsTable;
    @FXML private TableColumn nameCol;
    @FXML private TableColumn ratingCol;
    @FXML private TableColumn commentCol;
    @FXML private TableColumn editCol;
    @FXML private ToggleGroup sort;
    private String sqlSort = "rating DESC";

    @FXML
    public void initialize() {
        setToggleListener();
        nameCol.setCellValueFactory(new PropertyValueFactory<Review, String>("name"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<Review, Integer>("rating"));
        commentCol.setCellValueFactory(new PropertyValueFactory<Review, String>("comments"));
        editCol.setCellFactory(tc -> new EditCell<>());
        editCol.setMaxWidth(110);
        editCol.setMinWidth(110);
        populateTable();
    }

    /**
     * Changes the sort when a new toggle is selected.
     */
    private void setToggleListener() {
        sort.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (sort.getSelectedToggle() != null) {
                    sqlSort = (String) sort.getSelectedToggle().getUserData();
                    populateTable();
                }

            }
        });
    }

    public void populateTable() {
        String email = Accounts.getCurrentUser().getEmail();
        String query = String.format("SELECT cityID AS entityID, cityName AS name, rating, comments " +
                "FROM City JOIN Review ON cityID=entityID " +
                "WHERE email='%s' " +
                "UNION " +
                "SELECT attrID AS entityID, attrName AS name, rating, comments " +
                "FROM Attraction JOIN Review ON attrID=entityID " +
                "WHERE email='%s' " +
                "ORDER BY %s;", email, email, sqlSort);
        Review[] reviews = (Review[]) Database.directRetrieval(query, R.Review);
        ObservableList<Review> list = FXCollections.observableArrayList(reviews);
        reviewsTable.setItems(list);
    }

    public class EditCell<T> extends TableCell<T, Void> {

        private final Hyperlink editLink;
        private final Hyperlink deleteLink;

        public EditCell() {
            editLink = new Hyperlink("Edit");
            editLink.setOnAction(evt -> {
                // edit row item from tableview
                ObservableList<Review> reviews = (ObservableList<Review>) getTableView().getItems();
                Review review = reviews.get(getTableRow().getIndex());
                URL path = getClass().getResource("fxml/ReviewCityPane.fxml");
                ReviewCityPaneView controller = Controller.replacePane(this, path);
                if (Accounts.getCurrentUser().isSuspended()) { return; }
                controller.setTitle(review.getName());
                controller.setRating(review.getRating());
                controller.setCommentArea(review.getComments());
                controller.setEditableEntityId(review.getEntityID());
                controller.getBackButton().setOnAction(e -> {
                    URL backPath = getClass().getResource("fxml/MyReviewsPane.fxml");
                    Controller.replacePane(controller.getBackButton(), backPath);
                    });
                controller.setIsEditing(true);
            });
            deleteLink = new Hyperlink("Delete");
            deleteLink.setOnAction(evt -> {
                ObservableList<Review> reviews = (ObservableList<Review>) getTableView().getItems();
                Review review = reviews.get(getTableRow().getIndex());
                String query = String.format("select count(*) AS result from reviewableEntity JOIN review on reviewableEntity.entityID=Review.entityID WHERE Review.entityID='%d';",
                        review.getEntityID());
                if (Database.getCount(query) <= 1) {
                    UIAlert.show("Cannot delete last review", "You may not delete the last review.", Alert.AlertType.ERROR);
                    return;
                }
                boolean result = UIAlert.show("Confirm Delete Review", "Are you sure "
                        + "you want to delete this review permanently?", Alert.AlertType.WARNING, true, true);
                if (result) {
                    if (review.delete()) {
                        getTableView().getItems().remove(getTableRow().getIndex());
                    } else {
                        UIAlert.show("Review Not Deleted", "The review could not be deleted; an error occurred.", Alert.AlertType.ERROR);
                    }
                }
            });

        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            HBox box = new HBox(editLink, deleteLink);
            setGraphic(empty ? null : box);
        }

    }

}
