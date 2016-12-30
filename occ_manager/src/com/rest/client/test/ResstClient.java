package com.rest.client.test;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/*import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;*/

public class ResstClient {/*

	*//**
	 * @param args
	 *//*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
ClientConfig config=new DefaultClientConfig();
Client client=Client.create(config);
WebResource service=client.resource(getBaseURI());
LOGGER.info(service.path("rest").path("/").accept(MediaType.APPLICATION_JSON).get(String.class));
// Get XML for application
LOGGER.info(service.path("rest").path("RestContr/json").accept(MediaType.APPLICATION_JSON).get(String.class));
// Get JSON for application

	}
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8080/occ_manager").build();
	  }
*/}
