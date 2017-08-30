//package org.aaf.webInterface.util;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import javax.ws.rs.ApplicationPath;
//import javax.ws.rs.core.Application;
//
//import org.aaf.webInterface.rest.RecadoRest;
//
//
//@ApplicationPath("rest") 
//public class RestfulApp extends Application {
//	private Set<Object> singletons = new HashSet<Object>();
//	private Set<Class<?>> empty = new HashSet<Class<?>>();
//
//	public RestfulApp() {
//		this.singletons.add(new RecadoRest());
//	}
//
//	@Override
//	public Set<Class<?>> getClasses() {
//		return this.empty;
//	}
//
//	@Override
//	public Set<Object> getSingletons() {
//		return this.singletons;
//	}
//}