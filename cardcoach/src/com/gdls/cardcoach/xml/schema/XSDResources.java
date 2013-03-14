package com.gdls.cardcoach.xml.schema;

import java.io.InputStream;

public class XSDResources {

	private XSDResources() {
		
	}
	
	public static InputStream getSchema(String name) {
		return XSDResources.class.getResourceAsStream(name);
	}
}
