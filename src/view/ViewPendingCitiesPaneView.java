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
import model.City;

import java.net.URL;

public class ViewPendingCitiesPaneView {

    @FXML private TableView tableView;
    @FXML private TableColumn cityCol;
    @FXML private TableColumn countryCol;
    @FXML private TableColumn submitterCol;
    @FXML private TableColumn ratingCol;
    @FXML private TableColumn commentCol;
    @FXML private Hyperlink back;
    @FXML private Button approve;
    @FXML private Button reject;
    @FXML private ToggleGroup sort;
    private String sqlSort = "cityName ASC";

    @FXML
    public void initialize() {
        cityCol.setCellValueFactory(new PropertyValueFactory<City, String>("cityName"));
        countryCol.setCellValueFactory(new PropertyValueFactory<City, String>("country"));
        submitterCol.setCellValueFactory(new PropertyValueFactory<City, String>("submitterEmail"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<City, Double>("averageRating"));
        commentCol.setCellValueFactory(new PropertyValueFactory<City, String>("pendingComment"));
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
        String query = String.format("SELECT cityID, cityName, country, state, email AS submitterEmail, rating AS avgRating, comments AS pendingComment "
                + "FROM review JOIN "
                        + "(SELECT * FROM City JOIN Reviewableentity ON entityID=cityID "
                        + "WHERE isPending=1) "
                + "AS firstjoin on review.entityID=cityID " +
        "ORDER BY " + sqlSort + ";");
        City[] cities = (City[]) Database.directRetrieval(query, R.City);
        ObservableList<City> list = FXCollections.observableArrayList(cities);
        tableView.setItems(list);

    }
    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleApprove() {
        if (selectedCity() == null) { return; }
        int id = selectedCity().getCityId();
        if (Database.update(String.format("UPDATE ReviewableEntity SET isPending=0 WHERE entityID='%s';", id)).toBoolean()) {
            tableView.getItems().remove(selectedCity());
            tableView.refresh();
        }
    }

    @FXML
    public void handleReject() {
        if (selectedCity() == null) { return; }
        int id = selectedCity().getCityId();
        boolean result = UIAlert.show("Reject City?",
                "Are you sure you want to delete this City?", Alert.AlertType.WARNING, true, true);
        if (result) {
            Database.delete(String.format("DELETE FROM ReviewableEntity WHERE entityID='%s';", id));
            Database.delete(String.format("DELETE FROM City WHERE cityID='%s';", id));
            Database.delete(String.format("DELETE FROM Review WHERE entityID='%s';", id));
            tableView.getItems().remove(selectedCity());
            tableView.refresh();
        }
    }

    public City selectedCity() {
        return (City) tableView.getSelectionModel().getSelectedItem();
    }
}
