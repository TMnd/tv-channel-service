package pt.amaral;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pt.amaral.models.ConfigurationType;
import pt.amaral.models.entities.Configuration;

import java.util.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

    @Inject
    AppConfiguration appConfigurations;

    private static final Logger LOGGER = Logger.getLogger("ListenerBean");

    private void setConfigurations() {
        Configuration configuration = new Configuration();
        Configuration embyToken = configuration.findById(ConfigurationType.EMBY_TOKEN.toString());
        Configuration embyUserUUID = configuration.findById(ConfigurationType.EMBY_USER_UUID.toString());
        Configuration embyServerHost = configuration.findById(ConfigurationType.EMBY_SERVER_URL.toString());
        Configuration vlcAddress = configuration.findById(ConfigurationType.VLC_ADDRESS.toString());
        Configuration vlcPort = configuration.findById(ConfigurationType.VLC_PORT.toString());
        Configuration vlcPassword = configuration.findById(ConfigurationType.VLC_PASSWORD.toString());

        appConfigurations.setEmbyServerApiToken(embyToken.getConfiguration());
        appConfigurations.setEmbyServerDefaultUserUUID(embyUserUUID.getConfiguration());
        appConfigurations.setEmbyServerHost(embyServerHost.getConfiguration());
        appConfigurations.setVlcAddress(vlcAddress.getConfiguration());
        appConfigurations.setVlcPort(Integer.valueOf(vlcPort.getConfiguration()));
        appConfigurations.setVlcPassword(vlcPassword.getConfiguration());
    }


    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
        setConfigurations();
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}