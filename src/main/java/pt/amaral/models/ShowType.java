package pt.amaral.models;

public enum ShowType {
    MOVIES("Movie"),
    SERIES("Series");

    private String name;

    ShowType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
