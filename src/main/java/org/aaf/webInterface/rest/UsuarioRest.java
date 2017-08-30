///*
// * JBoss, Home of Professional Open Source
// * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
// * contributors by the @authors tag. See the copyright.txt in the
// * distribution for a full listing of individual contributors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.aaf.webInterface.rest;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.ejb.Stateful;
//import javax.enterprise.context.RequestScoped;
//import javax.inject.Inject;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import javax.validation.ValidationException;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.aaf.dto.UserDTO;
//import org.aaf.webInterface.service.UsuarioService;
//
//import com.cedarsoftware.util.io.JsonReader;
//import com.cedarsoftware.util.io.JsonWriter;
//
///**
// * JAX-RS Example
// * <p/>
// * This class produces a RESTful service to read/write the contents of the members table.
// */
//@Path("/users")
//@RequestScoped
//@Stateful
//public class UsuarioRest {
//    @Inject
//    private UsuarioService usuarioService;
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<UserDTO> listAll() {
//        /*return usuarioService.findAll();*/
//    	return null;
//    }
//
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createUser(String member) {
//
//        Response.ResponseBuilder builder = null;
//       /* UsuarioDTO dto = (UsuarioDTO) JsonReader.jsonToJava(member);
//        
//        try {
//            // Validates member using bean validation
//            //validateMember(member);
//
//        	dto = usuarioService.create(dto);
//
//            // Create an "ok" response
//            builder = Response.ok().entity(JsonWriter.objectToJson(dto));
//        } catch (ConstraintViolationException ce) {
//            // Handle bean validation issues
//            builder = createViolationResponse(ce.getConstraintViolations());
//        } catch (ValidationException e) {
//            // Handle the unique constrain violation
//            Map<String, String> responseObj = new HashMap<>();
//            responseObj.put("email", "Email taken");
//            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
//        } catch (Exception e) {
//            // Handle generic exceptions
//            Map<String, String> responseObj = new HashMap<>();
//            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
//        }*/
//
//        return builder.build();
//    }
//
//    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
//
//        Map<String, String> responseObj = new HashMap<>();
//
//        for (ConstraintViolation<?> violation : violations) {
//            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
//        }
//
//        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
//    }
// 
//    @GET
//	@Path("/id/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getTeansLeague(@PathParam("id") String id) {
//		Response.ResponseBuilder builder = null;
//
//		/*Usuario usuario = usuarioService.findById(Long.parseLong(id));
//
//		if (usuario == null) {
//			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
//			throw new WebApplicationException(Response.Status.NOT_FOUND);
//		} else {
//			UsuarioDTO user = new UsuarioDTO();
//			user.setId(usuario.getId());
//			user.setName(usuario.getName());
//			user.setPontosGeral(usuario.getPontosGeral());
//			user.setPontosMes(usuario.getPontosMes());
//			user.setPontosSemana(usuario.getPontosSemana());
//			
//			builder = Response.ok();
//			builder.entity(JsonWriter.objectToJson(user));
//		}
//*/
//		return builder.build();
//	}
//
//    @GET
// 	@Path("/maiorPontuadorSemana/")
// 	@Produces(MediaType.APPLICATION_JSON)
// 	public Response getUsuarioComMaisPontosGrupoSemana() {
// 		Response.ResponseBuilder builder = null;
//
// 		/*Usuario usuario = usuarioService.findMaiorPontuadorSemana();
//
// 		if (usuario == null) {
// 			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
// 			throw new WebApplicationException(Response.Status.NOT_FOUND);
// 		} else {
// 			UsuarioDTO user = new UsuarioDTO();
// 			user.setId(usuario.getId());
// 			user.setName(usuario.getName());
// 			user.setPontosGeral(usuario.getPontosGeral());
// 			user.setPontosMes(usuario.getPontosMes());
// 			user.setPontosSemana(usuario.getPontosSemana());
// 			
// 			builder = Response.ok();
// 			builder.entity(JsonWriter.objectToJson(user));
// 		}*/
//
// 		return builder.build();
// 	}
//
//    @GET
// 	@Path("/maiorPontuadorMes/")
// 	@Produces(MediaType.APPLICATION_JSON)
// 	public Response getUsuarioComMaisPontosMes() {
// 		Response.ResponseBuilder builder = null;
//
// 	/*	Usuario usuario = usuarioService.findMaiorPontuadorMes();
//
// 		if (usuario == null) {
// 			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
// 			throw new WebApplicationException(Response.Status.NOT_FOUND);
// 		} else {
// 			UserDTO user = new UserDTO();
// 			user.setId(usuario.getId());
// 			user.setName(usuario.getName());
// 			user.setPontosGeral(usuario.getPontosGeral());
// 			user.setPontosMes(usuario.getPontosMes());
// 			user.setPontosSemana(usuario.getPontosSemana());
// 			
// 			builder = Response.ok();
// 			builder.entity(JsonWriter.objectToJson(user));
// 		}*/
//
// 		return builder.build();
// 	}
//
//    @GET
// 	@Path("/maiorPontuadorGeral/")
// 	@Produces(MediaType.APPLICATION_JSON)
// 	public Response getUsuarioComMaisPontosGeral() {
// 		Response.ResponseBuilder builder = null;
//
// 		/*Usuario usuario = usuarioService.findMaiorPontuadorGeral();
//
// 		if (usuario == null) {
// 			builder = Response.status(Response.Status.BAD_REQUEST).entity("erro");
// 			throw new WebApplicationException(Response.Status.NOT_FOUND);
// 		} else {
// 			UsuarioDTO user = new UsuarioDTO();
// 			user.setId(usuario.getId());
// 			user.setName(usuario.getName());
// 			user.setPontosGeral(usuario.getPontosGeral());
// 			user.setPontosMes(usuario.getPontosMes());
// 			user.setPontosSemana(usuario.getPontosSemana());
// 			
// 			builder = Response.ok();
// 			builder.entity(JsonWriter.objectToJson(user));
// 		}*/
//
// 		return builder.build();
// 	}
//
//}
