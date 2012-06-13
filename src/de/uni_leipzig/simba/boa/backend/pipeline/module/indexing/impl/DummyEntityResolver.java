package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DummyEntityResolver implements EntityResolver {

	public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
		return new InputSource(new StringReader(""));
	}

}
