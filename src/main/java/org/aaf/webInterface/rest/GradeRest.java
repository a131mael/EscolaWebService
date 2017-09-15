package org.aaf.webInterface.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.aaf.webInterface.util.ServiceLocator;
import org.escola.enums.BimestreEnum;
import org.escola.enums.DisciplinaEnum;
import org.escola.service.GradeService;

import com.cedarsoftware.util.io.JsonWriter;

@Path("/grades")
@RequestScoped
@Stateful
public class GradeRest {
       
	@Inject
    private GradeService gradeService;

    @GET
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
    	
    	Response.ResponseBuilder  builder = Response.ok();
    	
    	return builder.build();
    }

    public GradeService getService(){
    	try {
    		if(gradeService == null){
    			gradeService = (GradeService) ServiceLocator.getInstance().getEjb(GradeService.class.getSimpleName(), GradeService.class.getName());
    		}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return gradeService;
    }
    
}
