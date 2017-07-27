package view;

import controller.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.City;
import model.ReviewableEntity;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddCityPaneView {

    @FXML private TextField newCityName;
    @FXML private TextField newCityCountry;
    @FXML private TextField newCityState;
    @FXML private ComboBox newCityRating;
    @FXML private TextArea commentArea;

    @FXML
    public void initialize() {
        populateRating();
    }

    @FXML
    public void handleSubmit() {
        String user = Accounts.getCurrentUser().getEmail();
        int rating = (int) newCityRating.getSelectionModel().getSelectedItem();
        int entityId = Database.getNewEntityId();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date today = Calendar.getInstance().getTime();
        String date = format.format(today);
        String cityName = newCityName.getText();
        String cityCountry = newCityCountry.getText();
        String cityState = newCityState.getText();
        String comment = commentArea.getText();
        if (!user.equals("") && !cityName.equals("") && !cityCountry.equals("") && !comment.equals("")) {
            Database.submitReviewableEntity(entityId, user, date);
            Database.submitPendingCity(entityId, cityName, cityCountry, cityState);
            Database.submitReview(user, rating, comment, entityId, date);

            String extraText = Accounts.getCurrentUser().isManager() ? "" :  " We will review your request shortly.";
            UIAlert.show("New City Submitted", "Thank you for submitting a " +
                    "new city!" + extraText, Alert.AlertType.INFORMATION);
            URL path = getClass().getResource("fxml/ViewAllCitiesPane.fxml");
            Controller.replacePane(newCityName, path);
        } else {
            UIAlert.show("Missing Field(s)", "Please fill out all required fields.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void populateRating() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        newCityRating.setItems(list);
        newCityRating.getSelectionModel().selectLast();
    }

}
