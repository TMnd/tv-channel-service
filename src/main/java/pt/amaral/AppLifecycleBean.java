package pt.amaral;

import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import pt.amaral.models.AppConfiguration;
import pt.amaral.models.ConfigurationType;
import pt.amaral.models.entities.Configuration;

import java.util.Map;
import java.util.logging.Logger;

@Startup
@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = Logger.getLogger("ListenerBean");

    private void setConfigurations() {

        AppConfiguration appConfiguration = AppConfiguration.getInstance();

        Configuration configuration = new Configuration();

        Map<String, String> mapConfigurations = configuration.getMapConfigurations();

        appConfiguration.setEmbyServerApiToken(mapConfigurations.get(ConfigurationType.EMBY_TOKEN.toString()));
        appConfiguration.setEmbyServerDefaultUserUUID(mapConfigurations.get(ConfigurationType.EMBY_USER_UUID.toString()));
        appConfiguration.setEmbyServerHost(mapConfigurations.get(ConfigurationType.EMBY_SERVER_URL.toString()));
        appConfiguration.setVlcAddress(mapConfigurations.get(ConfigurationType.VLC_ADDRESS.toString()));
        appConfiguration.setVlcPort(Integer.valueOf(mapConfigurations.get(ConfigurationType.VLC_PORT.toString())));
        appConfiguration.setVlcPassword(mapConfigurations.get(ConfigurationType.VLC_PASSWORD.toString()));

        Log.info("Configurations set: " + appConfiguration.toString());
    }


    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("The application is starting...");
        setConfigurations();
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}