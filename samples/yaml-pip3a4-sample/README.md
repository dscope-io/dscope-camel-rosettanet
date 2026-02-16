# RosettaNet PIP3A4 YAML Route Sample

This sample demonstrates how to use the dScope Camel RosettaNet component with YAML DSL to process RosettaNet PIP3A4 (Purchase Order Management) messages.

## Overview

PIP3A4 is a RosettaNet Partner Interface Process for Purchase Order Management. This sample shows how to:

1. **Unmarshal** RosettaNet PIP3A4 XML messages into Java objects
2. **Marshal** Java objects into RosettaNet PIP3A4 XML format
3. Use **YAML DSL** to define Camel routes (no Java code required)
4. Process Purchase Order Request messages

## What is PIP3A4?

PIP3A4 (Purchase Order Management) is a RosettaNet standard for managing purchase orders between trading partners. It includes:

- **Purchase Order Request**: Buyer sends order to supplier
- **Purchase Order Confirmation**: Supplier confirms or modifies order
- **Purchase Order Update**: Changes to existing orders
- **Purchase Order Cancellation**: Order cancellation requests

This sample focuses on the Purchase Order Request message type.

## Project Structure

```
yaml-pip3a4-sample/
├── pom.xml                                    # Maven project configuration
├── src/main/resources/
│   └── camel/
│       └── pip3a4-route.yaml                  # YAML route definitions
├── data/
│   ├── input/                                 # Input directory for XML files
│   │   └── sample-purchase-order.xml          # Sample PIP3A4 message
│   └── output/                                # Output directory for processed files
└── README.md                                  # This file
```

## Routes

### Route 1: Unmarshal Route (pip3a4-unmarshal-route)

This route monitors the `data/input` directory for RosettaNet XML files:

1. Reads XML files from `data/input` directory
2. Logs the incoming file name
3. Unmarshals RosettaNet PIP3A4 XML to Java objects using the component
4. Converts the Java object to JSON for easy viewing
5. Writes the JSON output to `data/output` directory

**Configuration:**
- **PIP Type**: PIP3A4
- **Version**: 02_05
- **Message Name**: PurchaseOrderRequest

### Route 2: Marshal Route (pip3a4-marshal-route)

This route demonstrates creating and marshalling a PIP3A4 message:

1. Triggers once after startup (timer component)
2. Creates a sample Purchase Order object structure
3. Marshals the object to RosettaNet PIP3A4 XML format
4. Writes the XML to `data/output/sample-po-request.xml`

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Access to the dscope-camel-rosettanet component (version 1.0.0)

## Building the Sample

```bash
mvn clean package
```

**Note:** This sample requires the `dscope-camel-rosettanet` component to be available. Version `1.0.0` is available from Maven Central. If building from the parent repository, you can also install the component locally first:

```bash
# From the parent repository root
mvn clean install -DskipTests

# Then build this sample
cd samples/yaml-pip3a4-sample
mvn clean package
```

## Running the Sample

### Using Maven Exec Plugin

```bash
mvn exec:java
```

### Using Maven Camel Plugin (Alternative)

If you prefer the Camel Maven plugin:

```bash
mvn camel:run
```

### What to Expect

When you run the sample:

1. The unmarshal route will start monitoring `data/input` directory
2. The sample file `data/input/sample-purchase-order.xml` will be processed
3. A JSON representation will be written to `data/output/`
4. The marshal route will execute once and create `data/output/sample-po-request.xml`
5. Log messages will show the processing steps

**Sample Output:**
```
Processing RosettaNet PIP3A4 message: sample-purchase-order.xml
Successfully unmarshalled PIP3A4 message
Creating sample RosettaNet PIP3A4 Purchase Order Request
Marshalling to RosettaNet PIP3A4 format
```

## Testing the Sample

### Test the Unmarshal Route

1. Copy the sample XML file to the input directory:
   ```bash
   cp data/input/sample-purchase-order.xml data/input/test-order.xml
   ```

2. The route will automatically pick up and process the file

3. Check the output in `data/output/test-order.xml.json`

### Test the Marshal Route

The marshal route runs automatically on startup. Check for `sample-po-request.xml` in the `data/output` directory.

## YAML Route Configuration

The YAML route configuration (`src/main/resources/camel/pip3a4-route.yaml`) demonstrates:

- **File component**: Reading files from a directory
- **Timer component**: Scheduled execution
- **RosettaNet component**: Marshalling and unmarshalling
- **JSON marshalling**: Converting objects to JSON
- **Logging**: Tracking message flow

## Customization

### Change PIP Version

To use a different PIP3A4 version, modify the YAML route:

```yaml
- to:
    uri: rosettanet:unmarshal
    parameters:
      pipType: PIP3A4
      version: "02_06"  # Change version here
      messageName: PurchaseOrderRequest
```

### Process Different Message Types

To process Purchase Order Confirmations instead:

```yaml
parameters:
  pipType: PIP3A4
  version: "02_05"
  messageName: PurchaseOrderConfirmation
```

### Add Error Handling

Add error handling to the YAML route:

```yaml
- route:
    id: pip3a4-unmarshal-route
    errorHandler:
      deadLetterChannel:
        deadLetterUri: file:data/error
        useOriginalMessage: true
    from:
      # ... rest of route
```

## Troubleshooting

### Component Not Found

**Error:** `Cannot find Camel component: rosettanet`

**Solution:** Ensure the parent component is built and installed:
```bash
cd ../..
mvn clean install -DskipTests
```

### Missing Dependencies

**Error:** `Could not resolve dependencies`

**Solution:** The dscope-rosettanet-jakarta-jaxb dependency might not be in public Maven repositories. This component may require access to a private repository or local installation.

### File Not Processed

**Issue:** Files in input directory are not being processed

**Check:**
- Ensure the Camel application is running
- Check file permissions
- Review log output for errors
- Verify the file format matches expected RosettaNet schema

## Related Resources

- [Apache Camel YAML DSL Documentation](https://camel.apache.org/manual/dsl.html#_yaml_dsl)
- [RosettaNet PIP3A4 Specification](https://www.rosettanet.org/)
- [dScope Camel RosettaNet Component](../../README.md)
- [RosettaNetJakartaJAXB Library](https://github.com/dscope-io/RosettaNetJakartaJAXB)

## License

This sample is licensed under the Apache License 2.0 - see the parent [LICENSE](../../LICENSE) file for details.

## Support

For issues or questions:
- Review the main component [README](../../README.md)
- Check the [GitHub Issues](https://github.com/dscope-io/dscope-camel-rosettanet/issues)
- Refer to RosettaNet PIP3A4 specifications
