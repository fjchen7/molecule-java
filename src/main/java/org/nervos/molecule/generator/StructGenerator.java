package org.nervos.molecule.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.nervos.molecule.descriptor.FieldDescriptor;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StructGenerator extends AbstractConcreteGenerator {
    FieldSpec size;
    FieldSpec fieldCount;
    List<FieldSpec> fields = new ArrayList<>();

    public StructGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        superClassName = base.classNameStruct;

        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            String fieldName = snakeCaseToCamelCase(fieldDescriptor.getName());
            TypeName fieldTypeName = getTypeName(fieldDescriptor.getTypeDescriptor());
            FieldSpec field = FieldSpec.builder(fieldTypeName, fieldName)
                    .addModifiers(Modifier.PRIVATE).build();
            fields.add(field);
        }
    }

    @Override
    protected void fillType() {
        size = FieldSpec.builder(int.class, "SIZE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$L", descriptor.getSize())
                .build();
        fieldCount = FieldSpec.builder(int.class, "FIELD_COUNT")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$L", descriptor.getFields().size())
                .build();
        typeBuilder.addField(size).addField(fieldCount);

        for (FieldSpec field : fields) {
            MethodSpec getter = MethodSpec.methodBuilder("get" + upperFirstChar(field.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(field.type)
                    .addAnnotation(Nonnull.class)
                    .addStatement("return $L", field.name)
                    .build();
            typeBuilder.addField(field).addMethod(getter);
        }
    }

    @Override
    protected void fillTypeBuilder() {
        MethodSpec.Builder constructorBuilder = constructorBuilder();
        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .beginControlFlow("if (buf.length != $N)", size)
                .addStatement("throw new $T($N, buf.length, $T.class)",
                        base.classNameMoleculeException, size, name)
                .endControlFlow();

        if (fields.size() > 0) {
            constructorBufBuilder.addStatement("byte[] itemBuf");
        }
        int start = 0;
        for (int i = 0; i < descriptor.getFields().size(); i++) {
            FieldSpec field = fields.get(i);
            typeBuilderBuilder.addField(field);
            int end = start + descriptor.getFields().get(i).getTypeDescriptor().getSize();
            if (field.type == TypeName.BYTE) {
                constructorBufBuilder.addStatement("$L = buf[$L]", field.name, start);
            } else {
                constructorBuilder.addStatement("$L = $T.builder().build()", field.name, field.type);
                constructorBufBuilder
                        .addStatement("itemBuf = $T.copyOfRange(buf, $L, $L)", Arrays.class, start, end)
                        .addStatement("$L = $T.builder(itemBuf).build()", field.name, field.type);
            }
            start = end;
        }
        typeBuilderBuilder
                .addMethod(constructorBuilder.build())
                .addMethod(constructorBufBuilder.build());

        for (FieldSpec field : fields) {
            MethodSpec setter = MethodSpec.methodBuilder("set" + upperFirstChar(field.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(builderName)
                    .addParameter(ParameterSpec.builder(field.type, field.name)
                            .addAnnotation(Nonnull.class)
                            .build())
                    .addStatement("$T.requireNonNull($L)", Objects.class, field.name)
                    .addStatement("this.$L = $L", field.name, field.name)
                    .addStatement("return this")
                    .build();
            typeBuilderBuilder.addMethod(setter);
        }

        MethodSpec.Builder buildBuilder = methodBuildBuilder();
        if (fields.size() > 0) {
            buildBuilder
                    .addStatement("int[] offsets = new int[$N]", fieldCount)
                    .addStatement("offsets[0] = 0");
        }
        for (int i = 1; i < descriptor.getFields().size(); i++) {
            FieldSpec field = fields.get(i - 1);
            if (field.type == TypeName.BYTE) {
                buildBuilder.addStatement("offsets[$L] = offsets[$L] + 1", i, i - 1);
            } else {
                buildBuilder.addStatement("offsets[$L] = offsets[$L] + $T.SIZE", i, i - 1, field.type);
            }
        }
        buildBuilder.addStatement("byte[] buf = new byte[$N]", size);
        for (int i = 0; i < descriptor.getFields().size(); i++) {
            FieldSpec field = fields.get(i);
            if (field.type == TypeName.BYTE) {
                buildBuilder.addStatement("buf[offsets[$L]] = $L", i, field.name);
            } else {
                buildBuilder.addStatement("$T.setBytes($L.getRawData(), buf, offsets[$L])",
                        base.classNameMoleculeUtils, field.name, i);
            }
        }
        buildBuilder.addStatement("$T s = new $T()", name, name)
                .addStatement("s.buf = buf");
        for (FieldSpec field : fields) {
            buildBuilder.addStatement("s.$L = $L", field.name, field.name);
        }
        buildBuilder.addStatement("return s");

        typeBuilderBuilder.addMethod(buildBuilder.build());
    }
}
