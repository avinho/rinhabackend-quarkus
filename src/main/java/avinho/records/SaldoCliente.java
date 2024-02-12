package avinho.records;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SaldoCliente(Integer total,
                           @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'") LocalDateTime data_extrato,
                           Integer limite) {
}