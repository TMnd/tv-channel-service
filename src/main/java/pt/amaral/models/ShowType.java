package pt.amaral.models;

public enum ShowType {
    MOVIES("Movie", "3230"),
    SERIES("Series","4042"),
    DOCUMENTARY("Documentary","5487"),
    ANIMATED_SERIES("Animated Series",""),
    EPISODE("Episode","");

    private String name;
    private String categoryID;

    ShowType(String name, String categoryId) {
        this.name = name;
        this.categoryID = categoryId;
    }

    public String getCategoryID(){
        return this.categoryID;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
