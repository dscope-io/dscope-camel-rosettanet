# dscope-camel-rosettanet

Apache Camel component for processing RosettaNet messages using the Jakarta JAXB library.

## Overview

This component provides marshalling and unmarshalling capabilities for RosettaNet Partner Interface Process (PIP) messages. It integrates with the [RosettaNetJakartaJAXB](https://github.com/dscope-io/RosettaNetJakartaJAXB) library to provide full RosettaNet message processing support.

## Requirements

- Java 21+
- Apache Camel 4.14.1+
- Maven 3.6+

## Installation

Add the dependency to your Maven project:

```xml
<dependency>
    <groupId>io.dscope</groupId>
    <artifactId>dscope-camel-rosettanet</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

### URI Format

```
rosettanet:marshal[?options]
rosettanet:unmarshal[?options]
```

### URI Options

| Option | Type | Description |
|--------|------|-------------|
| `pipType` | String | *Required.* The PIP type identifier (e.g., `PIP3A4`) |
| `version` | String | Optional. The message version (e.g., `02_05`). If not specified, the latest version is used. |
| `messageName` | String | Optional. The specific message name (e.g., `PurchaseOrderRequest`) |
| `type` | String | Optional. The payload type. Currently only `xml` is supported. Default: `xml` |

### Headers

The component can use the following message headers:

| Header | Type | Description |
|--------|------|-------------|
| `pipType` | String | PIP type identifier (overrides endpoint configuration) |
| `version` | String | Message version (overrides endpoint configuration) |
| `messageName` | String | Message name (overrides endpoint configuration) |

### Examples

#### Unmarshalling a RosettaNet Message

```java
from("file:input")
    .to("rosettanet:unmarshal?pipType=PIP3A4&version=02_05&messageName=PurchaseOrderRequest")
    .to("log:output");
```

#### Marshalling a RosettaNet Message

```java
from("direct:start")
    .to("rosettanet:marshal?pipType=PIP3A4&version=02_05&messageName=PurchaseOrderRequest")
    .to("file:output");
```

#### Using Headers

```java
from("direct:start")
    .setHeader("pipType", constant("PIP3A4"))
    .setHeader("version", constant("02_05"))
    .setHeader("messageName", constant("PurchaseOrderRequest"))
    .to("rosettanet:unmarshal")
    .to("log:output");
```

## Samples

Check out the [samples](samples/) directory for example implementations:

- **[YAML PIP3A4 Sample](samples/yaml-pip3a4-sample/)** - Demonstrates YAML DSL routes for RosettaNet PIP3A4 Purchase Order processing

## Building

To build the project:

```bash
mvn clean package
```

Note: This project requires Java 21. If you're using a different Java version, configure Maven toolchains accordingly.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Related Projects

- [RosettaNetJakartaJAXB](https://github.com/dscope-io/RosettaNetJakartaJAXB) - Jakarta JAXB bindings for RosettaNet
- [dscope-camel-iso20022](https://github.com/dscope-io/dscope-camel-iso20022) - Similar Camel component for ISO20022 messages
