package org.aaf.webInterface.rest;

import org.aaf.escolar.MemberDTO;
import org.escolar.model.Member;
import org.escolar.service.MemberRegistration;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("/members")
@RequestScoped
@Stateful
public class MemberRest {

    @EJB
    private MemberRegistration memberRegistration;

    // -----------------------------------------------------------------------
    // POST /members — registo (existente, mantido)
    // -----------------------------------------------------------------------
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response save(String body) {
        try {
            System.out.println("Salvando novo member");
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            MemberDTO dto = mapper.readValue(body, MemberDTO.class);
            System.out.println("DTO formado: " + dto);
            // register() existe no MemberRegistration ✓
            memberRegistration.register(dto);
            return Response.ok("{\"status\":\"Salvo\"}").build();
        } catch (javax.validation.ConstraintViolationException e) {
            return createViolationResponse(e.getConstraintViolations()).build();
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }
    }

    // -----------------------------------------------------------------------
    // GET /members/{id}
    // -----------------------------------------------------------------------
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getMember(@PathParam("id") Long id) {
        try {
            // findByIdDTO() existe no MemberRegistration ✓
            MemberDTO dto = memberRegistration.findByIdDTO(id);
            if (dto == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Membro nao encontrado\"}").build();
            return Response.ok(
                    com.cedarsoftware.util.io.JsonWriter.objectToJson(dto)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // PUT /members/{id}/token  — atualiza token FCM
    // -----------------------------------------------------------------------
    @PUT
    @Path("/{id}/token")
    @Consumes("application/json")
    @Produces("application/json")
    public Response atualizarToken(@PathParam("id") Long id, String body) {
        try {
            // findById() existe no MemberRegistration ✓
            Member member = memberRegistration.findById(id);
            if (member == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Membro nao encontrado\"}").build();

            @SuppressWarnings("unchecked")
            Map<String, Object> campos =
                    (Map<String, Object>) com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            // setTokenFCM() existe no Member ✓
            if (campos.get("tokenFCM") != null)
                member.setTokenFCM((String) campos.get("tokenFCM"));
            // setLatitude() / setLongitude() existem no Member ✓
            if (campos.get("latitude") != null)
                member.setLatitude(Double.parseDouble(campos.get("latitude").toString()));
            if (campos.get("longitude") != null)
                member.setLongitude(Double.parseDouble(campos.get("longitude").toString()));

            // CORRECÇÃO: memberRegistration.save() não existe.
            // O método correcto é persist()+flush() via o EJB.
            // MemberRegistration.register() faz persist+flush — mas aqui é update, não insert.
            // Usamos o padrão do próprio projecto: merge via EntityManager.
            memberRegistration.atualizarMember(member);

            return Response.ok("{\"status\":\"ok\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -----------------------------------------------------------------------
    // PUT /members/{id}/preferencias
    // -----------------------------------------------------------------------
    @PUT
    @Path("/{id}/preferencias")
    @Consumes("application/json")
    @Produces("application/json")
    public Response atualizarPreferencias(@PathParam("id") Long id, String body) {
        try {
            Member member = memberRegistration.findById(id);
            if (member == null)
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Membro nao encontrado\"}").build();

            @SuppressWarnings("unchecked")
            Map<String, Object> campos =
                    (Map<String, Object>) com.cedarsoftware.util.io.JsonReader.jsonToJava(body);

            // setDistanciaAlerta() / setAlertaProximidade() / setEnviarBoletosEmail() existem ✓
            if (campos.get("distanciaAlerta") != null)
                member.setDistanciaAlerta(Integer.parseInt(campos.get("distanciaAlerta").toString()));
            if (campos.get("alertaProximidade") != null)
                member.setAlertaProximidade((Boolean) campos.get("alertaProximidade"));
            if (campos.get("enviarBoletosEmail") != null)
                member.setEnviarBoletosEmail((Boolean) campos.get("enviarBoletosEmail"));

            memberRegistration.atualizarMember(member);
            return Response.ok("{\"status\":\"ok\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\":\"" + e.getMessage() + "\"}").build();
        }
    }

    private Response.ResponseBuilder createViolationResponse(
            Set<javax.validation.ConstraintViolation<?>> violations) {
        Map<String, String> resp = new HashMap<>();
        for (javax.validation.ConstraintViolation<?> v : violations)
            resp.put(v.getPropertyPath().toString(), v.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(resp);
    }
}
