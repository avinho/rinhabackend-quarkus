package avinho.records;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransacaoRequest(Double valor, Character tipo, String descricao) {
}
