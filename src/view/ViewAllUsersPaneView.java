package view;

import controller.Accounts;
import controller.Controller;
import controller.Database;
import controller.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import model.User;

import java.net.URL;
import java.sql.Date;

public class ViewAllUsersPaneView {

    @FXML private Hyperlink back;
    @FXML private TextField userSearch;
    @FXML private TableView userTable;
    @FXML private TableColumn emailCol;
    @FXML private TableColumn dateJoinedCol;
    @FXML private TableColumn userClassCol;
    @FXML private TableColumn suspendedCol;
    @FXML private TableColumn deleteCol;

    String search = "";


    @FXML
    public void initialize() {
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        dateJoinedCol.setCellValueFactory(new PropertyValueFactory<User, Date>("dateJoined"));
        userClassCol.setCellValueFactory(new PropertyValueFactory<User, String>("userClass"));
        suspendedCol.setCellValueFactory(new PropertyValueFactory<User, String>("suspendedName"));
        deleteCol.setCellFactory(tc -> new ManageCell<User>());
        deleteCol.setMinWidth(200);
        deleteCol.setMaxWidth(200);
        userSearch.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });
        populateTable("");
        userTable.requestFocus();
    }

    public void populateTable(String searchEmail) {
        search = searchEmail;
        String query = "SELECT email, dateJoined, isManager, isSuspended FROM user";
        if (!searchEmail.equals("")) {
            query += String.format(" WHERE email='%s'", searchEmail);
        }
        query += ";";
        User[] users = (User[]) Database.directRetrieval(query, R.User);
        ObservableList<User> list = FXCollections.observableArrayList(users);
        userTable.setItems(list);
        userTable.refresh();
    }

    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
        Controller.replacePane(back, path);
    }

    @FXML
    public void handleSearch() {
        populateTable(userSearch.getText());
    }

    @FXML
    public void handleReset() {
        populateTable("");
        userSearch.clear();
    }

    @FXML
    public void handleNewUser() {
        URL path = getClass().getResource("fxml/AddUserPane.fxml");
        Controller.replacePane(back, path);
    }

    public class ManageCell<T> extends TableCell<T, Void> {

        private final Hyperlink deleteLink = new Hyperlink("Delete");
        private Hyperlink suspendLink;
        private Hyperlink promoteLink;

        public ManageCell() {
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) { setGraphic(null); return; }

            User user = (User) getTableView().getItems().get(getTableRow().getIndex());
            deleteLink.setOnAction(evt -> {
                if (Database.getCount("SELECT COUNT(email) AS result FROM user WHERE (isManager = 1)") <= 1) {
                    UIAlert.show("Cannot Delete Manager", "At least one manager must exist at all times.",
                            Alert.AlertType.ERROR);
                } else {
                    if (user.equals(Accounts.getCurrentUser())) {
                        HomeView.deleteCurrentUser();
                        return;
                    }
                    // remove row item from tableview
                    boolean result = UIAlert.show("Confirm Delete User", "Are you sure "
                            + "you want to delete this user permanently?", Alert.AlertType.WARNING, true, true);
                    if (result) {
                        if (user.delete()) {
                            getTableView().getItems().remove(getTableRow().getIndex());
                        }
                    }
                }
            });
            suspendLink = new Hyperlink(user.isSuspended() ? "Activate" : "Suspend");
            suspendLink.setOnAction(e -> {
                if (user.isManager()) {
                    UIAlert.show("Cannot Suspend a Manager", "In order to suspend this user, demote the user first.", Alert.AlertType.INFORMATION);
                return;
                }
                boolean result = UIAlert.show("Confirm Action", "Are you sure you want to " + suspendLink.getText().toLowerCase() + " this user?", Alert.AlertType.WARNING, true);
                if (result) { return; }
                int newValue = user.isSuspended() ? 0 : 1;
                if (Database.update(String.format("UPDATE User set isSuspended=%d WHERE email='%s';", newValue, user.getEmail())).toBoolean()) {
                    user.setSuspended(user.isSuspended());
                    //suspendLink.setText(user.isSuspended() ? "Renew" : "Suspend");
                    populateTable(search);
                }
            });
            promoteLink = new Hyperlink(user.isManager() ? "Demote" : "Promote");
            promoteLink.setOnAction(e -> {
                if (user.isSuspended()) {
                    UIAlert.show("Cannot Promote a Suspended User", "In order to promote this user, activate the user first.", Alert.AlertType.INFORMATION);
                    return;
                }
                boolean result = UIAlert.show("Confirm Action", "Are you sure you want to " + promoteLink.getText().toLowerCase() + " this user?", Alert.AlertType.WARNING, true);
                if (result) { return; }
                int newValue = user.isManager() ? 0 : 1;
                if (Database.update(String.format("UPDATE User set isManager=%d WHERE email='%s';", newValue, user.getEmail())).toBoolean()) {
                    user.setManager(!user.isManager());
                    //promoteLink.setText(user.isManager() ? "Demote" : "Promote");
                    populateTable(search);
                }
            });

            HBox box = new HBox(promoteLink, suspendLink, deleteLink);
            box.setAlignment(Pos.CENTER);
            if (user.equals(Accounts.getCurrentUser())) {
                box.getChildren().setAll(new Label("Me!"), deleteLink);
            }
            setGraphic(empty ? null : box);
        }

    }
}
