package org.aaf.webInterface.rest;

import org.aaf.escolar.CarroDTO;
import org.escolar.enums.PerioddoEnum;
import org.escolar.model.Carro;
import org.escolar.model.ObjetoRota;
import org.escolar.service.AlunoService;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/carros")
@RequestScoped
@Stateful
public class CarroRest {

    @EJB
    private AlunoService alunoService;

    // -----------------------------------------------------------------------
    // GET /carros — lista todos os carros (id, nome, idGetrak)
    // -----------------------------------------------------------------------
    @GET
    @Produces("application/json")
    public Response listarCarros() {
        try {
            List<Carro> carros = alunoService.findAllCarros();
            List<CarroDTO> dtos = new ArrayList<>();
            for (Carro c : carros) {
                dtos.add(new CarroDTO(c.getId(), c.getNome(), c.getIdGetrak()));
            }
            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(dtos)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /carros/{id}/rota/{periodo} — rota ordenada do carro num período
    // Período aceite: MANHA, TARDE, INTEGRAL
    // -----------------------------------------------------------------------
    @GET
    @Path("/{id}/rota/{periodo}")
    @Produces("application/json")
    public Response getRotaCarro(
            @PathParam("id") Long idCarro,
            @PathParam("periodo") String periodoStr) {
        try {
            PerioddoEnum periodo;
            try {
                periodo = PerioddoEnum.valueOf(periodoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"erro\":\"Período inválido. Use MANHA, TARDE ou INTEGRAL.\"}").build();
            }

            List<ObjetoRota> objetos = alunoService.findObjetosRota(idCarro, periodo);
            List<Map<String, Object>> resultado = new ArrayList<>();

            for (ObjetoRota obj : objetos) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", obj.getId());
                item.put("posicao", obj.getPosicao());
                item.put("nome", obj.getNome());
                item.put("descricao", obj.getDescricao());
                item.put("pegarEntregar", obj.getPegarEntregar() != null ? obj.getPegarEntregar().name() : null);
                item.put("idCarro", obj.getIdCarro());
                item.put("idCarroAlvo", obj.getIdCarroAlvo());
                item.put("escola", obj.getEscola() != null ? obj.getEscola().name() : null);
                item.put("quantidadeAlunos", obj.getQuantidadeAlunos());

                if (obj.getAluno() != null) {
                    Map<String, Object> alunoMap = new HashMap<>();
                    alunoMap.put("id", obj.getAluno().getId());
                    alunoMap.put("nomeAluno", obj.getAluno().getNomeAluno());
                    alunoMap.put("enderecoAluno", obj.getAluno().getEnderecoAluno());
                    alunoMap.put("latitudeAluno", obj.getAluno().getLatitudeAluno());
                    alunoMap.put("longitudeAluno", obj.getAluno().getLongitudeAluno());
                    alunoMap.put("contatoNome1", obj.getAluno().getContatoNome1());
                    alunoMap.put("contatoTelefone1", obj.getAluno().getContatoTelefone1());
                    item.put("aluno", alunoMap);
                }

                resultado.add(item);
            }

            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(resultado)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
