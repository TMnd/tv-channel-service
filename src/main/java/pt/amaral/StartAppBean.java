package pt.amaral;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.math.NumberUtils;
import pt.amaral.models.AppConfiguration;
import pt.amaral.models.ConfigurationType;
import pt.amaral.models.entities.Configuration;

import java.util.List;

@ApplicationScoped
public class StartAppBean {

    @Inject
    EntityManager entityManager;

    private void setConfigurations() {

        final String configuration_query = "SELECT * from cat_config";

        Log.info("Set configurations");

        List<Configuration> configurations = entityManager.createNativeQuery(configuration_query, Configuration.class).getResultList();
        AppConfiguration appConfiguration = AppConfiguration.getInstance();

        for (Configuration configuration : configurations) {
            ConfigurationType name = ConfigurationType.getEnumByName(configuration.getName());
            String config = configuration.getConfiguration();

            switch (name) {
                case EMBY_TOKEN:
                    appConfiguration.setEmbyServerApiToken(config);
                    break;
                case EMBY_SERVER_URL:
                    appConfiguration.setEmbyServerHost(config);
                    break;
                case EMBY_USER_UUID:
                    appConfiguration.setEmbyServerDefaultUserUUID(config);
                    break;
                case VLC_ADDRESS:
                    appConfiguration.setVlcAddress(config);
                    break;
                case VLC_PORT:
                    appConfiguration.setVlcPort(NumberUtils.toInt(config));
                    break;
                case VLC_PASSWORD:
                    appConfiguration.setVlcPassword(config);
                    break;
                default:
                    Log.info("Base configuration not found!");
            }
        }

        System.out.println(appConfiguration.toString());
    }

    @Startup
    void init() {
        setConfigurations();
    }
}