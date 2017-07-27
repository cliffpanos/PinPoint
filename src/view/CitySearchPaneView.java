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
import model.Attraction;
import model.Category;
import model.City;
import model.Review;

import java.net.URL;
import java.util.ResourceBundle;

public class CitySearchPaneView implements Initializable {

    @FXML private TextField userSearch;
    @FXML private Label cityName;
    @FXML private Label rating;
    @FXML private ListView attractionsList;
    @FXML private ListView reviewsList;
    @FXML private Hyperlink review;
    @FXML private ToggleGroup sort;
    @FXML private ChoiceBox allCities;
    @FXML private ComboBox categoryCombo;
    String orderBy = "rating DESC";
    Review[] reviews;
    String currentCategory = "All Categories";

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setToggleListener();
        setCategoryListener();
        populateCitiesBox();
        populateCategories();
        setCurrentCityId(SearchSession.getCityId());
        userSearch.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });
        allCities.valueProperty().addListener(change -> {
            handleSearch();
        });
        attractionsList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String attrName = (String) attractionsList.getSelectionModel().getSelectedItem();
                    if (attrName == null) { return; }
                    String query = String.format("SELECT * FROM nonpendingAttr WHERE attrName='%s'", attrName);
                    Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
                    SearchSession.setAttractionId(attractions[0].getAttrID());
                    URL path = getClass().getResource("fxml/AttractionSearchPane.fxml");
                    Controller.replacePane(attractionsList, path);
                }
            }
        });
        if (Accounts.getCurrentUser().isManager()) {
            reviewsList.setOnMouseClicked(click -> {
                if (click.getClickCount() == 2) {
                    if (reviewsList.getSelectionModel().getSelectedItem() == null) {
                        return;
                    }
                    int index = reviewsList.getSelectionModel().getSelectedIndex();
                    String query = String.format("select count(*) AS result from nonpendingCities JOIN review on cityID=Review.entityID WHERE Review.entityID='%d';",
                            reviews[index].getEntityID());
                    if (Database.getCount(query) <= 1) {
                        UIAlert.show("Cannot delete last review", "You may not delete the last review for a city.", Alert.AlertType.ERROR);
                        return;
                    }
                    if (click.getClickCount() == 2) {
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
                }
            });
        }
    }


    private void setCurrentCityId(int id) {
        SearchSession.setCityId(id);
        setTitle();
        populateAttractions(currentCategory);
        populateReviews();
    }

    private void setTitle() {
        String query = String.format("SELECT cityName, AVG(rating) AS avgRating "
                        + "FROM (nonpendingCities LEFT OUTER JOIN review on review.entityID=cityID) "
                        + "WHERE cityID='%d' "
                        + "GROUP BY cityName;", SearchSession.getCityId());
        City[] city = (City[]) Database.directRetrieval(query, R.City);
        cityName.setText(city[0].getCityName());
        rating.setText(Double.toString(city[0].getAverageRating()) + " / 5");
    }

    private void populateCitiesBox() {
        ObservableList<String> list = FXCollections.observableArrayList();
        City[] cities = Database.getAllCities();
        for (City city : cities) {
            list.add(city.getCityName());
        }
        allCities.setItems(list);
    }

    private void populateCategories() {
        ObservableList<String> list = FXCollections.observableArrayList("All Categories");
        Category[] categories = Database.getAllCategories();
        for (Category category : categories) { list.add(category.getCatName()); }
        categoryCombo.setItems(list);
    }

    private void populateAttractions(String catName) {
                                                            //TODO this should be in this SQL statements
        String query = String.format("SELECT attrName "
                        + "FROM Undercategory NATURAL JOIN "
                        + "(select attrName, attrID from nonpendingAttr JOIN nonpendingCities as zeroJoin "
                        + "WHERE cityID=%d AND cityLocatedIn=cityID group by attrName, attrID)  AS firstJ "
                        + "%s group by attrName;", SearchSession.getCityId(),
                catName.equals("All Categories") ? "" : String.format(" WHERE catName='%s'", catName));

        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Attraction a : attractions) {
                list.add(a.getAttrName());
        }
        if (list.isEmpty()) { list.add("No " + catName + " attractions"); }
        attractionsList.setItems(list);
    }

    private void populateReviews() {
        String query = String.format("SELECT entityId, comments, rating, email "
                + "FROM Review "
                + "WHERE entityId='%d' "
                + "ORDER BY %s", SearchSession.getCityId(), orderBy);
        reviews = (Review[]) Database.directRetrieval(query, R.Review);
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Review r : reviews) {
            list.add(String.format("%d/5    %s -%s", r.getRating(), r.getComments(), r.getEmail()));
        }
        reviewsList.setItems(list);
    }

    private void setCategoryListener() {
        categoryCombo.valueProperty().addListener(change -> {
            currentCategory = (String) categoryCombo.getSelectionModel().getSelectedItem();
            populateAttractions(currentCategory);
        });
    }

    /**
     * Changes the sort when a new toggle is selected.
     */
    private void setToggleListener() {
        sort.selectedToggleProperty().addListener(change -> {
                if (sort.getSelectedToggle() != null) {
                    orderBy = (String) sort.getSelectedToggle().getUserData();
                    populateReviews();
                }

            });
    }

    @FXML
    public void handleSearch() {
        String cityChoice = (String) allCities.getSelectionModel().getSelectedItem();
        String userCity = userSearch.getText();
        String city = userCity.equals("") ? cityChoice : userCity;
        String query = String.format("SELECT cityID FROM nonpendingCities " +
                "WHERE cityName='%s'", city);
        City[] cities = (City[]) Database.directRetrieval(query, R.City);
        if (cities.length == 0) {
            UIAlert.show("No cities found", "Please try another city.", Alert.AlertType.INFORMATION, false);
        } else {
            setCurrentCityId(cities[0].getCityId());
        }
        userSearch.clear();
    }

    @FXML
    public void handleReview() {
        if (Database.alreadyReviewed(Accounts.getCurrentUser().getEmail(), SearchSession.getCityId())) {
            boolean result = UIAlert.show("Already Reviewed", "You have already reviewed this city. " +
                    "Would you like to edit your review?", Alert.AlertType.INFORMATION, true);
            if (!result) {
                URL path = getClass().getResource("fxml/ReviewCityPane.fxml");
                ReviewCityPaneView controller = Controller.replacePane(userSearch, path);
                String query = String.format("SELECT cityName, rating, comments FROM review join city on cityID=entityID " +
                                "WHERE email='%s' AND entityID='%d';",
                        Accounts.getCurrentUser().getEmail(), SearchSession.getCityId());
                Review[] reviews = (Review[]) Database.directRetrieval(query, R.Review);
                Review review = reviews[0];
                controller.setIsEditing(true);
                controller.setTitle(review.getName());
                controller.setRating(review.getRating());
                controller.setCommentArea(review.getComments());
                controller.setEditableEntityId(SearchSession.getCityId());
            }
        } else {
            URL path = getClass().getResource("fxml/ReviewCityPane.fxml");
            Controller.replacePane(review, path);
        }
    }

}
