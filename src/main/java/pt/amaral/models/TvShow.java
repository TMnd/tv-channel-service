package pt.amaral.models;

public class TvShow extends Show{

    private String episode;
    private String season;
    private String path;
    private String showName;

    public TvShow(String name, Long timeAsMicroseconds, String type) {
        super(name, timeAsMicroseconds, type);
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "TvShow{" +
                "name='" + super.getName() + '\'' +
                ", time='" + super.getTimeAsMicroseconds() + '\'' +
                ", showName='" + showName + '\'' +
                ", episode='" + episode + '\'' +
                ", season='" + season + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
