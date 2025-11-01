# Camel YAML Route Sample - RosettaNet PIP 3A4

This sample demonstrates how to configure and run Apache Camel routes using YAML DSL with the dScope RosettaNet component, specifically showcasing PIP 3A4 (Purchase Order Request) message processing.

## Overview

This example shows:
- How to define Camel routes using YAML configuration
- How to use the RosettaNet component for marshalling and unmarshalling PIP 3A4 messages
- How to run a standalone Camel application with YAML routes

## What is PIP 3A4?

PIP 3A4 (Partner Interface Process 3A4) is a RosettaNet standard for Purchase Order Request messages. It's used for electronic exchange of purchase order information between trading partners.

## Prerequisites

- Java 21 or later
- Maven 3.6 or later

## Project Structure

```
camel-yaml-route/
├── pom.xml                                    # Maven project configuration
├── README.md                                  # This file
└── src/
    └── main/
        ├── java/
        │   └── io/dscope/camel/sample/
        │       └── YamlRouteApplication.java  # Main application class
        └── resources/
            └── routes.yaml                     # YAML route definitions
```

## Routes Defined

The sample includes two routes:

### 1. Unmarshal Route (`rosettanet-pip3a4-unmarshal-route`)
- Triggered by a timer (runs once after 10 seconds)
- Demonstrates unmarshalling a RosettaNet PIP 3A4 XML message
- Uses `rosettanet:unmarshal` endpoint with PIP3A4 type and version 02_05

### 2. Marshal Route (`rosettanet-pip3a4-marshal-route`)
- Triggered by a timer (runs once after 15 seconds)
- Demonstrates marshalling a RosettaNet PIP 3A4 object to XML
- Uses `rosettanet:marshal` endpoint with PIP3A4 type and version 02_05

## Building the Sample

To build the project:

```bash
cd sample/camel-yaml-route
mvn clean package
```

This will:
- Compile the Java source code
- Download all required dependencies
- Package the application

## Running the Sample

To run the sample application:

```bash
mvn exec:java
```

Or alternatively, after building:

```bash
java -cp target/classes:target/dependency/* io.dscope.camel.sample.YamlRouteApplication
```

## Expected Output

When you run the sample, you should see log messages similar to:

```
[main] INFO  - Starting RosettaNet PIP 3A4 unmarshal example
[main] INFO  - Processing RosettaNet PIP 3A4 message (unmarshal)
[main] INFO  - Message unmarshalled successfully: [object]
[main] INFO  - Starting RosettaNet PIP 3A4 marshal example
[main] INFO  - Processing RosettaNet PIP 3A4 message (marshal)
[main] INFO  - Message marshalled successfully: [XML string]
```

The application will run for approximately 20 seconds and then shut down automatically after both routes have executed.

## Understanding the YAML Route Configuration

The `routes.yaml` file defines routes using Camel's YAML DSL:

```yaml
- route:
    id: rosettanet-pip3a4-unmarshal-route
    from:
      uri: timer:unmarshal-timer
      parameters:
        period: 10000
        repeatCount: 1
      steps:
        - to:
            uri: rosettanet:unmarshal
            parameters:
              pipType: PIP3A4
              version: 02_05
```

Key configuration elements:
- `pipType: PIP3A4` - Specifies the RosettaNet PIP type
- `version: 02_05` - Specifies the message version
- The `rosettanet:unmarshal` endpoint converts XML to Java objects
- The `rosettanet:marshal` endpoint converts Java objects to XML

## Customization

You can customize this sample by:

1. **Changing the PIP type**: Modify the `pipType` parameter to use other PIP types
2. **Using different versions**: Change the `version` parameter to use different message versions
3. **Adding more routes**: Define additional routes in the `routes.yaml` file
4. **Integrating with other systems**: Replace the timer endpoints with file, HTTP, or other Camel components

## Dependencies

This sample uses:
- **Apache Camel Core** (4.14.1) - Core routing engine
- **Apache Camel Main** (4.14.1) - Standalone application support
- **Apache Camel YAML DSL** (4.14.1) - YAML configuration support
- **dScope Camel RosettaNet** (0.1.0) - RosettaNet message processing component

## Troubleshooting

If you encounter issues:

1. **Dependency resolution errors**: Ensure you have access to the required Maven repositories
2. **Java version mismatch**: Verify you're using Java 21 or later
3. **Route loading errors**: Check the YAML syntax in `routes.yaml`

## Further Reading

- [Apache Camel YAML DSL](https://camel.apache.org/manual/yaml-dsl.html)
- [RosettaNet Standards](https://www.gs1us.org/industries-insights/standards/rosettanet)
- [dScope Camel RosettaNet Component](../../README.md)

## License

This sample is licensed under the Apache License 2.0 - see the [LICENSE](../../LICENSE) file for details.
