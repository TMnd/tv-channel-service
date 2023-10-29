package pt.amaral.models;

public class Movie extends Show{

    private String path;

    public Movie(String name, Long timeAsMicroseconds, String type) {
        super(name, timeAsMicroseconds, type);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
