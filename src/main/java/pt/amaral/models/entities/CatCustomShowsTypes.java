package pt.amaral.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cat_custom_shows_types")
public class CatCustomShowsTypes extends PanacheEntityBase {
    @Id
    private String name;
    @Column(name="custom_type")
    private String customType;

    public CatCustomShowsTypes() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }
}
