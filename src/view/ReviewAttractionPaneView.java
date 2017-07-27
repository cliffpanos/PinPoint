package view;

import controller.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Attraction;
import org.controlsfx.control.Rating;
import java.net.URL;
import java.sql.Date;

public class ReviewAttractionPaneView {

    @FXML private Hyperlink logout;
    @FXML private TextField newCityName;
    @FXML private TextArea commentArea;
    @FXML private Button submit;
    @FXML private Hyperlink back;
    @FXML private ComboBox newAttrRating;
    @FXML private Label title;
    @FXML  private VBox vBox;
    private Rating rating;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        rating = new Rating();
        rating.setMax(5);
        rating.setUpdateOnHover(true);
        vBox.getChildren().set(1, rating);
        setTitle();
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    @FXML
    public void setTitle() {
        String query = String.format("SELECT attrName "
                + "FROM nonpendingAttr "
                + "WHERE attrID='%d';", SearchSession.getAttractionId());
        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        String newTitle = String.format("New %s Review", attractions[0].getAttrName());
        title.setText(newTitle);
    }

    @FXML
    public void setEditTitle(String attrName) {
        String newTitle = String.format("Edit Your %s Review", attrName);
    }

    public void setRating(int newRating) {
        rating.setRating(newRating);
    }

    public void setCommentArea(String comment) {
        commentArea.setText(comment);
    }

    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/AttractionSearchPane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleSubmit() {
        if (isEditing) {
            isEditing = false;
            String query = String.format("UPDATE review SET rating='%d', comments='%s' " +
                    "WHERE email='%s' AND entityID='%d'",
                    (int) rating.getRating(), commentArea.getText(),
                    Accounts.getCurrentUser().getEmail(), SearchSession.getAttractionId());
            Database.update(query);
        } else {
            String user = Accounts.getCurrentUser().getEmail();
            int rate = (int) rating.getRating();
            String comment = (commentArea.getText().isEmpty()) ? "No comment." : commentArea.getText();
            int entityId = SearchSession.getAttractionId();

            Date date = Database.now();
            String query = String.format("INSERT INTO review " +
                            "VALUES ('%s', '%d', '%s', '%d', '%s');",
                    user, rate, comment, entityId, date);
            Database.insert(query);
        }
        handleBack();
    }
}
