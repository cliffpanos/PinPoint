package view;

import controller.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AttractionSearchPaneView implements Initializable {

    @FXML private TextField userSearch;
    @FXML private Label attractionName;
    @FXML private Label rating;
    @FXML private ChoiceBox choiceBox;
    @FXML private ListView categoryList;
    @FXML private ListView reviewsList;
    @FXML private Hyperlink review;
    @FXML private ToggleGroup sort;
    @FXML private Text addressLabel;
    @FXML private Text hoursLabel;
    @FXML private Text contactLabel;
    @FXML private Text descriptionLabel;
    @FXML private Label contactTypeLabel;
    private String orderBy = "rating DESC";
    Review[] reviews;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setToggleListener();
        populateChoiceBox();
        setCurrentAttrId(SearchSession.getAttractionId());
        userSearch.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });
        choiceBox.valueProperty().addListener(change -> {
            handleSearch();
        });
        if (Accounts.getCurrentUser().isManager()) {
            reviewsList.setOnMouseClicked(click -> {
                    if (click.getClickCount() == 2) {
                        if (reviewsList.getSelectionModel().getSelectedItem() == null) { return; }
                        int index = reviewsList.getSelectionModel().getSelectedIndex();
                        String query = String.format("select count(*) AS result from nonpendingAttr JOIN review on attrID=Review.entityID WHERE Review.entityID='%d';",
                                reviews[index].getEntityID());
                        if (Database.getCount(query) <= 1) {
                            UIAlert.show("Cannot delete last review", "You may not delete the last review for an attraction.", Alert.AlertType.ERROR);
                            return;
                        }
                        boolean result = UIAlert.show("Delete Review?", "Are you sure you want to delete this review?",
                                Alert.AlertType.WARNING, true, true);
                        if (result) {
                            Review review = reviews[index];
                            String query2 = String.format("DELETE FROM review WHERE " +
                                    "email='%s' AND entityID='%d';", review.getEmail(), review.getEntityID());
                            Database.delete(query2);
                            populateReviews();
                        }
                    }
                });
        }
    }

    private void setCurrentAttrId(int id) {
        SearchSession.setAttractionId(id);
        setTitle();
        populateCategories();
        populateReviews();
        setDetails();
    }

    private void setTitle() {
        String query = String.format("SELECT address, description, attrName, AVG(rating) AS avgRating "
                + "FROM (nonpendingAttr JOIN review on review.entityID=attrID) "
                + "WHERE attrID='%d' "
                + "GROUP BY attrName;", SearchSession.getAttractionId());
        Attraction[] attraction = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        attractionName.setText(attraction[0].getAttrName());
        rating.setText(Double.toString(attraction[0].getAverageRating()) + " / 5");
        addressLabel.setText(attraction[0].getAddress());
        descriptionLabel.setText(attraction[0].getDescription());
    }

    private void setDetails() {

        int attrID = SearchSession.getAttractionId();
        String opQuery = String.format(
                "SELECT openTime, closeTime, openDay FROM operationHours WHERE attrID=%d;", attrID);
        OperationHours[] hours = (OperationHours[]) Database.directRetrieval(opQuery, R.OperationHours);
        if (hours.length > 0) {
            hoursLabel.setText(hours[0].getHours());
        } else {
            hoursLabel.setText("None listed");
        }
        String contactQuery = String.format(
                "SELECT contactMethod, contactValue FROM contactInfo WHERE attrID=%d;", attrID);
        ContactInfo[] contactInfos = (ContactInfo[]) Database.directRetrieval(contactQuery, R.ContactInfo);
        if (contactInfos.length > 0) {
            contactLabel.setText(contactInfos[0].getContactValue());
            contactTypeLabel.setText(contactInfos[0].getContactMethod());
        } else {
            contactLabel.setText("None listed");
            contactTypeLabel.setText("Contact");
        }
    }

    private void populateChoiceBox() {
        String query = "SELECT attrName FROM nonpendingAttr";
        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Attraction a : attractions) {
            list.add(a.getAttrName());
        }
        choiceBox.setItems(list);
    }

    private void populateCategories() {
        String query = String.format("SELECT catName "
                + "FROM undercategory "
                + "WHERE attrID='%d' "
                + "ORDER BY catName DESC;", SearchSession.getAttractionId());
        UnderCategory[] categories = (UnderCategory[]) Database.directRetrieval(query, R.UnderCategory);
        ObservableList<String> list = FXCollections.observableArrayList();
        for (UnderCategory c : categories) {
            list.add(c.getCatName());
        }
        categoryList.setItems(list);
    }

    private void populateReviews() {
        String query = String.format("SELECT entityID, comments, rating, email  "
                + "FROM Review "
                + "WHERE entityId='%d' "
                + "ORDER BY %s", SearchSession.getAttractionId(), orderBy);
        reviews = (Review[]) Database.directRetrieval(query, R.Review);
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Review r : reviews) {
            list.add(String.format("%d/5    %s -%s", r.getRating(), r.getComments(), r.getEmail()));
        }
        reviewsList.setItems(list);
    }

    /**
     * Changes the sort when a new toggle is selected.
     */
    private void setToggleListener() {
        sort.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (sort.getSelectedToggle() != null) {
                    orderBy = (String) sort.getSelectedToggle().getUserData();
                    populateReviews();
                }

            }
        });
    }

    @FXML
    public void handleSearch() {
        String attrChoice = (String) choiceBox.getSelectionModel().getSelectedItem();
        String userAttr = userSearch.getText();
        String attr = userAttr.equals("") ? attrChoice : userAttr;
        String query = String.format("SELECT attrID FROM nonpendingAttr " +
                "WHERE attrName='%s'", attr);
        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        if (attractions.length == 0) {
            UIAlert.show("No Attractions Found",
                    "Please search for another attraction.",
                    Alert.AlertType.INFORMATION, false);
        } else {
            Attraction attraction = attractions[0];
            setCurrentAttrId(attraction.getAttrID());
        }
        userSearch.clear();
    }

    @FXML
    public void handleReview() {
        if (Database.alreadyReviewed(Accounts.getCurrentUser().getEmail(), SearchSession.getAttractionId())) {
            boolean result = UIAlert.show("Already Reviewed", "You have already reviewed this attraction. " +
                    "Would you like to edit your review?", Alert.AlertType.INFORMATION, true);
            if (!result) {
                URL path = getClass().getResource("fxml/ReviewAttractionPane.fxml");
                ReviewAttractionPaneView controller = Controller.replacePane(userSearch, path);
                String query = String.format("SELECT attrName, rating, comments FROM review join attraction on attrID=entityID " +
                                "WHERE email='%s' AND entityID='%d';",
                        Accounts.getCurrentUser().getEmail(), SearchSession.getAttractionId());
                Review[] reviews = (Review[]) Database.directRetrieval(query, R.Review);
                Review review = reviews[0];
                controller.setIsEditing(true);
                controller.setEditTitle(review.getName());
                controller.setRating(review.getRating());
                controller.setCommentArea(review.getComments());
            }
        } else {
            URL path = getClass().getResource("fxml/ReviewAttractionPane.fxml");
            Controller.replacePane(userSearch, path);
        }
    }


}
