package controller;

/**
 * Created by Cliff on 7/21/17.
 *
 * SQLResult enumerates some common error cases so that when an SQLException is
 * thrown, the error message can be mapped to one of these errors so that other
 * code and methods can check for these specific cases and then handle them in a
 * locally specific manner.
 */
public enum SQLResult {

    Success, DuplicatePK, InvalidFormat, Error, MissingField;

    /**
     * Convert the SQLResult to a boolean; success or failure
     * @return true if the SQLResult is Success
     */
    public boolean toBoolean() {
        return this == Success;
    }

}
