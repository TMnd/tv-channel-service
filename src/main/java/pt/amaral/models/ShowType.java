package pt.amaral.models;

public enum ShowType {
    MOVIES("Movie", "3230"),
    SERIES("Series","4042"),
    DOCUMENTARY("Documentary","5487");

    private String name;
    private String catrgoryID;

    ShowType(String name, String categoryId) {
        this.name = name;
        this.catrgoryID = categoryId;
    }

    public String getCatrgoryID(){
        return this.catrgoryID;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
