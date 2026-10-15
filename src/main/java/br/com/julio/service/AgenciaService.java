package br.com.julio.service;

import br.com.julio.exception.AgenciaNaoAtivoOuEncontradaException;
import br.com.julio.modelo.Agencia;
import br.com.julio.modelo.http.AgenciaHttp;
import br.com.julio.modelo.http.SituacaoCadastral;
import br.com.julio.repository.AgenciaRepository;
import br.com.julio.service.http.SituacaoCadastralHttpService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    private AgenciaRepository agenciaRepository;

    AgenciaService(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

//    private List<Agencia> agencias = new ArrayList<>();

    public void cadastrarAgencia(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarAgencia(agencia.getCnpj());
        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
//            agencias.add(agencia);
            agenciaRepository.persist(agencia);
            Log.info("Agencia com o CNPJ " + agencia.getCnpj() + " foi cadastrada.");
        } else {
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " não foi cadastrada.");
            throw new AgenciaNaoAtivoOuEncontradaException();
        }
    }

    public Agencia buscarPorId(Long id) {
//        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
        return agenciaRepository.findById(id);
    }

    public void deletar(Long id) {
//        agencias.removeIf(ag -> ag.getId().equals(id));
        agenciaRepository.deleteById(id);
        Log.info("A agência com o id " + id + " foi deletada.");
    }

    public void alterar(Agencia agencia) {
//        deletar(agencia.getId());
//        cadastrarAgencia(agencia);
        agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(), agencia.getRazaoSocial(),
                agencia.getCnpj(), agencia.getId());
        Log.info("A agência com o CNPJ " + agencia.getCnpj() + " foi alterada.");
    }
}
