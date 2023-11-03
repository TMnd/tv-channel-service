package pt.amaral.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_config")
public class Configurations extends PanacheEntity {
    public String name;
    public String configuration;



}
