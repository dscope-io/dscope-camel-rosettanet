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

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "4.15.0", scheme = "rosettanet", syntax = "rosettanet", title = "RosettaNet Document Processor", category = { Category.DOCUMENT})
public class RosettaNetEndpoint extends DefaultEndpoint {
	
	private static final Logger LOG = LoggerFactory.getLogger(RosettaNetEndpoint.class);
	
	@UriPath(label = "common",  enums = "marshal,unmarshal")
	@Metadata(description = "The type of RosettaNet operation to use",required = true)
    private String methodType;	
	
	@UriParam(label = "common", enums = "xml")
	@Metadata(description = "The type RosettaNet payload", required = false)
    private String type = RosettaNetConfiguration.XML_TYPE;
	
    @UriParam(label = "common")
    @Metadata(description = "The PIP type of RosettaNet message (e.g., PIP3A4)")
    private String pipType;
    
    @UriParam(label = "common")
    @Metadata(description = "The version of RosettaNet message (e.g., 02_05)")
    private String version; 
    
    @UriParam(label = "common")
    @Metadata(description = "The message name of RosettaNet message (e.g., PurchaseOrderRequest)")
    private String messageName;     
    
    
    public RosettaNetEndpoint(String uri, RosettaNetComponent component, String type) {
    	super(UnsafeUriCharactersEncoder.encode(uri), component);
    	
    	if(type != null)
    		this.type = type;
    	  		
    }
    
    public RosettaNetEndpoint(String uri, RosettaNetComponent component, String pipType, String type) {
    	super(UnsafeUriCharactersEncoder.encode(uri), component);
    	
    	if(type != null)
    		this.type = type;
    	
 
    	if(pipType != null)
    		this.pipType = pipType;   	
    	
    } 
    
    public RosettaNetEndpoint(String uri, RosettaNetComponent component) {
    	super(UnsafeUriCharactersEncoder.encode(uri), component);
    }   

	@Override
	public Producer createProducer() throws Exception {
		Producer producer = new RosettaNetProducer(this);
		
		return producer;
	}
	
	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		// Consumer not implemented yet
		return null;
	}	

	/**
	 * @return methodType
	 */
	public String getMethodType() {
		return methodType;
	}

	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}	

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type of payload
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return pipType
	 */
	public String getPipType() {
		return this.pipType;
	}

	/**
	 * @param pipType to set
	 */
	public void setPipType(String pipType) {
		this.pipType = pipType;
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return messageName 
	 */
	public String getMessageName() {
		return this.messageName;
	}

	/**
	 * @param messageName 
	 */
	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}	
	
}
