# Karavan Metadata Generation

This document describes the Apache Karavan metadata generation for the Camel RosettaNet component.

## Overview

The metadata generator creates JSON descriptors for all RosettaNet message types, enabling visual design in Apache Karavan with full type support and intelligent field suggestions.

### What Gets Generated

1. **Model Metadata** (`src/main/resources/karavan/metadata/model/*.json`)
   - 187+ JSON files for RosettaNet message types
   - Includes all PIPs and versions from the RosettaNet Dictionary
   - Structure: `{messageType}_{version}_{className}.json`
   - Example: `purchaseorderrequest_02_05_PurchaseOrderRequestType.json`

2. **Field Labels** (`src/main/resources/karavan/metadata/model-labels.json`)
   - 219+ human-friendly field labels
   - Auto-generated from Java field names
   - Example: `purchaseOrderIdentifier` → "Purchase Order Identifier"

3. **Component Metadata** (`src/main/resources/karavan/metadata/component/rosettanet.json`)
   - Component descriptor with RosettaNet-specific attributes
   - PIP type, version, and message name options

### Metadata Structure

Each model JSON contains:
- `className`: Fully qualified Java class name
- `pipName`: PIP identifier (e.g., "3A4", "3B2", "3C3")
- `version`: Message version (e.g., "02_05")
- `messageType`: Message type name (e.g., "purchaseorderrequest")
- `properties`: Array of field definitions with types, cardinality, and labels

## Generate Metadata

### Command Line

```bash
mvn -Pkaravan-metadata compile exec:java
```

This will:
1. Load the RosettaNet Dictionary XML from the JAXB library
2. Process all 200+ message definitions
3. Generate model JSON files for available classes
4. Create field labels from Java introspection
5. Output statistics (processed/skipped counts)

### Maven Profile Configuration

The `karavan-metadata` profile is already configured in `pom.xml`:

```xml
<profile>
  <id>karavan-metadata</id>
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.3.0</version>
        <configuration>
          <mainClass>io.dscope.tools.karavan.RosettaNetKaravanModelGenerator</mainClass>
          <classpathScope>compile</classpathScope>
        </configuration>
      </plugin>
    </plugins>
  </build>
</profile>
```

## Implementation Details

### Dictionary-Driven Generation

The generator uses a **dictionary-driven approach** instead of classpath scanning:

1. **Load RosettaNet Dictionary**: Reads `RosettaNet_Dictionary.xml` from the JAXB library classpath
2. **Parse Message Definitions**: Extracts name, package, PIP type, and version for all 201 messages
3. **Construct Class Names**: Builds fully qualified names as `{package}.{name}Type`
4. **Attempt Class Loading**: Uses `Class.forName()` to load each class
5. **Generate Metadata**: Creates JSON for successfully loaded classes
6. **Handle Missing Classes**: Gracefully skips messages not in the JAXB library

### Message Definition Structure

From RosettaNet_Dictionary.xml:
```xml
<message 
  name="PurchaseOrderRequest" 
  package="io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05" 
  type="PIP3A4" 
  version="02_05"/>
```

Generates:
- Qualified class name: `io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05.PurchaseOrderRequestType`
- PIP code: `3A4` (extracted from `PIP3A4`)
- Message type: `purchaseorderrequest` (from package path)
- Filename: `purchaseorderrequest_02_05_PurchaseOrderRequestType.json`

### Coverage

- **Dictionary Messages**: 200 unique messages (201 with 1 duplicate)
- **Generated Files**: 187 model JSON files
- **Skipped**: 13+ messages (confirmations, notifications not in JAXB library v0.5.0)
- **Field Labels**: 219+ unique field labels

## Supported PIPs

The metadata covers major RosettaNet PIPs including:

- **PIP3A1**: Quote Request/Confirmation
- **PIP3A2**: Price and Availability Request/Response  
- **PIP3A4**: Purchase Order Request/Confirmation/Change
- **PIP3B2**: Shipping Order Request/Confirmation
- **PIP3C3**: Invoice Request/Confirmation
- **PIP4A1**: Strategic Forecast Notification
- **PIP4A2**: Planning/Threshold Release Forecast
- **PIP9B3**: Certificate of Analysis Notification
- **And many more...**

Each PIP may have multiple versions (e.g., 01_00, 02_00, 02_05).

## CI Integration

For automated metadata generation in CI/CD pipelines:

```yaml
# .github/workflows/generate-karavan-metadata.yml
name: Generate Karavan Metadata

on:
  push:
    paths:
      - 'src/main/java/io/dscope/tools/karavan/**'
      - 'pom.xml'

jobs:
  generate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Generate metadata
        run: mvn -Pkaravan-metadata compile exec:java
      - name: Commit changes
        run: |
          git config user.name "GitHub Actions"
          git config user.email "actions@github.com"
          git add src/main/resources/karavan/metadata/
          git commit -m "Update Karavan metadata" || echo "No changes"
          git push
```
