package org.aaf.webInterface.rest;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.aaf.dto.MemberDTO;
import org.aaf.webInterface.util.ServiceLocator;
import org.escola.service.MemberRegistration;

import com.cedarsoftware.util.io.JsonWriter;

@Path("/login")
@RequestScoped
public class LoginRESTService {

	@EJB
	private MemberRegistration userService;

	@GET
	@Path("/automatic/{phoneNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginAutomatico(@PathParam("phoneNumber") String phoneNumber) {
		Response.ResponseBuilder builder = null;
		MemberDTO member = null;
		Object retorno = getService().findByPhoneDTO(phoneNumber);
		if(retorno != null){
			member = (MemberDTO)retorno;
		}
		if (member == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(member			));
		}

		return builder.build();
	}

	@GET
	@Path("/{login}/{senha}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUsuarioESenha(@PathParam("login") String login, @PathParam("senha") String senha) {
		Response.ResponseBuilder builder = null;
		MemberDTO member = null;
		Object retorno = getService().findByLoginSenha(login, senha);
		if(retorno != null){
			member = (MemberDTO)retorno;
		}

		if (member == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(member));
		}

		return builder.build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUsuarioESenha(@PathParam("id") String id) {
		Response.ResponseBuilder builder = null;

		MemberDTO member = getService().findByIdDTO(Long.parseLong(id));

		if (member == null) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			builder = Response.ok();
			builder.entity(JsonWriter.objectToJson(member));
		}

		return builder.build();
	}

	
	/*
	 * @GET
	 * 
	 * @Path("/login/{countryId:[0-9][0-9]*}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public TeamDTO
	 * lookupAvaliableTeamByCountry(@PathParam("countryId") long id) { // Team t
	 * = null; // try { // t = teamService.getAvailableTeam(id); // if (t ==
	 * null) { // throw new WebApplicationException(Response.Status.NOT_FOUND);
	 * // } // // } catch (Exception e) { // e.printStackTrace(); // } // return
	 * t; return null; }
	 */

	// TODO - importante passar senha e depois token
	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * register(@QueryParam("login") String login) {
	 * 
	 * Response.ResponseBuilder builder = Response.ok();
	 * 
	 * try {
	 * 
	 * System.out.println("chegou no web service!!!!!!!!!!!!!");
	 * System.out.println("chegou no web service!!!!!!!!!!!!!  -------- logn:" +
	 * login);
	 * builder.entity(JsonWriter.objectToJson(Convertes.getUser(userService.
	 * login(login))));
	 * 
	 * } catch (ConstraintViolationException ce) { // Handle bean validation
	 * issues } catch (ValidationException e) { // Handle the unique constrain
	 * violation Map<String, String> responseObj = new HashMap<>();
	 * responseObj.put("email", "Email taken"); builder =
	 * Response.status(Response.Status.CONFLICT).entity(responseObj); } catch
	 * (Exception e) { // Handle generic exceptions Map<String, String>
	 * responseObj = new HashMap<>(); responseObj.put("error", e.getMessage());
	 * builder =
	 * Response.status(Response.Status.BAD_REQUEST).entity(responseObj); }
	 * 
	 * return builder.build(); }
	 */
	
	  public MemberRegistration getService(){
	    	try {
	    		if(userService == null){
	    			userService = (MemberRegistration) ServiceLocator.getInstance().getEjb(MemberRegistration.class.getSimpleName(), MemberRegistration.class.getName());
	    		}
			} catch (NamingException e) {
				
			}
			return userService;
	    }
}
