package avinho.records;

import avinho.models.Transacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record ExtratoResponse(
        SaldoCliente saldo,
        @JsonIgnoreProperties({"id", "cliente_id"}) List<Transacao> ultimas_transacoes) {
}
