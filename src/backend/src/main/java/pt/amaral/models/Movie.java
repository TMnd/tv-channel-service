package pt.amaral.models;

public class Movie extends Show{

    private String path;

    public Movie(String id, String name, Long timeAsMicroseconds, String type) {
        super(id, name, timeAsMicroseconds, type);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
