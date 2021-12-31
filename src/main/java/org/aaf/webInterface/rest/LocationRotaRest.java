package org.aaf.webInterface.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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

import org.aaf.escolar.LocationRotaDTO;
import org.escolar.model.LocalizacaoRota;
import org.escolar.service.LocationRotaService;
import org.escolar.util.ServiceLocator;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

@Path("/locationsRota")
@RequestScoped
@Stateful
public class LocationRotaRest {
       
	@Inject
    private LocationRotaService locationService;

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
    @Path("/all/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response getLocationByIdentificador(@PathParam("name") String uiiD) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		
		List<LocationRotaDTO> locs = getService().findByName(uiiD);
		builder.entity(JsonWriter.objectToJson(locs));
		return builder.build();
    }
	
	@GET
    @Path("/all/{name}/{dataUltimaAtualizacao}")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response getLocationNovasByIdentificador(@PathParam("name") String uiiD, @PathParam("dataUltimaAtualizacao") String dataUltimaAtualizacao) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		Date date1 = null;
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		try {
			StringBuilder sb2 = new StringBuilder();
			if(dataUltimaAtualizacao != null && dataUltimaAtualizacao.length() > 10){
				if(dataUltimaAtualizacao != null && dataUltimaAtualizacao.length() > 10){
					StringBuilder sb = new StringBuilder();
					sb.append(dataUltimaAtualizacao);
					sb2.append(sb.substring(0, 10));	
					sb2.append(" ");
					sb2.append(sb.substring(10));
				}
			}
			
			System.out.println("dataUltimaAtualizacaopica de los sacos" + sb2.toString());
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sb2.toString());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		List<LocationRotaDTO> locs = getService().findByName(uiiD,date1);
		builder.entity(JsonWriter.objectToJson(locs));
		return builder.build();
    }
	
	@GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response getLocationByName(@PathParam("name") String uiiD) {
		Response.ResponseBuilder builder = null;
		builder = Response.ok();
		LocalizacaoRota loc = getService().findRotabyName(uiiD);
		LocationRotaDTO locDTO = null;
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
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            //validateMember(member);
        	LocationRotaDTO dtoResponse = (LocationRotaDTO) JsonReader.jsonToJava(member);
        	LocationRotaDTO dto = getService().saveNewLocation(dtoResponse);
        	LocationRotaDTO dto2 = new LocationRotaDTO();
        	dto2.setId(dto.getId());
        	
            // Create an "ok" response
            builder = Response.ok().entity(JsonWriter.objectToJson(dto2));
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

    public LocationRotaService getService(){
    	try {
    		if(locationService == null){
    			locationService = (LocationRotaService) ServiceLocator.getInstance().getLocationRotaService(LocationRotaService.class.getSimpleName(), LocationRotaService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return locationService;
    }
    
}
