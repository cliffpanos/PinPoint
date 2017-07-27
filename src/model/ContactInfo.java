package model;

import controller.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cliff on 7/4/17.
 */
public class ContactInfo extends Relation {

    @PrimaryKey @ForeignKey private int attrID;
    @PrimaryKey private String contactMethod;
    @PrimaryKey private String contactValue;
    //All three of these attributes form the PK together

    public ContactInfo() {
    }

    public ContactInfo createFrom(ResultSet attributes) throws SQLException {

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.attrID = (int) Database.propertyFill(attributes, "attrID", Type.INT);
        contactInfo.contactMethod = (String) Database.propertyFill(attributes, "contactMethod", Type.STRING);
        contactInfo.contactValue = (String) Database.propertyFill(attributes, "contactValue", Type.STRING);
        //attraction.contact = /*(contactMethod == null ? "" : (contactMethod + ": ")) + */ contactValue;
        return contactInfo;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public String getContactValue() {
        return contactValue;
    }
}
