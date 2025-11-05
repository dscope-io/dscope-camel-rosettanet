package io.dscope.tools.karavan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.classgraph.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates Karavan model metadata for JAXB classes under io.dscope.rosettanet.interchange.
 * Emits one JSON file per *RequestType/*ResponseType into karavan/metadata/model.
 */
public class RosettaNetKaravanModelGenerator {

    private static final String ROOT_PKG = "io.dscope.rosettanet.interchange";
    private static final String OUT_DIR  = "src/main/resources/karavan/metadata/model";

    public static void main(String[] args) throws Exception {
        ObjectMapper om = new ObjectMapper();
        new File(OUT_DIR).mkdirs();

        try (ScanResult scan = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(ROOT_PKG)
                .scan()) {

            List<ClassInfo> candidates = scan.getAllClasses().stream()
                    .filter(ci -> !ci.isAbstract() && !ci.isInterface())
                    .filter(ci -> ci.getSimpleName().endsWith("RequestType") || ci.getSimpleName().endsWith("ResponseType"))
                    .collect(Collectors.toList());

            for (ClassInfo ci : candidates) {
                Class<?> model = ci.loadClass();
                ObjectNode root = om.createObjectNode();
                root.put("model", model.getSimpleName());
                root.put("package", model.getPackageName());
                root.put("qualifiedName", model.getName());

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

                String fileName = OUT_DIR + "/" + model.getSimpleName() + ".json";
                om.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), root);
                System.out.println("Wrote " + fileName);
            }
        }
    }

    private static String simplify(Type t) {
        String s = t.getTypeName();
        // List<...>
        if (s.startsWith("java.util.List")) {
            int lt = s.indexOf('<'), gt = s.lastIndexOf('>');
            if (lt > 0 && gt > lt) {
                return "List<" + simplify(s.substring(lt + 1, gt)) + ">";
            }
        }
        // JAXBElement
        if (s.startsWith("jakarta.xml.bind.JAXBElement")) {
            int lt = s.indexOf('<'), gt = s.lastIndexOf('>');
            if (lt > 0 && gt > lt) return "JAXBElement<" + simplify(s.substring(lt + 1, gt)) + ">";
        }
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
