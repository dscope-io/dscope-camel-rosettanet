# Camel YAML Route Sample - PIP 3A4

This sample demonstrates how to use the dScope Camel RosettaNet component with a YAML-based route configuration to process RosettaNet PIP 3A4 (Purchase Order Request) messages.

## Overview

This example shows:
- How to configure a Camel route using YAML DSL
- How to use the `rosettanet:unmarshal` endpoint to convert XML to Java objects
- How to use the `rosettanet:marshal` endpoint to convert Java objects back to XML
- Processing RosettaNet PIP 3A4 Purchase Order Request messages

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- The parent `dscope-camel-rosettanet` component must be installed in your local Maven repository

## Project Structure

```
camel-yaml-route/
├── pom.xml                           # Maven project configuration
├── README.md                         # This file
└── src/
    └── main/
        └── resources/
            └── routes.yaml            # YAML route definition
```

## Building the Sample

To build the sample:

```bash
cd sample/camel-yaml-route
mvn clean package
```

## Running the Sample

To run the sample:

```bash
mvn exec:java
```

The application will:
1. Start a timer that triggers once after 5 seconds
2. Create a sample PIP 3A4 Purchase Order Request XML message
3. Unmarshal the XML to a Java object using the RosettaNet component
4. Marshal the Java object back to XML
5. Log the results and shut down after 10 seconds

## Expected Output

You should see log output similar to:

```
[main] INFO  rosettanet-pip3a4-demo - Starting RosettaNet PIP 3A4 demonstration...
[main] INFO  rosettanet-pip3a4-demo - Created sample PurchaseOrderRequest XML body
[main] INFO  rosettanet-pip3a4-demo - Successfully unmarshalled XML to Java object: PurchaseOrderRequest
[main] INFO  rosettanet-pip3a4-demo - Successfully marshalled Java object back to XML
[main] INFO  rosettanet-pip3a4-demo - RosettaNet PIP 3A4 demonstration completed successfully!
```

## Understanding the YAML Route

The `routes.yaml` file defines a Camel route that:

1. **Timer Source**: Uses a timer to trigger the route once
   ```yaml
   from:
     uri: timer:demo
     parameters:
       period: 5000
       repeatCount: 1
   ```

2. **Sample Message Creation**: Creates a sample PIP 3A4 Purchase Order Request XML
   ```yaml
   - setBody:
       constant: |
         <?xml version="1.0" encoding="UTF-8"?>
         <PurchaseOrderRequest xmlns="...">
           ...
         </PurchaseOrderRequest>
   ```

3. **Unmarshal**: Converts XML to Java object
   ```yaml
   - to:
       uri: rosettanet:unmarshal
       parameters:
         pipType: PIP3A4
         version: "02_05"
         messageName: PurchaseOrderRequest
   ```

4. **Marshal**: Converts Java object back to XML
   ```yaml
   - to:
       uri: rosettanet:marshal
       parameters:
         pipType: PIP3A4
         version: "02_05"
         messageName: PurchaseOrderRequest
   ```

## Customization

You can modify the route to:
- Process files from a directory instead of using a timer
- Send the marshalled XML to a file or HTTP endpoint
- Add error handling and transformation logic
- Process different PIP types by changing the `pipType` parameter

## Additional Resources

- [Apache Camel YAML DSL Documentation](https://camel.apache.org/manual/yaml-dsl.html)
- [dScope Camel RosettaNet Component](../../README.md)
- [RosettaNet Standards](https://www.rosettanet.org/)

## Troubleshooting

If you encounter issues:

1. **Maven toolchain error**: Ensure Java 21 is properly configured in your Maven toolchains
2. **Component not found**: Make sure the parent project is built and installed (`mvn clean install` from the repository root)
3. **XML parsing errors**: Verify the XML structure matches the RosettaNet PIP 3A4 schema
