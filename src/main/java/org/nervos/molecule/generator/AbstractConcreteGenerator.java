package org.nervos.molecule.generator;

import com.squareup.javapoet.*;
import org.nervos.molecule.MoleculeType;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public abstract class AbstractConcreteGenerator extends AbstractGenerator {
    protected BaseTypeGenerator base;
    protected TypeDescriptor descriptor;

    protected TypeSpec.Builder typeBuilder;
    protected TypeSpec.Builder typeBuilderBuilder;

    protected ClassName className;
    protected ClassName builderClassName;

    protected ClassName baseTypeClassName;

    public AbstractConcreteGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        this.base = base;
        this.descriptor = descriptor;
        this.packageName = packageName;
        className = ClassName.get("", descriptor.getName());
        builderClassName = ClassName.get("", "Builder");
    }

    @Override
    public void generateAndWriteTo(Path path) throws IOException {
        newJavaFile(generate()).writeTo(path);
    }

    protected TypeSpec generate() {
        typeBuilder = TypeSpec.classBuilder(className.canonicalName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(baseTypeClassName)
                .addMethod(constructorBuilder().build());
        typeBuilderBuilder = TypeSpec.classBuilder(builderClassName.canonicalName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        fillType();

        typeBuilder
                .addMethod(MethodSpec.methodBuilder("builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(builderClassName)
                        .addStatement("return new $T()", builderClassName)
                        .build())
                .addMethod(MethodSpec.methodBuilder("builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(builderClassName)
                        .addParameter(ParameterSpec.builder(byte[].class, "buf")
                                .addAnnotation(Nonnull.class).build())
                        .addStatement("return new $T(buf)", builderClassName)
                        .build());

        fillTypeBuilder();
        typeBuilder.addType(typeBuilderBuilder.build());
        return typeBuilder.build();
    }

    protected TypeName getTypeName(TypeDescriptor descriptor) {
        if (descriptor == TypeDescriptor.BYTE_TYPE_DESCRIPTOR) {
            return TypeName.BYTE;
        }
        if (descriptor.getMoleculeType() != MoleculeType.OPTION) {
            return ClassName.get("", descriptor.getName());
        } else {
            TypeDescriptor innerTypeDescriptor = descriptor.getFields().get(0).getTypeDescriptor();
            return ClassName.get("", innerTypeDescriptor.getName());
        }
    }

    protected boolean isByte(TypeDescriptor descriptor) {
        return descriptor == TypeDescriptor.BYTE_TYPE_DESCRIPTOR;
    }

    protected MethodSpec.Builder methodBuildBuilder() {
        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC).returns(className);
    }

    protected MethodSpec.Builder constructorBuilder() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
    }

    protected MethodSpec.Builder constructorBufBuilder() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(byte[].class, "buf")
                        .addAnnotation(Nonnull.class).build())
                .addStatement("$T.requireNonNull(buf)", Objects.class);
    }

    public static String snakeCaseToCamelCase(String snakeCase) {
        String camelCase = snakeCase;
        while (camelCase.charAt(camelCase.length() - 1) == '_') {
            camelCase = camelCase.substring(0, camelCase.length() - 1);
        }
        while (camelCase.contains("_")) {
            camelCase = camelCase.replaceFirst(
                    "_[a-z]",
                    String.valueOf(Character.toUpperCase(camelCase.charAt(camelCase.indexOf("_") + 1))));
        }
        return camelCase;
    }

    public static String upperFirstChar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    abstract void fillType();

    abstract void fillTypeBuilder();
}
