package org.aaf.webInterface.rest;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.aaf.dto.RecadoDTO;
import org.aaf.webInterface.util.ServiceLocator;
import org.escola.service.RecadoService;

@Path("/recados")
@RequestScoped
@Stateful
public class RecadoRest {
       
    @Inject
    private RecadoService recadoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RecadoDTO> listAll() {
        return getService().findAllDTO();
    }

   /* @GET
	@Path("/discipline/{discipline}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeansLeague(@PathParam("discipline") String discipline) {
		Response.ResponseBuilder builder = null;

		List<Question> questions = questionService.findByDiscipline(discipline);

		if (questions == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(questions));
		}

		return builder.build();
	}
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
	}

    
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Question lookupMemberById(@PathParam("id") long id) {
        Question question = questionService.findById(id);
        if (question == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return question;
    }

    @POST
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
    }


    private void validateQuestion(Question question) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Question>> violations = validator.validate(question);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }*/
    
    public RecadoService getService(){
    	try {
    		if(recadoService == null){
    			recadoService = (RecadoService) ServiceLocator.getInstance().getEjb(RecadoService.class.getSimpleName(), RecadoService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return recadoService;
    }
    
}