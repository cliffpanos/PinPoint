package view;

import controller.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Attraction;
import model.City;
import org.w3c.dom.Attr;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewAllAttractionsPaneView implements Initializable {

    @FXML private Hyperlink logout;
    @FXML private TableView attrTable;
    @FXML private TableColumn nameCol;
    @FXML private TableColumn catCol;
    @FXML private TableColumn locCol;
    @FXML private TableColumn avgRatingCol;
    @FXML private TableColumn numRatingsCol;
    @FXML private ToggleGroup sort;
    private String sqlSort = "cityName ASC";

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameCol.setCellValueFactory(new PropertyValueFactory<Attraction, String>("attrName"));
        catCol.setCellValueFactory(new PropertyValueFactory<Attraction, Double>("category"));
        locCol.setCellValueFactory(new PropertyValueFactory<Attraction, Integer>("cityName"));
        numRatingsCol.setCellValueFactory(new PropertyValueFactory<Attraction, Integer>("numberOfReviews"));
        avgRatingCol.setCellValueFactory(new PropertyValueFactory<Attraction, Integer>("averageRating"));
        if (Accounts.getCurrentUser().isManager()) {
            TableColumn deleteCol = new TableColumn("Delete");
            deleteCol.setCellFactory(tc -> new DeleteCell<Attraction>());
            attrTable.getColumns().add(deleteCol);
        }
        populateTable();
        setToggleListener();
        setDoubleClickListener();
    }

    public class DeleteCell<T> extends TableCell<T, Void> {

        private final Hyperlink delete;
        public DeleteCell() {
            delete = new Hyperlink("Delete");
            delete.setOnAction(evt -> {
                boolean result = UIAlert.show("Delete Attraction",
                        "Are you sure you want to delete this attraction?",
                        Alert.AlertType.WARNING, true, true);
                if (result) {
                    ObservableList<Attraction> attractions = (ObservableList<Attraction>) getTableView().getItems();
                    Attraction attraction = attractions.get(getTableRow().getIndex());
                    int id = attraction.getAttrID();
                    if (SearchSession.getAttractionId() == id) {
                        SearchSession.setAttractionId(9);
                    }
                    Database.delete(String.format("DELETE FROM ReviewableEntity WHERE entityID='%s';", id));
                    populateTable();
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : delete);
        }

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

    private void setDoubleClickListener() {
        attrTable.setRowFactory( tv -> {
            TableRow<Attraction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Attraction attraction = row.getItem();
                    SearchSession.setAttractionId(attraction.getAttrID());
                    URL path = getClass().getResource("fxml/AttractionSearchPane.fxml");
                    Controller.replacePane(attrTable, path);
                }
            });
            return row ;
        });
    }

    /**
     * Redirects the user to the login screen.
     */
    @FXML
    public void handleLogout() {
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(FXBuilder.sceneForFXML("LoginView.fxml"));
        stage.show();
    }

    @FXML
    public void populateTable() {
        String query = String.format("SELECT attrName, catName, cityName, avgRating, countRating, attrID "
                + "FROM City JOIN "
                +       "(SELECT attrName, catName, cityLocatedIn, AVG(rating) AS avgRating, COUNT(rating) as countRating, attrID "
                +       "FROM (Review JOIN "
                +           "(Select * "
                +           "FROM nonPendingAttr NATURAL JOIN UnderCategory) as firstJoin ON "
                +       "firstJoin.entityID=review.entityID) "
                + "GROUP BY attrID, catName) AS secondJoin "
                + "ON cityID=cityLocatedIn "
                + "ORDER BY " + sqlSort + ";");
        Attraction[] attractions = (Attraction[]) Database.directRetrieval(query, R.Attraction);
        ObservableList<Attraction> list = FXCollections.observableArrayList(attractions);
        attrTable.setItems(list);
    }

}