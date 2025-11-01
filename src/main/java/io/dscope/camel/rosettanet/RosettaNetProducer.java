/*
 * Copyright 2025 dScope.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package io.dscope.camel.rosettanet;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;
import java.util.Properties;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultAsyncProducer;

import io.dscope.rosettanet.dictionary.RosettaNetDictionary;
import io.dscope.rosettanet.dictionary.RosettaNetDictionary.MessageDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RosettaNetProducer extends DefaultAsyncProducer {
	public static final String PIP_TYPE_HEADER = "pipType";
	public static final String VERSION_HEADER = "version";
	public static final String MESSAGE_NAME_HEADER = "messageName";

	private static final Logger LOG = LoggerFactory.getLogger(RosettaNetProducer.class);

	private CamelContext camelContext;

	public RosettaNetProducer(RosettaNetEndpoint endpoint) {

		super(endpoint);
		this.camelContext = endpoint.getCamelContext();

	}

	@Override
	public RosettaNetEndpoint getEndpoint() {
		return (RosettaNetEndpoint) super.getEndpoint();
	}

	public boolean process(Exchange exchange, AsyncCallback callback) {
		LOG.debug("Exchange Pattern {}", exchange.getPattern());

		String methodType = this.getEndpoint().getMethodType();

		switch (methodType) {
		case RosettaNetConfiguration.MARSHAL_PREFIX:
			return this.marshall(exchange, callback);
		case RosettaNetConfiguration.UNMARSHAL_PREFIX:
			return this.unmarshall(exchange, callback);

		}

		exchange.setException(new Exception("Unknown method " + methodType));
		callback.done(true);
		return true;

	}

	boolean marshall(Exchange exchange, AsyncCallback callback) {
		try {
			String type = this.getEndpoint().getType();

			String pipType = this.getEndpoint().getPipType();
			String version = this.getEndpoint().getVersion();
			String messageName = this.getEndpoint().getMessageName();

			if (pipType == null)
				pipType = (String) exchange.getMessage().getHeader(PIP_TYPE_HEADER);

			if (version == null)
				version = (String) exchange.getMessage().getHeader(VERSION_HEADER);

			if (messageName == null)
				messageName = (String) exchange.getMessage().getHeader(MESSAGE_NAME_HEADER);

			if (pipType == null) {
				throw new IllegalArgumentException("PIP type is required for marshalling");
			}

			// Get the message definition from the dictionary
			Optional<MessageDefinition> definition = RosettaNetDictionary.findMessage(pipType, version, messageName);
			
			if (definition.isEmpty()) {
				throw new IllegalArgumentException("No message definition found for PIP type: " + pipType + 
						", version: " + version + ", messageName: " + messageName);
			}

			MessageDefinition msgDef = definition.get();
			LOG.debug("Marshalling RosettaNet message: {} - {}", msgDef.getType(), msgDef.getName());

			Object document = exchange.getIn().getBody();

			// Create JAXB context from the properties file
			Properties properties = RosettaNetDictionary.loadProperties(msgDef.getProperties());
			String contextPath = properties.getProperty("packages");
			if (contextPath == null || contextPath.trim().isEmpty()) {
				throw new IllegalArgumentException("No JAXB packages found in properties file: " + msgDef.getProperties());
			}
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath.trim());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter writer = new StringWriter();
			marshaller.marshal(document, writer);
			String xmlOutput = writer.toString();

			LOG.debug("Marshalled XML message: {}", xmlOutput);
			exchange.getMessage().setBody(xmlOutput);

			callback.done(true);
			return true;

		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage(), e);
			exchange.setException(e);
			callback.done(true);
			return true;
		}
	}

	boolean unmarshall(Exchange exchange, AsyncCallback callback) {
		try {
			String type = this.getEndpoint().getType();

			String pipType = this.getEndpoint().getPipType();
			String version = this.getEndpoint().getVersion();
			String messageName = this.getEndpoint().getMessageName();

			if (pipType == null)
				pipType = (String) exchange.getMessage().getHeader(PIP_TYPE_HEADER);

			if (version == null)
				version = (String) exchange.getMessage().getHeader(VERSION_HEADER);

			if (messageName == null)
				messageName = (String) exchange.getMessage().getHeader(MESSAGE_NAME_HEADER);

			if (pipType == null) {
				throw new IllegalArgumentException("PIP type is required for unmarshalling");
			}

			// Get the message definition from the dictionary
			Optional<MessageDefinition> definition = RosettaNetDictionary.findMessage(pipType, version, messageName);
			
			if (definition.isEmpty()) {
				throw new IllegalArgumentException("No message definition found for PIP type: " + pipType + 
						", version: " + version + ", messageName: " + messageName);
			}

			MessageDefinition msgDef = definition.get();
			LOG.debug("Unmarshalling RosettaNet message: {} - {}", msgDef.getType(), msgDef.getName());

			String xmlInput = exchange.getIn().getBody(String.class);
			LOG.debug("Unmarshalling XML message: {}", xmlInput);

			// Create JAXB context from the properties file
			Properties properties = RosettaNetDictionary.loadProperties(msgDef.getProperties());
			String contextPath = properties.getProperty("packages");
			if (contextPath == null || contextPath.trim().isEmpty()) {
				throw new IllegalArgumentException("No JAXB packages found in properties file: " + msgDef.getProperties());
			}
			JAXBContext jaxbContext = JAXBContext.newInstance(contextPath.trim());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			Object document = unmarshaller.unmarshal(new StringReader(xmlInput));
			
			// Handle JAXBElement wrapper
			if (document instanceof JAXBElement) {
				document = ((JAXBElement<?>) document).getValue();
			}

			exchange.getMessage().setBody(document);
			exchange.getMessage().setHeader(PIP_TYPE_HEADER, msgDef.getType());
			exchange.getMessage().setHeader(VERSION_HEADER, msgDef.getVersion());
			exchange.getMessage().setHeader(MESSAGE_NAME_HEADER, msgDef.getName());

			callback.done(true);
			return true;

		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage(), e);
			exchange.setException(e);
			callback.done(true);
			return true;
		}
	}

}
