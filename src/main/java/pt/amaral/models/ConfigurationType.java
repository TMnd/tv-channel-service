package pt.amaral.models;

import org.apache.commons.lang3.StringUtils;

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

    public static ConfigurationType getEnumByName(String name) {
        for (ConfigurationType configurationType : values()) {
            if(StringUtils.equals(configurationType.configurationName, name)) {
                return configurationType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.configurationName;
    }
}
