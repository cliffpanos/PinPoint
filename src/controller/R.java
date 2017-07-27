package controller;

import model.Attraction;
import model.Relation;

import java.util.ArrayList;

/**
 * Created by Cliff on 7/5/17.
 *
 * An enumeration of all table names (relations) present in the PinPoint database
 * R is shorthand for relation
 */
public enum R {

    Attraction,
    ContactInfo,
    OperationHours,
    UnderCategory,
    Category,

    User,
    City,

    Review,
    ReviewableEntity;

    /**
     * Create a specific instance of Relation using an R type as its specifier
     * @return an instance of a subclass of Relation (i.e. R.User returns a new User() )
     */
    public Relation toObject() {
        switch (this) {
            case Attraction:
                return new model.Attraction();
            case ContactInfo:
                return new model.ContactInfo();
            case OperationHours:
                return new model.OperationHours();
            case UnderCategory:
                return new model.UnderCategory();
            case User:
                return new model.User();
            case City:
                return new model.City();
            case Review:
                return new model.Review();
            case Category:
                return new model.Category();
            case ReviewableEntity:
                Testing.print("ReviewableEntity cannot be instantiated!");
                break;
        }
        return null;
    }

    /**
     * Create an array of a specified Relation type using an R as the specifier
     * and an ArrayList as the data container. This method returns an array of a
     * subclass of Relation in order to avoid ClassCastExceptions later.
     * @param list the ArrayList that contains instances of Relation
     * @return an array of a specific type of Relation (e.g. User[], City[], ...)
     */
    public Relation[] toArray(ArrayList<Relation> list) {

        switch (this) {
            case Attraction:
                model.Attraction[] attractions = new model.Attraction[list.size()];
                attractions = list.toArray(attractions);
                return attractions;
            case ContactInfo:
                model.ContactInfo[] contactInfos = new model.ContactInfo[list.size()];
                contactInfos = list.toArray(contactInfos);
                return contactInfos;
            case OperationHours:
                model.OperationHours[] operationHours = new model.OperationHours[list.size()];
                operationHours = list.toArray(operationHours);
                return operationHours;
            case UnderCategory:
                model.UnderCategory[] underCategories = new model.UnderCategory[list.size()];
                underCategories = list.toArray(underCategories);
                return underCategories;
            case User:
                model.User[] users = new model.User[list.size()];
                users = list.toArray(users);
                return users;
            case City:
                model.City[] cities = new model.City[list.size()];
                cities = list.toArray(cities);
                return cities;
            case Review:
                model.Review[] reviews = new model.Review[list.size()];
                reviews = list.toArray(reviews);
                return reviews;
            case Category:
                model.Category[] categories = new model.Category[list.size()];
                categories = list.toArray(categories);
                return categories;
            case ReviewableEntity:
                Testing.print("ReviewableEntity cannot be instantiated!");
                break;
        }
        return null;
    }
}