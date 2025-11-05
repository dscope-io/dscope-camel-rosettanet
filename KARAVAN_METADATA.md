# Karavan Metadata Bundle

This bundle prepares Apache Karavan metadata for the Camel RosettaNet component.

## Generate all model JSONs

```
mvn -Pkaravan-metadata -q exec:java -Dexec.mainClass=io.dscope.tools.karavan.RosettaNetKaravanModelGenerator
```

Outputs to: `src/main/resources/karavan/metadata/model/*.json`

## Maven profile (add to your pom.xml)

```xml
<profiles>
  <profile>
    <id>karavan-metadata</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.codehaus.mojo</groupId>
          <artifactId>exec-maven-plugin</artifactId>
          <version>3.3.0</version>
          <configuration>
            <classpathScope>compile</classpathScope>
          </configuration>
        </plugin>
      </plugins>
    </build>
    <dependencies>
      <dependency>
        <groupId>io.github.classgraph</groupId>
        <artifactId>classgraph</artifactId>
        <version>4.8.172</version>
      </dependency>
      <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.1</version>
      </dependency>
    </dependencies>
  </profile>
</profiles>
```

## CI

Workflow: `.github/workflows/generate-karavan-metadata.yml`
