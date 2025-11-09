package io.dscope.tools.karavan;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;

/**
 * Generates Karavan model metadata for JAXB classes under io.dscope.rosettanet.interchange.
 * Emits one JSON file per *RequestType/*ResponseType into karavan/metadata/model.
 */
public class RosettaNetKaravanModelGenerator {

    private static final String OUT_DIR  = "src/main/resources/karavan/metadata/model";
    private static final String LABELS_FILE = "src/main/resources/karavan/metadata/model-labels.json";

    public static void main(String[] args) throws Exception {
        ObjectMapper om = new ObjectMapper();
        new File(OUT_DIR).mkdirs();
        
        // Load messages from RosettaNet_Dictionary.xml
        List<MessageDefinition> messages = loadMessageDefinitions();
        System.out.println("Loaded " + messages.size() + " message definitions from RosettaNet_Dictionary.xml\n");
        
        // Map to collect all labels: "ModelName.fieldName" -> "Display Label"
        Map<String, String> allLabels = new TreeMap<>();
        
        int processedCount = 0;
        int skippedCount = 0;

        for (MessageDefinition msg : messages) {
            try {
                // Try to load the class
                Class<?> model = Class.forName(msg.qualifiedClassName);
                
                ObjectNode root = om.createObjectNode();
                root.put("model", model.getSimpleName());
                root.put("package", model.getPackageName());
                root.put("qualifiedName", model.getName());
                root.put("pipName", msg.pipName);
                root.put("version", msg.version);
                root.put("messageType", msg.messageType);

                ArrayNode fields = root.putArray("fields");

                // Fields
                for (Field f : model.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    ObjectNode field = fields.addObject();
                    field.put("name", f.getName());
                    field.put("type", simplify(f.getGenericType()));
                    field.put("cardinality", cardinality(f));
                    field.put("jaxbElement", isJaxbElementRef(f));
                    field.put("required", isRequired(f));
                    field.put("description", "");
                    
                    // Add to labels map
                    String labelKey = model.getSimpleName() + "." + f.getName();
                    String labelValue = generateLabel(f.getName());
                    allLabels.put(labelKey, labelValue);
                }

                // Add getters as fallback (if fields are not visible due to XJC generation style)
                for (Method m : model.getMethods()) {
                    if (m.getParameterCount() == 0 && m.getName().startsWith("get")) {
                        String name = decap(m.getName().substring(3));
                        if (!hasField(fields, name)) {
                            ObjectNode fn = fields.addObject();
                            fn.put("name", name);
                            fn.put("type", simplify(m.getGenericReturnType()));
                            fn.put("cardinality", cardinality(m.getGenericReturnType()));
                            fn.put("jaxbElement", false);
                            fn.put("required", false);
                            fn.put("description", "");
                        }
                    }
                }

                String fileName = OUT_DIR + "/" + msg.messageType + "_" + msg.version + "_" + model.getSimpleName() + ".json";
                om.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), root);
                System.out.println("Wrote " + fileName);
                processedCount++;
            } catch (ClassNotFoundException e) {
                // Class not found in classpath - skip it
                skippedCount++;
            }
        }
        
        System.out.println("\nProcessed: " + processedCount + " models");
        System.out.println("Skipped: " + skippedCount + " models (class not found in classpath)");
        
        // Write model-labels.json file
        System.out.println("\nGenerating model-labels.json with " + allLabels.size() + " labels...");
        om.writerWithDefaultPrettyPrinter().writeValue(new File(LABELS_FILE), allLabels);
        System.out.println("Wrote " + LABELS_FILE);
    }
    
    /**
     * Generate a human-friendly label from a camelCase field name.
     * Example: "purchaseOrderNumber" -> "Purchase Order Number"
     */
    private static String generateLabel(String fieldName) {
        // Split camelCase into words
        String[] words = fieldName.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
        
        // Capitalize first letter of each word
        StringBuilder label = new StringBuilder();
        for (String word : words) {
            if (label.length() > 0) {
                label.append(" ");
            }
            if (word.length() > 0) {
                label.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    label.append(word.substring(1));
                }
            }
        }
        
        return label.toString();
    }
    
    /**
     * Load message definitions from RosettaNet_Dictionary.xml in the classpath.
     * For each <message> element, extracts name, package, type (PIP), and version,
     * then constructs the fully qualified class name.
     */
    private static List<MessageDefinition> loadMessageDefinitions() {
        List<MessageDefinition> definitions = new ArrayList<>();
        
        try (InputStream is = RosettaNetKaravanModelGenerator.class.getResourceAsStream("/RosettaNet_Dictionary.xml")) {
            if (is == null) {
                System.err.println("ERROR: Could not find RosettaNet_Dictionary.xml in classpath");
                return definitions;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            
            NodeList messages = doc.getElementsByTagName("message");
            for (int i = 0; i < messages.getLength(); i++) {
                Element message = (Element) messages.item(i);
                String name = message.getAttribute("name");         // e.g., "PurchaseOrderRequest"
                String packageName = message.getAttribute("package"); // e.g., "io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05"
                String pipType = message.getAttribute("type");       // e.g., "PIP3A4"
                String version = message.getAttribute("version");    // e.g., "02_05"
                
                if (name != null && !name.isEmpty() && 
                    packageName != null && !packageName.isEmpty() &&
                    pipType != null && !pipType.isEmpty() &&
                    version != null && !version.isEmpty()) {
                    
                    // Extract message type from package (part after "interchange")
                    String messageType = extractMessageType(packageName);
                    
                    // Extract PIP code from type (e.g., "PIP3A4" -> "3A4")
                    String pipCode = pipType.startsWith("PIP") ? pipType.substring(3) : pipType;
                    
                    // Construct fully qualified class name: package + "." + name + "Type"
                    String qualifiedClassName = packageName + "." + name + "Type";
                    
                    definitions.add(new MessageDefinition(name, packageName, qualifiedClassName, 
                                                         pipCode, version, messageType));
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load RosettaNet_Dictionary.xml: " + e.getMessage());
            e.printStackTrace();
        }
        
        return definitions;
    }
    
    /**
     * Extract message type from package name.
     * E.g., "io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05" -> "purchaseorderrequest"
     */
    private static String extractMessageType(String packageName) {
        String[] parts = packageName.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("interchange")) {
                return parts[i + 1];
            }
        }
        return "unknown";
    }
    
    /**
     * Represents a message definition from the RosettaNet dictionary.
     */
    static class MessageDefinition {
        String name;                 // e.g., "PurchaseOrderRequest"
        String packageName;          // e.g., "io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05"
        String qualifiedClassName;   // e.g., "io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05.PurchaseOrderRequestType"
        String pipName;              // e.g., "3A4"
        String version;              // e.g., "02_05"
        String messageType;          // e.g., "purchaseorderrequest"
        
        MessageDefinition(String name, String packageName, String qualifiedClassName, 
                         String pipName, String version, String messageType) {
            this.name = name;
            this.packageName = packageName;
            this.qualifiedClassName = qualifiedClassName;
            this.pipName = pipName;
            this.version = version;
            this.messageType = messageType;
        }
    }

    private static String simplify(Type t) {
        String s = t.getTypeName();
        // List<...>
        if (s.startsWith("java.util.List")) {
            int lt = s.indexOf('<'), gt = s.lastIndexOf('>');
            if (lt > 0 && gt > lt) {
                return "List<" + simplifyString(s.substring(lt + 1, gt)) + ">";
            }
        }
        // JAXBElement
        if (s.startsWith("jakarta.xml.bind.JAXBElement")) {
            int lt = s.indexOf('<'), gt = s.lastIndexOf('>');
            if (lt > 0 && gt > lt) return "JAXBElement<" + simplifyString(s.substring(lt + 1, gt)) + ">";
        }
        int lastDot = s.lastIndexOf('.');
        return lastDot > 0 ? s.substring(lastDot + 1) : s;
    }

    private static String simplifyString(String s) {
        int lastDot = s.lastIndexOf('.');
        return lastDot > 0 ? s.substring(lastDot + 1) : s;
    }

    private static String cardinality(Field f) {
        return List.class.isAssignableFrom(f.getType()) ? "0..n" : "0..1";
    }
    private static String cardinality(Type t) {
        if (t.getTypeName().startsWith("java.util.List")) return "0..n";
        return "0..1";
    }

    private static boolean isJaxbElementRef(Field f) {
        return f.getAnnotation(XmlElementRef.class) != null;
    }

    private static boolean isRequired(Field f) {
        XmlElement xe = f.getAnnotation(XmlElement.class);
        return xe != null && xe.required();
    }

    private static boolean hasField(ArrayNode fields, String name) {
        for (var n : fields) if (Objects.equals(n.get("name").asText(), name)) return true;
        return false;
    }

    private static String decap(String s) {
        return s.isEmpty() ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
