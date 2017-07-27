package view;

import controller.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.City;
import org.controlsfx.control.Rating;
import java.net.URL;
import java.sql.Date;

public class ReviewCityPaneView {

    @FXML private TextArea commentArea;
    @FXML private Button submit;
    @FXML private Hyperlink back;
    @FXML private ComboBox newCityRating;
    @FXML private Label title;
    @FXML private VBox vBox;
    private Rating rating;
    private boolean isEditing = false;
    private int editableEntityId;

    @FXML
    public void initialize() {
        setTitle();
        rating = new Rating();
        rating.setMax(5);
        rating.setUpdateOnHover(true);
        vBox.getChildren().set(1, rating);
    }

    @FXML
    public void setTitle() {
        String query = String.format("SELECT cityName "
                + "FROM nonpendingCities "
                + "WHERE cityID='%d';", SearchSession.getCityId());
        City[] cities = (City[]) Database.directRetrieval(query, R.City);
        String newTitle = String.format("New %s Review", cities[0].getCityName());
        title.setText(newTitle);
    }

    public void setTitle(String newTitle) {
        title.setText(String.format("Edit Your %s Review", newTitle));
    }

    public void setCommentArea(String comment) {
        commentArea.setText(comment);
    }

    public void setRating(int newRating) {
        rating.setRating(newRating);
    }

    public void setIsEditing(boolean editable) {
        isEditing = editable;
    }

    public void setEditableEntityId(int newId) {
        editableEntityId = newId;
    }

    public Hyperlink getBackButton() {
        return back;
    }
    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/CitySearchPane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleSubmit() {
        if (isEditing) {
            setIsEditing(false);
            submitEdit();
            return;
        }
        String user = Accounts.getCurrentUser().getEmail();
        int rate = (int) rating.getRating();
        String comment = (commentArea.getText().isEmpty()) ? "No comment." : commentArea.getText();
        int entityId = SearchSession.getCityId();

        Date date = Database.now();
        String query = String.format("INSERT INTO review " +
                    "VALUES ('%s', '%d', '%s', '%d', '%s');",
                user, rate, comment, entityId, date);
        Database.insert(query);
        handleBack();
    }

    public void submitEdit() {
        String query = String.format("UPDATE review " +
                "SET rating='%f', comments='%s' " +
                "WHERE email='%s' AND entityID='%d';",
                rating.getRating(), commentArea.getText(),
                Accounts.getCurrentUser().getEmail(), editableEntityId);
        Database.update(query);
        URL path = getClass().getResource("fxml/MyReviewsPane.fxml");
        Controller.replacePane(back, path);
    }
}
