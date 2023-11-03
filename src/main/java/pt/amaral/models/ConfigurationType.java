package pt.amaral.models;

public enum ConfigurationType {
    EMBY_TOKEN("emby-token"),
    EMBY_USER_UUID("emby-user-uuid"),
    EMBY_SERVER_URL("emby-server-url"),
    VLC_ADDRESS("vlc-address"),
    VLC_PORT("vlc-port"),
    VLC_PASSWORD("vlc-password");

    private String configurationName;

    ConfigurationType(String configurationName) {
        this.configurationName = configurationName;
    }

    @Override
    public String toString() {
        return this.configurationName;
    }
}
