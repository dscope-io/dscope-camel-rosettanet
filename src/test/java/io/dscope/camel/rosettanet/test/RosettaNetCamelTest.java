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

package io.dscope.camel.rosettanet.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RosettaNetCamelTest extends CamelTestSupport {

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// Test route for unmarshalling RosettaNet messages
				from("direct:unmarshal")
					.to("rosettanet:unmarshal?pipType=PIP3A4&version=02_05")
					.to("mock:result");
				
				// Test route for marshalling RosettaNet messages
				from("direct:marshal")
					.to("rosettanet:marshal?pipType=PIP3A4&version=02_05")
					.to("mock:result");
			}
		};
	}

	@Test
	public void testComponentRegistered() throws Exception {
		// Test that the component is registered
		assertNotNull(context.getComponent("rosettanet"));
	}
	
	@Test
	public void testEndpointCreation() throws Exception {
		// Test endpoint creation
		ProducerTemplate template = context.createProducerTemplate();
		
		// This will test that the endpoint can be created without errors
		// Actual message processing would require real RosettaNet message data
		assertNotNull(context.getEndpoint("rosettanet:unmarshal?pipType=PIP3A4"));
		assertNotNull(context.getEndpoint("rosettanet:marshal?pipType=PIP3A4"));
	}
}
