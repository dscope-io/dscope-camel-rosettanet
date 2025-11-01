# Camel YAML Route Sample with RosettaNet PIP 3A4

This sample demonstrates how to configure and run an Apache Camel route using YAML DSL with RosettaNet PIP 3A4 message processing.

## Overview

This example shows:
- How to define Camel routes using YAML DSL
- How to unmarshal a RosettaNet PIP 3A4 Purchase Order Request XML message
- How to marshal a Java object back to RosettaNet XML format
- Integration of the dscope-camel-rosettanet component in a YAML-based route

## Prerequisites

- Java 21+
- Maven 3.6+
- The dscope-camel-rosettanet component (version 0.1.0)
- The dscope-rosettanet-jakarta-jaxb library (version 0.5.0)

## Project Structure

```
camel-yaml-route/
├── pom.xml                              # Maven project configuration
└── src/
    └── main/
        └── resources/
            └── routes.yaml              # YAML route definition
```

## What the Sample Does

The route performs the following steps:

1. **Timer Trigger**: Starts automatically using a timer (runs once after 60 seconds)
2. **Sample XML**: Sets a sample RosettaNet PIP 3A4 Purchase Order Request XML message as the message body
3. **Unmarshal**: Unmarshals the XML into a Java object using the RosettaNet component
4. **Marshal**: Marshals the Java object back into XML format
5. **Log**: Logs the processing steps to the console

The route demonstrates both marshalling and unmarshalling operations using PIP 3A4 version 02.25.

## Building the Sample

To compile the sample, run:

```bash
mvn clean package
```

## Running the Sample

To run the sample, execute:

```bash
mvn exec:java
```

The application will start and execute the route. You should see log output showing:
- The route starting
- The sample XML message
- Successful unmarshalling of the PIP 3A4 message
- Successful marshalling back to XML
- Completion message

Press `Ctrl+C` to stop the application.

## Expected Output

When running the sample, you should see console output similar to:

```
[main] INFO  rosettanet-pip3a4-unmarshal-route - Starting RosettaNet PIP 3A4 sample route
[main] INFO  rosettanet-pip3a4-unmarshal-route - Processing RosettaNet PIP 3A4 message body: <?xml version="1.0"...
[main] INFO  rosettanet-pip3a4-unmarshal-route - Successfully unmarshalled PIP 3A4 message: ...
[main] INFO  rosettanet-pip3a4-unmarshal-route - Successfully marshalled PIP 3A4 message back to XML
[main] INFO  rosettanet-pip3a4-unmarshal-route - Sample completed successfully
```

## Configuration Details

### Route Configuration

The route is defined in `src/main/resources/routes.yaml` and uses:

- **Timer Component**: Triggers the route execution
- **RosettaNet Component**: Handles marshalling/unmarshalling with parameters:
  - `pipType`: PIP3A4 (Partner Interface Process 3A4)
  - `version`: 02_25 (RosettaNet version 02.25)
  - `messageName`: PurchaseOrderRequest

### Dependencies

Key dependencies defined in `pom.xml`:

- `camel-core`: Apache Camel core functionality
- `camel-main`: Enables running Camel as a standalone application
- `camel-yaml-dsl`: Support for YAML-based route definitions
- `dscope-camel-rosettanet`: RosettaNet component for Camel
- `slf4j`: Logging

## Customizing the Sample

To modify the sample for your needs:

1. **Change PIP Type**: Update the `pipType` parameter in routes.yaml
2. **Change Version**: Update the `version` parameter to target a different RosettaNet version
3. **Change Message Type**: Update the `messageName` parameter and the sample XML accordingly
4. **Add Processing**: Add additional Camel steps between unmarshal and marshal operations

## Troubleshooting

- **Build fails**: Ensure you have Java 21 and Maven 3.6+ installed
- **Dependencies not found**: Ensure the dscope-camel-rosettanet and dscope-rosettanet-jakarta-jaxb libraries are available in your local Maven repository or configured repository
- **Runtime errors**: Check that the XML message matches the expected PIP 3A4 schema

## References

- [Apache Camel YAML DSL Documentation](https://camel.apache.org/manual/yaml-dsl.html)
- [RosettaNet Component Documentation](../../README.md)
- [RosettaNet PIP 3A4 Specification](https://www.gs1us.org/resources/rosettanet)

## License

This sample is licensed under the Apache License 2.0 - see the [LICENSE](../../LICENSE) file for details.
