package pt.amaral.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "cat_config")
public class Configuration extends CustomPanacheEntity {

    @Id
    private String name;
    private String configuration;

    public Configuration() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Map<String, String> getMapConfigurations() {
        Map<String, String> result = new HashMap<>();

        List<Configuration> configurations = getAllFromTable("cat_config", Configuration.class);

        for (Configuration config : configurations) {
            result.put(config.getName(), config.getConfiguration());
        }

        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
