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
import javafx.scene.layout.HBox;
import model.City;
import model.Review;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewAllCitiesPaneView implements Initializable {

    @FXML private TableView cityTable;
    @FXML private TableColumn cityCol;
    @FXML private TableColumn avgRatingCol;
    @FXML private TableColumn countRatingCol;
    @FXML private TableColumn countAttrCol;
    @FXML private ToggleGroup sort;
    private String sqlSort = "cityName ASC";

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cityCol.setCellValueFactory(new PropertyValueFactory<City, String>("cityName"));
        avgRatingCol.setCellValueFactory(new PropertyValueFactory<City, Double>("averageRating"));
        countRatingCol.setCellValueFactory(new PropertyValueFactory<City, Integer>("numberOfReviews"));
        countAttrCol.setCellValueFactory(new PropertyValueFactory<City, Integer>("numberOfAttr"));
        if (Accounts.getCurrentUser().isManager()) {
            TableColumn deleteCol = new TableColumn("Delete");
            deleteCol.setCellFactory(tc -> new DeleteCell<City>());
            cityTable.getColumns().add(deleteCol);
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
                boolean result = UIAlert.show("Delete City", "Are you sure you want to delete this city?", Alert.AlertType.WARNING, true, true);
                if (result) {
                    ObservableList<City> cities = (ObservableList<City>) getTableView().getItems();
                    City city = cities.get(getTableRow().getIndex());
                    int id = city.getCityId();
                    Database.delete(String.format("DELETE FROM ReviewableEntity WHERE entityID='%s';", id));
                    if (SearchSession.getCityId() == id) {
                        SearchSession.setCityId(1);
                    }
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

    private void setDoubleClickListener() {
        cityTable.setRowFactory( tv -> {
            TableRow<City> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    City city = row.getItem();
                    SearchSession.setCityId(city.getCityId());
                    URL path = getClass().getResource("fxml/CitySearchPane.fxml");
                    Controller.replacePane(cityTable, path);
                }
            });
            return row ;
        });
    }

    /**
     * Changes the sort when a new toggle is selected.
     */
    private void setToggleListener() {
        sort.selectedToggleProperty().addListener(change -> {
                if (sort.getSelectedToggle() != null) {
                    sqlSort = (String) sort.getSelectedToggle().getUserData();
                    populateTable();
                }

            });
    }


    @FXML
    public void populateTable() {
        String query = String.format("SELECT cityID, cityName, avgRating, countRating, COUNT(attrId) AS countAttr " +
                "from nonpendingAttr RIGHT OUTER JOIN " +
                "    (SELECT cityID, cityName, AVG(rating) AS avgRating, COUNT(rating) AS countRating " +
                "     FROM (nonpendingCities JOIN review on review.entityID=cityID)\n" +
                "     group by cityID, cityName) " +
                "    AS firstjoin ON cityLocatedIn=cityID " +
                "group by cityID " +
                "ORDER BY " + sqlSort + ";");
        City[] cities = (City[]) Database.directRetrieval(query, R.City);
        ObservableList<City> list = FXCollections.observableArrayList(cities);
        cityTable.setItems(list);

    }

}
