package br.com.julio.service;

import br.com.julio.exception.AgenciaNaoAtivoOuEncontradaException;
import br.com.julio.modelo.Agencia;
import br.com.julio.modelo.http.AgenciaHttp;
import br.com.julio.modelo.http.SituacaoCadastral;
import br.com.julio.repository.AgenciaRepository;
import br.com.julio.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    private AgenciaRepository agenciaRepository;
    private MeterRegistry meterRegistry;

    AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
    }

//    private List<Agencia> agencias = new ArrayList<>();

    public void cadastrarAgencia(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarAgencia(agencia.getCnpj());
        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
//            agencias.add(agencia);
            agenciaRepository.persist(agencia);
            meterRegistry.counter("agencia_cadastrada").increment();
            Log.info("Agencia com o CNPJ " + agencia.getCnpj() + " foi cadastrada.");
        } else {
            meterRegistry.counter("agencia_nao_cadastrada").increment();
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " não foi cadastrada.");
            throw new AgenciaNaoAtivoOuEncontradaException();
        }
    }

    public Agencia buscarPorId(Long id) {
//        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
        meterRegistry.counter("agencia_buscada").increment();
        return agenciaRepository.findById(id);
    }

    public void deletar(Long id) {
//        agencias.removeIf(ag -> ag.getId().equals(id));
        agenciaRepository.deleteById(id);
        meterRegistry.counter("agencia_deletada").increment();
        Log.info("A agência com o id " + id + " foi deletada.");
    }

    public void alterar(Agencia agencia) {
//        deletar(agencia.getId());
//        cadastrarAgencia(agencia);
        agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(), agencia.getRazaoSocial(),
                agencia.getCnpj(), agencia.getId());
        meterRegistry.counter("agencia_alterada").increment();
        Log.info("A agência com o CNPJ " + agencia.getCnpj() + " foi alterada.");
    }
}
