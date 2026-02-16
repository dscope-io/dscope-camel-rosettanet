# dscope-camel-rosettanet

Apache Camel component for processing RosettaNet messages using the Jakarta JAXB library.

## Overview

This component provides marshalling and unmarshalling capabilities for RosettaNet Partner Interface Process (PIP) messages. It integrates with the [RosettaNetJakartaJAXB](https://github.com/dscope-io/RosettaNetJakartaJAXB) library to provide full RosettaNet message processing support.

## Requirements

- Java 21+
- Apache Camel 4.15.0+
- Maven 3.6+

## Installation

### Maven (from Maven Central)

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.dscope</groupId>
    <artifactId>dscope-camel-rosettanet</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle (from Maven Central)

Groovy DSL (`build.gradle`):

```groovy
dependencies {
    implementation 'io.dscope:dscope-camel-rosettanet:1.0.0'
}
```

Kotlin DSL (`build.gradle.kts`):

```kotlin
dependencies {
    implementation("io.dscope:dscope-camel-rosettanet:1.0.0")
}
```

### Local development (before Central release)

If you are consuming locally built snapshots/releases from this repository:

1. Build and install locally:

```bash
mvn clean install
```

2. Ensure your consumer project can resolve from local Maven cache.

Maven consumer (`pom.xml`):

```xml
<repositories>
    <repository>
        <id>local-maven</id>
        <url>file://${user.home}/.m2/repository</url>
    </repository>
</repositories>
```

Gradle consumer (`build.gradle` / `build.gradle.kts`):

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}
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

## Apache Karavan Integration

This component includes auto-generated metadata for [Apache Karavan](https://github.com/apache/camel-karavan), the visual designer for Apache Camel. The metadata enables visual design of RosettaNet routes with full type support.

### Karavan Metadata Generation

The component automatically generates Karavan metadata during the build process:

- **Model metadata**: JSON files for all 200+ RosettaNet message types from the official RosettaNet Dictionary
- **Field labels**: Human-friendly labels for message fields (e.g., "Purchase Order Request", "Ship To Location")
- **Component metadata**: Component descriptor with PIP-specific attributes

The metadata generator (`RosettaNetKaravanModelGenerator`) uses the RosettaNet Dictionary XML to create metadata for:
- Purchase Order messages (PIP3A4)
- Quote messages (PIP3A1, PIP3A2)
- Shipping Order messages (PIP3B2)
- Invoice messages (PIP3C3)
- Forecast messages (PIP4A1, PIP4A2)
- And many more PIPs across different versions

To regenerate metadata:

```bash
mvn -Pkaravan-metadata compile exec:java
```

The generated files are located in `src/main/resources/karavan/metadata/`.

## Building

To build the project:

```bash
mvn clean package
```

To install to local Maven repository:

```bash
mvn clean install
```

**Requirements:**
- Java 21 (enforced via Maven toolchains)
- Maven 3.6+

If you're using a different Java version, configure Maven toolchains accordingly.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Related Projects

- [RosettaNetJakartaJAXB](https://github.com/dscope-io/RosettaNetJakartaJAXB) - Jakarta JAXB bindings for RosettaNet
- [dscope-camel-iso20022](https://github.com/dscope-io/dscope-camel-iso20022) - Similar Camel component for ISO20022 messages
