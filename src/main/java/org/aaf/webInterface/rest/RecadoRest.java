package org.aaf.webInterface.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.aaf.escolar.RecadoDTO;
import org.aaf.escolar.RecadoDestinatarioDTO;
import org.escolar.service.RecadoService;
import org.escolar.util.ServiceLocator;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

@Path("/recados")
@RequestScoped
@Stateful
public class RecadoRest {
       
    @Inject
    private RecadoService recadoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		builder.entity(JsonWriter.objectToJson(getService().findAllDTO()));
		return builder.build();
    }
    
    @GET
    @Path("/{idMember}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageByMember(@PathParam("idMember") String idMember) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		builder.entity(JsonWriter.objectToJson(getService().findAllDTO(idMember)));
		return builder.build();
    }

    @GET
	@Path("/{idMember}/{ordinal}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeansLeague(@PathParam("idMember") String idMember,@PathParam("ordinal") String ordinal) {
		Response.ResponseBuilder builder = null;

		RecadoDTO recado = getService().findAllDTO().get(Integer.parseInt(ordinal));

		if (recado == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(recado));
		}

		return builder.build();
	}

    @GET
	@Path("/receiver/{idRecado}/{idMember}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecadoDestinatario(@PathParam("idRecado") String idRecado,@PathParam("idMember") String idMember) {
		Response.ResponseBuilder builder = null;

		RecadoDestinatarioDTO recadoDestinatarioDTO = getService().findRecadoDestinatario(idRecado,idMember);

		if (recadoDestinatarioDTO == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(recadoDestinatarioDTO));
		}

		return builder.build();
	}

    
    @POST
    @Path("/answer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAwser(String resposta) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateMember(member);
        	RecadoDestinatarioDTO dto = (RecadoDestinatarioDTO) JsonReader.jsonToJava(resposta);
            getService().saveAwnser(dto);
            
            // Create an "ok" response
            builder = Response.ok().entity(dto);
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
   /* 
    @GET
	@Path("/disciplineYear")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionByDisciplineAndYear(@QueryParam("discipline") String discipline, @QueryParam("year") String year) {
		Response.ResponseBuilder builder = null;

		List<Question> questions = questionService.findByDisciplineOrYear(discipline,year);

		if (questions == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(questions));
		}

		return builder.build();
	}*/

    
   /* @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Question lookupMemberById(@PathParam("id") long id) {
        Question question = questionService.findById(id);
        if (question == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return question;
    }*/

    /*@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createQuestion(Question question) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateMember(member);
        	if(question.getFontSizeQuestion() == 0){
        		question.setFontSizeQuestion(12);
        	}

            questionService.createQuestion(question);

            // Create an "ok" response
            builder = Response.ok().entity(question);
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }*/


    /*private void validateQuestion(Question question) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Question>> violations = validator.validate(question);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
*/
      
    
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        //log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
    
    
    public RecadoService getService(){
    	try {
    		if(recadoService == null){
    			recadoService = (RecadoService) ServiceLocator.getInstance().getEjbGeneric(RecadoService.class.getSimpleName(), RecadoService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return recadoService;
    }
    
}
