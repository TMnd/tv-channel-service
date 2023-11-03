package pt.amaral;

import jakarta.inject.Singleton;

@Singleton
public class AppConfiguration {
    private String embyServerHost;
    private String embyServerDefaultUserUUID;
    private String embyServerApiToken;
    private String vlcAddress;
    private Integer vlcPort;
    private String vlcPassword;

    public AppConfiguration() {}

    public String getEmbyServerHost() {
        return embyServerHost;
    }

    public void setEmbyServerHost(String embyServerHost) {
        this.embyServerHost = embyServerHost;
    }

    public String getEmbyServerDefaultUserUUID() {
        return embyServerDefaultUserUUID;
    }

    public void setEmbyServerDefaultUserUUID(String embyServerDefaultUserUUID) {
        this.embyServerDefaultUserUUID = embyServerDefaultUserUUID;
    }

    public String getEmbyServerApiToken() {
        return embyServerApiToken;
    }

    public void setEmbyServerApiToken(String embyServerApiToken) {
        this.embyServerApiToken = embyServerApiToken;
    }

    public String getVlcAddress() {
        return vlcAddress;
    }

    public void setVlcAddress(String vlcAddress) {
        this.vlcAddress = vlcAddress;
    }

    public Integer getVlcPort() {
        return vlcPort;
    }

    public void setVlcPort(Integer vlcPort) {
        this.vlcPort = vlcPort;
    }

    public String getVlcPassword() {
        return vlcPassword;
    }

    public void setVlcPassword(String vlcPassword) {
        this.vlcPassword = vlcPassword;
    }
}
