package org.aaf.webInterface.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.aaf.escolar.AlunoDTO;
import org.escolar.service.AlunoService;
import org.escolar.util.ServiceLocator;

import com.cedarsoftware.util.io.JsonWriter;

@Path("/students")
@RequestScoped
@Stateful
public class AlunoRest {
       
    @Inject
    private AlunoService memberRegistration;

    
   
    @GET
    @Path("/boletos/{idStudent}/{mesBoleto}/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enviarBoletoEmail(@PathParam("idStudent") String idStudent,
    								  @PathParam("mesBoleto") String mesBoleto,
    								  @PathParam("email") String email) {
     
    	System.out.println("xxpt 000 ");
    	Response.ResponseBuilder builder = null;

    	long idcrianca = Long.parseLong(idStudent);
    	System.out.println("xxpt 001 ");
    	System.out.println("xxpt 002 ");
    	int mesBoletoInt = Integer.parseInt(mesBoleto);
    	System.out.println("xxpt 003 ");
		String erro = getService().enviarBoletoEmail(idcrianca, mesBoletoInt, email);		 
		System.out.println("xxpt 004 ");
		if (erro != null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			//builder.entity(JsonWriter.objectToJson(aluno));
		}

		return builder.build();
    }
    
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        //log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
    
    public AlunoService getService(){
    	try {
    		if(memberRegistration == null){
    			memberRegistration = (AlunoService) ServiceLocator.getInstance().getAlunoService(AlunoService.class.getSimpleName(), AlunoService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return memberRegistration;
    }
    
    
}
