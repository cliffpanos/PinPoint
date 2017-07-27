package model;

import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Created by Cliff on 7/4/17.
 *
 * The OperationHours relation
 */
public class OperationHours extends Relation {

    @PrimaryKey @ForeignKey private int attrID;   //attrID & weekday form the PK
    @PrimaryKey private String openDay;
    private String hours;


    public OperationHours createFrom(ResultSet attributes) throws SQLException {

        OperationHours operationHours = new OperationHours();
        operationHours.attrID = (int) Database.propertyFill(attributes, "attrID", Type.INT);
        operationHours.openDay = (String) Database.propertyFill(attributes, "openDay", Type.STRING);
        Time open = (Time) Database.propertyFill(attributes, "openTime", Type.TIME);
        String openTime = open == null ? "Open" : open.toString();
        Time close = (Time) Database.propertyFill(attributes, "closeTime", Type.TIME);
        String closeTime = close == null ? "Close" : close.toString();
        operationHours.hours = openTime + "-" + closeTime;
        return operationHours;
    }

    public String getHours() {
        return hours;
    }
}
