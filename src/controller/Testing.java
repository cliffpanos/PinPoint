package controller;

import javafx.scene.image.ImageView;
import resources.AVAsset;
import resources.Resources;

import javax.xml.crypto.Data;

/**
 * Created by Cliff on 7/5/17.
 *
 * USED FOR TESTING
 * SHOULD NOT BE USED IN DISTRIBUTED APP
 */
public class Testing {

    public static boolean debug = false;

    public static void testSQLQuery() {

        String query = SQLQuery
                .select(Attributed.ALL)
                .from(R.City, R.Category, R.UnderCategory)
                .where(Attributed.name, SQLQuery.Condition.EQUAL, Attributed.email).prepared();

        String other = SQLQuery
                .select(Attributed.email, Attributed.name)
                .from(R.Review, R.ReviewableEntity)
                .where(Attributed.name, SQLQuery.Condition.EQUAL, "Cliff").prepared();

        String three = SQLQuery
                .select(Attributed.ALL)
                .from(R.Category).prepared();

    }

    public static void testInsert() {
        //Database.insert("cpanos@yahoo.com");
    }

    public static void testAssets() {
        //Test of resources class and image getting
        ImageView imageView = Resources.imageViewFor(AVAsset.PinPointAppIcon, 100);
        Resources.playAudio(AVAsset.Done);
    }

    public static void print(String debugMessage) {
        if (!Testing.debug) {
            return;
        }

        System.out.println(debugMessage);
    }
}
