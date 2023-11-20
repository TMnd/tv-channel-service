package pt.amaral.models;

import java.time.ZonedDateTime;

public class Show {
    private String id;
    private String name;
    private Long timeAsMicroseconds;
    private String type;
    private Boolean hasPlayed;
    private ZonedDateTime lastTimeAir;

    public Show(String id, String name, Long timeAsMicroseconds, String type) {
        this.id = id;
        this.name = name;
        this.timeAsMicroseconds = timeAsMicroseconds;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getHasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(Boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public ZonedDateTime getLastTimeAir() {
        return lastTimeAir;
    }

    public void setLastTimeAir(ZonedDateTime lastTimeAir) {
        this.lastTimeAir = lastTimeAir;
    }
}
