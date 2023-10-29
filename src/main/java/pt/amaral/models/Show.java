package pt.amaral.models;

public class Show {
    private String name;
    private Long timeAsMicroseconds;
    private String type;

    public Show(String name, Long timeAsMicroseconds, String type) {
        this.name = name;
        this.timeAsMicroseconds = timeAsMicroseconds;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeAsMicroseconds() {
        return timeAsMicroseconds;
    }

    public void setTimeAsMicroseconds(Long timeAsMicroseconds) {
        this.timeAsMicroseconds = timeAsMicroseconds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
