package org.nervos.molecule.generator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BaseTypeGenerator extends AbstractGenerator {

    public ClassName classNameMolecule;
    public ClassName classNameArray;
    public ClassName classNameStruct;
    public ClassName classNameTable;
    public ClassName classNameVector;
    public ClassName classNameDynamicVector;
    public ClassName classNameFixedVector;
    public ClassName classNameUnion;
    public ClassName classNameOption;
    public ClassName classNameMoleculeException;
    public ClassName classNameMoleculeUtils;

    public BaseTypeGenerator(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void generateAndWriteTo(Path path) throws IOException {
        List<TypeSpec> typeSpecs = generate();
        for (TypeSpec typeSpec : typeSpecs) {
            newJavaFile(typeSpec).writeTo(path);
        }
    }

    public List<TypeSpec> generate() {
        List<TypeSpec> typeSpecs = new ArrayList<>();
        typeSpecs.add(generateBaseMolecule());
        typeSpecs.add(generateBaseArray());
        typeSpecs.add(generateBaseStruct());
        typeSpecs.add(generateBaseVector());
        typeSpecs.add(generateBaseDynamicVector());
        typeSpecs.add(generateBaseFixedVector());
        typeSpecs.add(generateBaseTable());
        typeSpecs.add(generateBaseUnion());
        typeSpecs.add(generateBaseOption());
        typeSpecs.add(generateMoleculeException());
        typeSpecs.add(generateMoleculeUtils());
        return typeSpecs;
    }

    private TypeSpec generateBaseMolecule() {
        TypeSpec molecule = TypeSpec.classBuilder("Molecule")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addField(FieldSpec.builder(byte[].class, "buf")
                        .addModifiers(Modifier.PROTECTED).build())
                .addMethod(MethodSpec.methodBuilder("getRawData")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(byte[].class)
                        .addStatement("return buf")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getSize")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(int.class)
                        .addStatement("return getRawData().length")
                        .build())
                .build();
        classNameMolecule = ClassName.get(packageName, molecule.name);
        return molecule;
    }

    private TypeSpec generateBaseArray() {
        TypeSpec array = TypeSpec.classBuilder("Array")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .addMethod(MethodSpec.methodBuilder("getItemCount")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(int.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getItemSize")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(int.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getItemType")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(Class.class)
                        .build())
                .build();
        classNameArray = ClassName.get(packageName, array.name);
        return array;
    }

    private TypeSpec generateBaseStruct() {
        TypeSpec struct = TypeSpec.classBuilder("Struct")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .build();
        classNameStruct = ClassName.get(packageName, struct.name);
        return struct;
    }

    private TypeSpec generateBaseTable() {
        TypeSpec table = TypeSpec.classBuilder("Table")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .build();
        classNameTable = ClassName.get(packageName, table.name);
        return table;
    }

    private TypeSpec generateBaseVector() {
        TypeSpec vector = TypeSpec.classBuilder("Vector")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .addMethod(MethodSpec.methodBuilder("getItemCount")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(int.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getItemType")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(Class.class)
                        .build())
                .build();
        classNameVector = ClassName.get(packageName, vector.name);
        return vector;
    }

    private TypeSpec generateBaseDynamicVector() {
        TypeSpec dynamicVector = TypeSpec.classBuilder("DynamicVector")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameVector)
                .build();
        classNameDynamicVector = ClassName.get(packageName, dynamicVector.name);
        return dynamicVector;
    }

    private TypeSpec generateBaseFixedVector() {
        TypeSpec fixedVector = TypeSpec.classBuilder("FixedVector")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameVector)
                .build();
        classNameFixedVector = ClassName.get(packageName, fixedVector.name);
        return fixedVector;
    }

    private TypeSpec generateBaseUnion() {
        TypeSpec union = TypeSpec.classBuilder("Union")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .addField(FieldSpec.builder(int.class, "typeId")
                        .addModifiers(Modifier.PROTECTED).build())
                .addField(FieldSpec.builder(Object.class, "item")
                        .addModifiers(Modifier.PROTECTED).build())
                .addMethod(MethodSpec.methodBuilder("getTypeId")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(int.class)
                        .addStatement("return this.typeId")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getItem")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Object.class)
                        .addStatement("return this.item")
                        .build())
                .build();
        classNameUnion = ClassName.get(packageName, union.name);
        return union;
    }

    private TypeSpec generateBaseOption() {
        TypeVariableName t = TypeVariableName.get("T");
        TypeSpec option = TypeSpec.classBuilder("Option")
                .addTypeVariable(t)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(classNameMolecule)
                .addField(FieldSpec.builder(t, "item")
                        .addModifiers(Modifier.PROTECTED)
                        .build())
                .addMethod(MethodSpec.methodBuilder("isNull")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(boolean.class)
                        .addStatement("return item == null")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getItem")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(t)
                        .addStatement("return item")
                        .build())
                .build();
        classNameOption = ClassName.get(packageName, option.name);
        return option;
    }

    public TypeSpec generateMoleculeException() {
        TypeSpec moleculeException = TypeSpec.classBuilder("MoleculeException")
                .addModifiers(Modifier.PUBLIC)
                .superclass(IllegalArgumentException.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, "message")
                        .addStatement("super(message)")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(int.class, "expectedLength")
                        .addParameter(int.class, "actualLength")
                        .addParameter(Class.class, "clazz")
                        .addStatement("super(String.format(\n"
                                + "\"Expect %d-byte but receive %d-byte raw data for molecule class %s.\",\n"
                                + "expectedLength, actualLength, clazz.getSimpleName()))")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Throwable.class, "cause")
                        .addStatement("super(cause)")
                        .build())
                .build();
        classNameMoleculeException = ClassName.get(packageName, moleculeException.name);
        return moleculeException;
    }

    public TypeSpec generateMoleculeUtils() {
        MethodSpec setBytes = MethodSpec.methodBuilder("setBytes")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(byte[].class, "from")
                .addParameter(byte[].class, "to")
                .addParameter(int.class, "start")
                .addStatement("$T.arraycopy(from, 0, to, start, from.length)", System.class)
                .build();

        MethodSpec setInt = MethodSpec.methodBuilder("setInt")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(int.class, "size")
                .addParameter(byte[].class, "to")
                .addParameter(int.class, "start")
                .addStatement("byte[] from = new byte[4]")
                .addStatement("from[3] = (byte) ((size >> 24) & 0xff)")
                .addStatement("from[2] = (byte) ((size >> 16) & 0xff)")
                .addStatement("from[1] = (byte) ((size >> 8) & 0xff)")
                .addStatement("from[0] = (byte) (size & 0xff)")
                .addStatement("$N(from, to, start)", setBytes)
                .build();

        MethodSpec littleEndianBytes4ToInt = MethodSpec.methodBuilder("littleEndianBytes4ToInt")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(int.class)
                .addParameter(byte[].class, "buf")
                .addParameter(int.class, "start")
                .addStatement("return ((0xFF & buf[start + 3]) << 24)\n"
                        + "| ((0xFF & buf[start + 2]) << 16)\n"
                        + "| ((0xFF & buf[start + 1]) << 8)\n"
                        + "| (0xFF & buf[start])")
                .build();

        MethodSpec getOffsets = MethodSpec.methodBuilder("getOffsets")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(int[].class)
                .addParameter(byte[].class, "buf")
                .beginControlFlow("if (buf.length < 4)")
                .addStatement("throw new $T(\"Byte length is less than 4.\")",
                        this.classNameMoleculeException)
                .endControlFlow()
                .beginControlFlow("if (buf.length == 4)")
                .addStatement("return new int[]{4}")
                .endControlFlow()
                .addStatement("int headerEnd = $N(buf, 4)", littleEndianBytes4ToInt)
                .beginControlFlow("if (headerEnd % 4 != 0)")
                .addStatement("throw new $T(\"Byte length of header in raw data should be multiples of 4.\")",
                        this.classNameMoleculeException)
                .endControlFlow()
                .addStatement("int count = headerEnd / 4 - 1")
                .addStatement("int[] offsets = new int[count + 1]")
                .beginControlFlow("for (int i = 0; i < count; i++)")
                .addStatement("offsets[i] = $N(buf, 4 + i * 4)", littleEndianBytes4ToInt)
                .endControlFlow()
                .addStatement("offsets[count] = buf.length")
                .addStatement("return offsets")
                .build();

        TypeSpec moleculeUtils = TypeSpec.classBuilder("MoleculeUtils")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(setBytes)
                .addMethod(setInt)
                .addMethod(littleEndianBytes4ToInt)
                .addMethod(getOffsets)
                .build();
        classNameMoleculeUtils = ClassName.get(packageName, moleculeUtils.name);

        return moleculeUtils;
    }
}
