package avinho.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Cacheable
@RegisterForReflection
public class Transacao extends PanacheEntity {
    public Integer valor;
    public char tipo;
    public String descricao;
    public LocalDateTime realizada_em;
    public Long cliente_id;

    public Transacao() {
    }

    public Transacao(Integer valor, char tipo, String descricao, LocalDateTime realizada_em, Long cliente_id) {
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.realizada_em = realizada_em;
        this.cliente_id = cliente_id;
    }

    public static List<Transacao> findLast10(Long id) {
        return find("cliente_id = ?1 order by realizada_em desc limit 10", id).list();
    }
}
