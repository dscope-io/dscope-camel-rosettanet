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

import java.util.Map;

import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("rosettanet")
public class RosettaNetComponent extends DefaultComponent {
	
	private static final Logger LOG = LoggerFactory.getLogger(RosettaNetComponent.class);

	@Override
	protected RosettaNetEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		RosettaNetEndpoint endpoint = new RosettaNetEndpoint(uri, this);
		
		setProperties(endpoint, parameters);
		
		if (remaining.startsWith(RosettaNetConfiguration.MARSHAL_PREFIX))
			endpoint.setMethodType(RosettaNetConfiguration.MARSHAL_PREFIX);
		else
			endpoint.setMethodType(RosettaNetConfiguration.UNMARSHAL_PREFIX);
		
		return endpoint;
	}

}
