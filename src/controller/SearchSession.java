package controller;

public class SearchSession {

    private static int cityId = 1;
    private static int attractionId = 9;

    public static int getCityId() {
        return cityId;
    }

    public static void setCityId(int id) {
        cityId = id;
    }

    public static int getAttractionId() {
        return attractionId;
    }

    public static void setAttractionId(int id) {
        attractionId = id;
    }
}
