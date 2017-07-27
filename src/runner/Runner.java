package runner;

import controller.Database;
import controller.Testing;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.FXBuilder;
import view.LoginView;
import view.RootView;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Runner extends Application {

    private static String username = "cs4400";
    private static String password = "abcspinpointcs4400";
    private static String dbName = "PinPoint";
    private static String serverName = "localhost";
    private static int portNumber = 3306;

    public static Parent root = null;


    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene loginScene = LoginView.loginScene;
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("PinPoint Login");
        primaryStage.setWidth(550);
        primaryStage.setHeight(400);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.toFront();
        primaryStage.show();
        LoginView.loginStage = primaryStage;
        //Testing.debug = true;

        try {
            initializeConnection();
        } catch (SQLException sqle) {
            Testing.print(sqle.getMessage());
            System.out.println("No SQL Connection\n");
        }

        //Testing.testInsert();
        //Testing.testSQLQuery();
        //Testing.testAssets();
    }

    /**
     * Initialize the main screen for the application.
     */
    private void initRootLayout(Stage mainScreen) throws Exception {

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void initializeConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);

        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" +
                        serverName +
                        ":" + portNumber +
                        "/" + dbName + "?autoReconnect=true&useSSL=false",
                connectionProps);

        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        Database.setConnection(conn);

        Testing.print("*** Connected to database! *** \n");
    }

    @Override
    public void stop() throws Exception {
        Database.disconnect(this);
    }

}
