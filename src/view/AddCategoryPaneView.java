package view;

import controller.Controller;
import controller.Database;
import controller.SQLResult;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Category;

import java.net.URL;

public class AddCategoryPaneView {

    @FXML private Hyperlink back;
    @FXML private Button submit;
    @FXML private TextField categoryField;
    @FXML private Label title;
    private boolean isEditing = false;
    private String catName;


    public void setCategory(Category category) {
        catName = category.getCatName();
        categoryField.setText(category.getCatName());
        title.setText("Edit Category " + category.getCatName());
    }

    public void setIsEditing(boolean editable) {
        isEditing = editable;
    }

    public Hyperlink getBackButton() {
        return back;
    }

    @FXML
    public void handleSubmit() {
        if (categoryField.getText().isEmpty() || categoryField.getText().length() < 3) {
            UIAlert.show("Enter Category Name", "Please enter a name of at least 3 characters.", Alert.AlertType.CONFIRMATION);
            return;
        }
        if (isEditing) {
            submitEdit();
            return;
        }
        SQLResult result = Database.insert(String.format("INSERT INTO Category VALUES ('%s');", categoryField.getText()));
        considerResult(result);
    }

    private void submitEdit() {
        String query = String.format("UPDATE Category SET catName='%s' "
                        + "WHERE catName='%s';", categoryField.getText(), catName);
        considerResult(Database.update(query));
    }

    private void considerResult(SQLResult result) {
        if (result.toBoolean()) {
            UIAlert.show("Category " + (isEditing ? "Updated!" : "Created!"), "The category " + (isEditing ? "update" : "creation") + " was successful!", Alert.AlertType.INFORMATION);
            URL path = getClass().getResource("fxml/ViewAllCategoriesPane.fxml");
            Controller.replacePane(back, path);
        } else if (result.equals(SQLResult.DuplicatePK)){
            UIAlert.show("Category Already Exists", "The " + categoryField.getText() + " category already exists.\nPlease choose another.", Alert.AlertType.ERROR);
        } else {
            UIAlert.show("Category Creation Error", "Sorry, but there was an error while trying to create the category.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleBack() {
        URL path = getClass().getResource("fxml/ManagerProfilePane.fxml");
        Controller.replacePane(back, path);
    }
}
