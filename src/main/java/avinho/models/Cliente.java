package avinho.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

@Entity
@Cacheable
@RegisterForReflection
public class Cliente extends PanacheEntity {
    public Integer limite;
    public Integer saldo;
}
