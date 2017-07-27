package view;

import controller.Accounts;
import controller.Controller;
import controller.Database;
import controller.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Category;
import model.Review;
import model.User;
import java.net.URL;


public class ViewAllCategoriesPaneView {

    @FXML private Hyperlink back;
    @FXML private TableView tableView;
    @FXML private TableColumn categoryCol;
    @FXML private TableColumn attractionsCol;
    @FXML private TableColumn manageCol;
    @FXML private ToggleGroup sort;
    private String sqlSort = "catName ASC";


    @FXML
    public void initialize() {
        categoryCol.setCellValueFactory(new PropertyValueFactory<Category, String>("catName"));
        attractionsCol.setCellValueFactory(new PropertyValueFactory<Category, Integer>("numberOfAttractions"));
        manageCol.setCellFactory(evt -> new EditCell<>());
        manageCol.setMaxWidth(110);
        manageCol.setMinWidth(110);
        populateTable();
        sort.selectedToggleProperty().addListener(change -> {
            if (sort.getSelectedToggle() != null) {
                sqlSort = (String) sort.getSelectedToggle().getUserData();
                populateTable();
            }
        });
    }

    @FXML
    public void populateTable() {
        String query = "SELECT Category.catName, COUNT(FirstJoin.attrID) AS countAttr FROM Category LEFT OUTER JOIN "
                + "(SELECT * FROM Undercategory NATURAL JOIN nonpendingAttr) AS FirstJoin "
                + "ON Category.catName=FirstJoin.catName GROUP BY catName "
                + "ORDER BY " + sqlSort + ";";
        Category[] categories = (Category[]) Database.directRetrieval(query, R.Category);
        ObservableList<Category> list = FXCollections.observableArrayList(categories);
        tableView.setItems(list);
    }


    @FXML
    public void handleNewCategory() {
        URL path = getClass().getResource("fxml/AddCategoryPane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
        Controller.replacePane(back, path);
    }

    public class EditCell<T> extends TableCell<T, Void> {

        private final Hyperlink editLink;
        private final Hyperlink deleteLink;

        public EditCell() {
            editLink = new Hyperlink("Edit");
            editLink.setOnAction(evt -> {
                // edit row item from tableview
                ObservableList<Category> reviews = (ObservableList<Category>) getTableView().getItems();
                Category category = reviews.get(getTableRow().getIndex());
                URL path = getClass().getResource("fxml/AddCategoryPane.fxml");
                AddCategoryPaneView controller = Controller.replacePane(this, path);
                if (Accounts.getCurrentUser().isSuspended()) { return; }
                controller.setCategory(category);
                controller.setIsEditing(true);
                controller.getBackButton().setOnAction(e -> {
                    URL backPath = getClass().getResource("fxml/MyReviewsPane.fxml");
                    Controller.replacePane(controller.getBackButton(), backPath);
                });
            });
            deleteLink = new Hyperlink("Delete");
            deleteLink.setOnAction(evt -> {
                boolean result = UIAlert.show("Confirm Delete Category", "Are you sure "
                        + "you want to delete this category permanently?", Alert.AlertType.WARNING, true, true);
                if (result) {
                    ObservableList<Category> categories = (ObservableList<Category>) getTableView().getItems();
                    Category category = categories.get(getTableRow().getIndex());
                    if (category.delete()) {
                        getTableView().getItems().remove(getTableRow().getIndex());
                    } else {
                        UIAlert.show("Category Not Deleted", "The category could not be deleted; an error occurred.", Alert.AlertType.ERROR);
                    }
                }
            });

        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            HBox box = new HBox(editLink, deleteLink);
            setGraphic(empty ? null : box);
        }

    }
}
