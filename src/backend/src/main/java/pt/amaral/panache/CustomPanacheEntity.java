package pt.amaral.panache;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CustomPanacheEntity extends PanacheEntityBase {

    private static final String GET_ALL_FROM_TABLE = "SELECT * FROM %s";

    public CustomPanacheEntity() {}

    public static <T extends PanacheEntityBase> List<T> getAllFromTable(String tableName, Class<T> clazz) {
        EntityManager entityManager = getEntityManager();

        String query = String.format(GET_ALL_FROM_TABLE, tableName);

        return entityManager.createNativeQuery(query, clazz).getResultList();
    }
}
