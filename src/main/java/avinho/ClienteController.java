package avinho;

import avinho.models.Cliente;
import avinho.models.Transacao;
import avinho.records.ExtratoResponse;
import avinho.records.SaldoCliente;
import avinho.records.TransacaoRequest;
import avinho.records.TransacaoResponse;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    @POST
    @Path("/{id}/transacoes")
    @Transactional
    public RestResponse<TransacaoResponse> transacao(Long id, TransacaoRequest transacao) {
        if (id < 0 || id > 5) return RestResponse.status(404);

        if (!validaTransacao(transacao)) return RestResponse.status(422);

        int valor = Integer.parseInt(transacao.valor());

        Cliente cliente = Cliente.findById(id);

        switch (transacao.tipo()) {
            case "c" -> {
                if (valor < 0 || valor > cliente.limite) {
                    return RestResponse.status(422);
                }
                cliente.saldo += valor;
            }
            case "d" -> {
                int novoSaldo = cliente.saldo - valor;
                if (novoSaldo < -cliente.limite) {
                    return RestResponse.status(422);
                }
                cliente.saldo = novoSaldo;
            }
            default -> RestResponse.status(422);
        }

        cliente.persist();

        Transacao newTransacao = new Transacao();
        newTransacao.valor = valor;
        newTransacao.tipo = transacao.tipo();
        newTransacao.descricao = transacao.descricao();
        newTransacao.realizada_em = LocalDateTime.now();
        newTransacao.cliente_id = id;

        newTransacao.persist();

        TransacaoResponse saldo = new TransacaoResponse(cliente.limite, cliente.saldo);

        return RestResponse.ok(saldo);
    }

    @GET
    @Path("/{id}/extrato")
    public RestResponse<ExtratoResponse> extrato(Long id) {
        if (id < 0 || id > 5) {
            return RestResponse.status(404);
        }

        Cliente cliente = Cliente.findById(id);

        SaldoCliente saldo = new SaldoCliente(cliente.saldo, LocalDateTime.now(), cliente.limite);

        List<Transacao> transacoes = Transacao.findLast10(id);

        return RestResponse.ok(new ExtratoResponse(saldo, transacoes));
    }

    public static boolean validaTransacao(TransacaoRequest data) {
        return data.descricao() != null
                && data.valor() != null
                && data.tipo() != null
                && !data.descricao().isEmpty()
                && data.descricao().length() <= 10
                && !data.valor().contains(".")
                && data.tipo().length() == 1
                && ("c".equals(data.tipo()) || "d".equals(data.tipo()));
    }
}
