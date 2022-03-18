package org.nervos.molecule.generator;

import org.nervos.molecule.MoleculeType;
import org.nervos.molecule.descriptor.FieldDescriptor;
import org.nervos.molecule.descriptor.TypeDescriptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.nervos.molecule.MoleculeType.STRUCT;

public class MoleculeResolver {
    Map<String, TypeDescriptor> schemas = null;
    Path schemaPath;

    public MoleculeResolver(Path schemaPath) {
        this.schemaPath = schemaPath;
    }

    public Map<String, TypeDescriptor> resolve() throws IOException {
        String content = readFile(schemaPath);
        content = formatSchema(content);
        normalizeSchema(content);
        linkDescriptor();
        resolveSize();
        return schemas;
    }

    private String readFile(Path path) throws IOException {
        File file = path.toFile();
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }

    private String formatSchema(String schema) {
        return schema
                .replaceAll("^\\s+//.*\n", "")
                .replaceAll("\\s*//.*\n", "\n")
                .replaceAll("/\\*.*\\*/", "")
                .replaceAll("(?m)^\\s*", "")
                .replaceAll("(?m) +", " ")
                .replaceAll("(?m): ", " ")
                .replaceAll("(?m); ", ";")
                .replaceAll("(?m)(,|:|;)$", "");
    }

    private Map<String, TypeDescriptor> normalizeSchema(String content) {
        schemas = new HashMap<>();
        schemas.put(TypeDescriptor.BYTE_TYPE_DESCRIPTOR.getName(), TypeDescriptor.BYTE_TYPE_DESCRIPTOR);
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] tokens = line.split(" ");
            MoleculeType moleculeType = MoleculeType.from(tokens[0]);

            TypeDescriptor.TypeDescriptorBuilder builder = TypeDescriptor.builder();
            String name = tokens[1];
            builder.setMoleculeType(moleculeType);
            builder.setName(name);

            switch (moleculeType) {
                case ARRAY:
                    String[] innerTypeN = tokens[2].substring(1, tokens[2].length() - 1).split(";");
                    String innerType = innerTypeN[0];
                    int n = Integer.valueOf(innerTypeN[1]);
                    builder.addFieldPlaceholder("innerType", innerType);
                    builder.setSize(n); // size count
                    break;
                case VECTOR:
                case OPTION:
                    innerType = tokens[2].substring(1, tokens[2].length() - 1);
                    builder.addFieldPlaceholder("innerType", innerType);
                    break;
                case TABLE:
                case STRUCT:
                    i++;
                    while (i < lines.length && !"}".equals(lines[i])) {
                        String[] fieldToken = lines[i].split(" ");
                        builder.addFieldPlaceholder(fieldToken[0], fieldToken[1]);
                        i++;
                    }
                    if (moleculeType == STRUCT) {
                        builder.setSize(Integer.MAX_VALUE);
                    }
                    break;
                default:
                    throw new RuntimeException("Can not support Molecule Type " + moleculeType);
            }

            schemas.put(name, builder.build());
        }
        return schemas;
    }

    private void linkDescriptor() {
        for (TypeDescriptor type : schemas.values()) {
            for (FieldDescriptor field : type.getFields()) {
                String fieldTypeName = field.getTypeDescriptor().getName();
                TypeDescriptor fieldTypeDescriptor = schemas.get(fieldTypeName);
                if (fieldTypeDescriptor == null) {
                    throw new RuntimeException("Can not find TypeDescriptor of name " + fieldTypeName);
                }
                field.setTypeDescriptor(fieldTypeDescriptor);
            }
        }
    }

    private void resolveSize() {
        Map<String, Boolean> isResolved = new HashMap<>();
        for (TypeDescriptor type : schemas.values()) {
            resolveSize(type, isResolved);
        }
    }

    private void resolveSize(TypeDescriptor typeDescriptor, Map<String, Boolean> isResolved) {
        if (Boolean.TRUE.equals(isResolved.get(typeDescriptor.getName()))) {
            return;
        }
        MoleculeType moleculeType = typeDescriptor.getMoleculeType();
        switch (moleculeType) {
            case ARRAY:
                int count = typeDescriptor.getSize();
                TypeDescriptor fieldTypeDescriptor = typeDescriptor.getFields().get(0).getTypeDescriptor();
                resolveSize(fieldTypeDescriptor, isResolved);
                typeDescriptor.setSize(count * fieldTypeDescriptor.getSize());
                break;
            case STRUCT:
                int byteSize = 0;
                for (FieldDescriptor field : typeDescriptor.getFields()) {
                    fieldTypeDescriptor = field.getTypeDescriptor();
                    resolveSize(fieldTypeDescriptor, isResolved);
                    if (!fieldTypeDescriptor.isFixedType()) {
                        throw new RuntimeException(
                                "Struct can only have fixed type as field. [struct: "
                                        + typeDescriptor.getName()
                                        + ", field: "
                                        + fieldTypeDescriptor.getName()
                                        + "]");
                    }
                    if (fieldTypeDescriptor.getSize() == -1) {
                        throw new RuntimeException(
                                "Fail to determine size of fields in struct " + typeDescriptor.getName());
                    }
                    byteSize += fieldTypeDescriptor.getSize();
                }
                typeDescriptor.setSize(byteSize);
                break;
            case TABLE:
            case OPTION:
                typeDescriptor.setSize(-1);
                break;
            case VECTOR:
                fieldTypeDescriptor = typeDescriptor.getFields().get(0).getTypeDescriptor();
                resolveSize(fieldTypeDescriptor, isResolved);
                if (fieldTypeDescriptor.isFixedType()) {
                    typeDescriptor.setSize(1);
                } else {
                    typeDescriptor.setSize(-1);
                }
            case BYTE:
                break;
            default:
                throw new RuntimeException("Can not support Molecule Type " + moleculeType);
        }
        isResolved.put(typeDescriptor.getName(), true);
    }
}
