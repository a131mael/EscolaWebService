package org.aaf.webInterface.util;

public class EndPoints {

	private static StringBuilder urlBase = new StringBuilder("http://localhost");
	//private static StringBuilder urlBase = new StringBuilder("http://ec2-52-67-36-232.sa-east-1.compute.amazonaws.com");
	
 	
	public static final String SCRAPS = new StringBuilder(urlBase).append("/EscolarWebService/rest/recados").toString();
	
	public static final String GRADE = new StringBuilder(urlBase).append("/EscolarWebService/rest/grades").toString();
}
