package view;

import controller.Controller;
import controller.Database;
import controller.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Attraction;

import java.net.URL;

public class ViewPendingAttractionsPaneView {

    @FXML private Hyperlink back;
    @FXML private Button approve;
    @FXML private Button reject;
    @FXML private TableView tableView;
    @FXML private TableColumn attractionCol;
    @FXML private TableColumn cityCol;
    @FXML private TableColumn addressCol;
    @FXML private TableColumn categoryCol;
    @FXML private TableColumn descriptionCol;
    @FXML private TableColumn hoursCol;
    @FXML private TableColumn submitterCol;
    @FXML private TableColumn ratingCol;
    @FXML private TableColumn commentCol;
    @FXML private ToggleGroup sort;
    private String sqlSort = "attrName ASC";


    @FXML
    public void initialize() {
        attractionCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("attrName"));
        cityCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("cityName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("address"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("category"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("description"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("pendingHours"));
        submitterCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("submitterEmail"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<Attraction, Double>("averageRating"));
        commentCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("pendingComment"));
        populateTable();
        setToggleListener();
    }

    /**
     * Changes the sort when a new toggle is selected.
     */
    private void setToggleListener() {
        sort.selectedToggleProperty().addListener(e -> {
            if (sort.getSelectedToggle() != null) {
                sqlSort = (String) sort.getSelectedToggle().getUserData();
                populateTable();
            }
        });
    }

    @FXML
    public void populateTable() {
        String query = String.format("SELECT attrID, attrName, cityName, address, description, catName, openTime, closeTime, submitterEmail, rating AS avgRating, comments "
                + "FROM review JOIN "
                    + "(SELECT * FROM City JOIN "
                        + "(SELECT * "
                            + "FROM UnderCategory natural JOIN (SELECT * FROM Attraction JOIN "
                                + "(SELECT entityID, submitterEmail, dateSubmitted, openDay, openTime, closeTime FROM ReviewableEntity LEFT OUTER JOIN OperationHours on entityID=attrID "
                                + "WHERE isPending=1) AS firstJoin on entityID=Attraction.attrID) AS secondjoin) AS thirdJoin on cityID=cityLocatedIn) AS fourthJoin on review.entityID=attrID "
                + "ORDER BY " + sqlSort + ";");
        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        ObservableList<Attraction> list = FXCollections.observableArrayList(attractions);
        tableView.setItems(list);

    }
    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleApprove() {
        if (selectedAttraction() == null) { return; }
        int id = selectedAttraction().getAttrID();
        if (Database.update(String.format("UPDATE ReviewableEntity SET isPending=0 WHERE entityID='%s';", id)).toBoolean()) {
            tableView.getItems().remove(selectedAttraction());
            tableView.refresh();
        }
    }

    @FXML
    public void handleReject() {
        if (selectedAttraction() == null) { return; }
        int id = selectedAttraction().getAttrID();
        boolean result = UIAlert.show("Reject Attraction?",
                "Are you sure you want to delete this Attraction?", Alert.AlertType.WARNING, true, true);
        if (result) {
            Database.delete(String.format("DELETE FROM ReviewableEntity WHERE entityID='%s';", id));
            Database.delete(String.format("DELETE FROM Attraction WHERE attrID='%s';", id));
            Database.delete(String.format("DELETE FROM Review WHERE entityID='%s';", id));
            tableView.getItems().remove(selectedAttraction());
            tableView.refresh();
        }
    }

    public Attraction selectedAttraction() {
        return (Attraction) tableView.getSelectionModel().getSelectedItem();
    }

}
