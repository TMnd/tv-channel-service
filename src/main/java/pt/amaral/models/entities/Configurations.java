package pt.amaral.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_config")
public class Configurations extends PanacheEntityBase {
    @Id
    public String name;
    public String configuration;
}
