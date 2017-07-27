package view;

import controller.Accounts;
import controller.Controller;
import controller.Database;
import controller.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Category;
import model.City;

import javax.xml.crypto.Data;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddAttractionPaneView {

    @FXML private TextField newAttrName;
    @FXML private TextField newAttrAddress;
    @FXML private ComboBox newAttrCity;
    @FXML private TextField newAttrDescription;
    @FXML private TextField newAttrContact;
    @FXML private ListView catList;
    @FXML private ComboBox newAttrRating;
    @FXML private ComboBox openCombo;
    @FXML private ComboBox closeCombo;
    @FXML private ComboBox contactCombo;
    @FXML private TextArea commentArea;

    @FXML
    public void initialize() {
        populateStaticComboBoxes();
        populateCitiesBox();
        populateCategories();
    }

    private void populateStaticComboBoxes() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        newAttrRating.setItems(list);
        newAttrRating.getSelectionModel().selectLast();
        ObservableList<String> times = FXCollections.observableArrayList("None");
        for (int i = 0; i < 24; i++) { times.add(i + ":00:00"); }
        openCombo.setItems(times);
        closeCombo.setItems(times);
        openCombo.getSelectionModel().select(10);
        closeCombo.getSelectionModel().select(18);
        contactCombo.setItems(FXCollections.observableArrayList("Phone", "Email"));
        contactCombo.getSelectionModel().selectFirst();
    }

    private void populateCitiesBox() {
            ObservableList<String> list = FXCollections.observableArrayList();
            City[] cities = Database.getAllCities();
            for (City city : cities) {
                list.add(city.getCityName());
            }
            newAttrCity.setItems(list);
            newAttrCity.getSelectionModel().selectFirst();
    }

    private void populateCategories() {
        catList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<String> list = FXCollections.observableArrayList();
        Category[] categories = Database.getAllCategories();
        for (Category category : categories) {
            list.add(category.getCatName());
        }
        catList.setItems(list);
    }

    @FXML
    public void handleSubmit() {
        String user = Accounts.getCurrentUser().getEmail();
        int rating = (int) newAttrRating.getSelectionModel().getSelectedItem();
        int entityId = Database.getNewEntityId();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date today = Calendar.getInstance().getTime();
        String date = format.format(today);
        String attrName = newAttrName.getText();
        String attrAddress = newAttrAddress.getText();
        String attrCity = (String) newAttrCity.getSelectionModel().getSelectedItem();
        String attrDescription = newAttrDescription.getText();
        String attrOpen = (String) openCombo.getSelectionModel().getSelectedItem();
        String attrClose = (String) closeCombo.getSelectionModel().getSelectedItem();
        String attrContactMethod = (String) contactCombo.getSelectionModel().getSelectedItem();
        String attrContact = newAttrContact.getText();
        ObservableList<String> categories = catList.getSelectionModel().getSelectedItems();
        String comment = commentArea.getText();

        if (!user.equals("") && !attrName.equals("") && !attrDescription.equals("")
                && !attrAddress.equals("") && !comment.equals("")) {
            Database.submitReviewableEntity(entityId, user, date);
            Database.submitPendingAttr(entityId, attrName, attrAddress,
                    attrDescription, getCityId(attrCity));
            for (String category : categories) {
                Database.submitAttractionCategory(entityId, category);
            }
            if (!attrOpen.equals("None") && !attrClose.equals("None")) {
                Database.submitHours(entityId, "MTWTHFSSU", attrOpen, attrClose);
            }
            if (!attrContact.isEmpty()) {
                Database.submitContactInfo(entityId, attrContactMethod, attrContact);
            }
            Database.submitReview(user, rating, comment, entityId, date);

            String extraText = Accounts.getCurrentUser().isManager() ? "" :  " We will review your request shortly.";
            UIAlert.show("New Attraction Submitted", "Thank you for submitting a " +
                    "new attraction!" + extraText, Alert.AlertType.INFORMATION);
            URL path = getClass().getResource("fxml/ViewAllAttractionsPane.fxml");
            Controller.replacePane(newAttrName, path);
        } else {
            UIAlert.show("Missing Field(s)", "Please fill out all required fields.", Alert.AlertType.ERROR);
        }
    }

    private int getCityId(String cityName) {
        String query = String.format("SELECT cityID FROM nonpendingCities " +
                "WHERE cityName='%s'", cityName);
        City[] cities = (City[]) Database.directRetrieval(query, R.City);
        return cities[0].getCityId();
    }
}
