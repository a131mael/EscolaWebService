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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.aaf.escolar.LocationDTO;
import org.escolar.model.Localizacao;
import org.escolar.service.LocationService;
import org.escolar.util.ServiceLocator;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

@Path("/locations")
@RequestScoped
@Stateful
public class LocationRest {
       
	@Inject
    private LocationService locationService;

/*    @GET
    @Path("/gradeStudent/{idStudent}/{discipline}/{turn}/{rec}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGrade(@PathParam("idStudent") String idStudent,@PathParam("discipline") String discipline,@PathParam("turn") String turn,@PathParam("rec") String rec) {
     
    	Response.ResponseBuilder builder = null;

    	Float grade = getService().getAVGrade(Long.parseLong(idStudent),  DisciplinaEnum.values()[Integer.parseInt(discipline)], BimestreEnum.values()[Integer.parseInt(turn)], Boolean.parseBoolean(rec));
    	
		if (grade == null || grade.isInfinite() || grade.isNaN()) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(grade));
		}

		return builder.build();
    }
*/
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		builder.entity(JsonWriter.objectToJson(getService().findAllDTO()));
		return builder.build();
    }
	
	@GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll2() {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		builder.entity(JsonWriter.objectToJson(getService().findAllDTO()));
		return builder.build();
    }
	
	@GET
    @Path("/androidID/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response getBAYUIIU(@PathParam("id") String uiiD) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		builder.entity(JsonWriter.objectToJson(getService().findByAndroidID(uiiD).getDTO()));
		return builder.build();
    }
	
	@GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response getLocationByName(@PathParam("name") String uiiD) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		Localizacao loc = getService().findByName(uiiD);
		LocationDTO locDTO = null;
		if(loc != null){
			locDTO = loc.getDTO();	
		}
		builder.entity(JsonWriter.objectToJson(locDTO));
		return builder.build();
    }
	
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response saveLocation(String member) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateMember(member);
        	LocationDTO dtoResponse = (LocationDTO) JsonReader.jsonToJava(member);
        	LocationDTO dto = getService().saveCar(dtoResponse);
            // Create an "ok" response
            builder = Response.ok().entity(JsonWriter.objectToJson(dto));
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

	private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        //log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    public LocationService getService(){
    	try {
    		if(locationService == null){
    			locationService = (LocationService) ServiceLocator.getInstance().getLocationService(LocationService.class.getSimpleName(), LocationService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return locationService;
    }
    
}
