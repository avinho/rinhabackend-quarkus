package avinho;

import avinho.models.Cliente;
import avinho.models.Transacao;
import avinho.records.ExtratoResponse;
import avinho.records.SaldoCliente;
import avinho.records.TransacaoRequest;
import avinho.records.TransacaoResponse;
import io.smallrye.common.annotation.RunOnVirtualThread;
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
    @RunOnVirtualThread
    public RestResponse<TransacaoResponse> transacao(Long id, TransacaoRequest transacao) {
        if (id < 0 || id > 5) return RestResponse.status(404);

        if (!validaTransacao(transacao)) return RestResponse.status(422);

        Cliente cliente = Cliente.findById(id);
        int valor = transacao.valor().intValue();
        int limite = cliente.limite;

        if (cliente.saldo <= -limite || valor > limite || (cliente.saldo - valor) < -limite) {
            return RestResponse.status(422);
        }

        switch (transacao.tipo()) {
            case 'c' -> {
                if (valor < 0) return RestResponse.status(422);
                cliente.saldo += valor;
            }
            case 'd' -> {
                cliente.saldo -= valor;;
            }
            default -> RestResponse.status(422);
        }

        Transacao newTransacao = new Transacao(valor, transacao.tipo(), transacao.descricao(), LocalDateTime.now(), id);
        cliente.persist();
        newTransacao.persist();

        return RestResponse.ok(new TransacaoResponse(limite, cliente.saldo));
    }

    @GET
    @Path("/{id}/extrato")
    @RunOnVirtualThread
    public RestResponse<ExtratoResponse> extrato(Long id) {
        if (id < 0 || id > 5) {
            return RestResponse.status(404);
        }

        Cliente cliente = Cliente.findById(id);

        SaldoCliente saldo = new SaldoCliente(cliente.saldo, LocalDateTime.now(), cliente.limite);

        List<Transacao> transacoes = Transacao.findLast10(id);

        return RestResponse.ok(new ExtratoResponse(saldo, transacoes));
    }

    private boolean validaTransacao(TransacaoRequest transacao) {
        return transacao.descricao() != null
                && transacao.valor() != null
                && !transacao.descricao().isEmpty()
                && transacao.descricao().length() <= 10
                && validaTipo(transacao.tipo())
                && validaValor(transacao.valor());
    }

    public static boolean validaValor(double valor) {
        return valor % 1 == 0;
    }

    private boolean validaTipo(char type) {
        final char[] VALID_TYPES = {'c', 'd'};
        for (char validType : VALID_TYPES) {
            if (type == validType) {
                return true;
            }
        }
        return false;
    }
}
