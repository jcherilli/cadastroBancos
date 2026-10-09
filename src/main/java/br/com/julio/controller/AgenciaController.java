package br.com.julio.controller;

import br.com.julio.modelo.Agencia;
import br.com.julio.service.http.AgenciaService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/agencias")
public class AgenciaController {

    private final AgenciaService agenciaService;

    AgenciaController (AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @POST
    @Transactional
    public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        this.agenciaService.cadastrarAgencia(agencia);
        return RestResponse.created(uriInfo.getAbsolutePath());
    }

    @GET
    @Path("{id}")
    public RestResponse<Agencia> pesquisar(Long id) {
        Agencia agencia = this.agenciaService.buscarPorId(id);
        return RestResponse.ok(agencia);
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public RestResponse<Void> deletar(Long id) {
        this.agenciaService.deletar(id);
        return RestResponse.ok();
    }

    @PUT
    @Transactional
    public RestResponse<Agencia> alterar(Agencia agencia) {
        this.agenciaService.alterar(agencia);
        return RestResponse.ok(agencia);
    }
}
