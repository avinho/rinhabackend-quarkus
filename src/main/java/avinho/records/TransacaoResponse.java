package avinho.records;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransacaoResponse(Integer limite, Integer saldo) {
}
