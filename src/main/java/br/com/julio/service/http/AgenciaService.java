package br.com.julio.service.http;

import br.com.julio.exception.AgenciaNaoAtivoOuEncontradaException;
import br.com.julio.modelo.Agencia;
import br.com.julio.modelo.http.AgenciaHttp;
import br.com.julio.modelo.http.SituacaoCadastral;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    private List<Agencia> agencias = new ArrayList<>();

    public void cadastrarAgencia(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarAgencia(agencia.getCnpj());
        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            agencias.add(agencia);
        } else {
            throw new AgenciaNaoAtivoOuEncontradaException();
        }
    }

    public Agencia buscarPorId(Integer id) {
        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
    }

    public void deletar(Integer id) {
        agencias.removeIf(ag -> ag.getId().equals(id));
    }

    public void alterar(Agencia agencia) {
        deletar(agencia.getId());
        cadastrarAgencia(agencia);
    }
}
