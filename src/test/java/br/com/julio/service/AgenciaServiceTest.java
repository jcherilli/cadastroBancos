package br.com.julio.service;

import br.com.julio.exception.AgenciaNaoAtivoOuEncontradaException;
import br.com.julio.modelo.Agencia;
import br.com.julio.modelo.Endereco;
import br.com.julio.modelo.http.AgenciaHttp;
import br.com.julio.modelo.http.SituacaoCadastral;
import br.com.julio.repository.AgenciaRepository;
import br.com.julio.service.http.SituacaoCadastralHttpService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class AgenciaServiceTest {

    @InjectMock
    private AgenciaRepository agenciaRepository;

    @InjectMock
    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @Inject
    private AgenciaService agenciaService;

    @Test
    public void NaoDeveCadastrarAgencia() {
        Agencia agencia = criarAgenciaVazia();
        Mockito.when(situacaoCadastralHttpService.buscarAgencia("123")).thenReturn(null);

        Assertions.assertThrows(AgenciaNaoAtivoOuEncontradaException.class, () -> agenciaService.cadastrarAgencia(agencia));

        Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
    }

    @Test
    public void DeveCadastrarAgencia() {
        Agencia agencia = criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarAgencia("123")).thenReturn(criarAgenciaHttp());

        agenciaService.cadastrarAgencia(agencia);

        Mockito.verify(agenciaRepository).persist(agencia);
    }

    private Agencia criarAgenciaVazia() {
        Endereco endereco = new Endereco(1,"","","",1);
        Agencia agencia = new Agencia(1L, "", "", "", endereco);
        return agencia;
    }

    private Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "Quadra", "Teste", "Teste", 1);
        return new Agencia(1L, "Agencia Teste", "Razao Agencia Teste", "123", endereco);
    }


    private AgenciaHttp criarAgenciaHttp() {
        return new AgenciaHttp("Agencia Teste", "Razao Agencia Teste", "123", SituacaoCadastral.ATIVO);
    }

}
