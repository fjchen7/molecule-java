package org.nervos.molecule.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.nervos.molecule.MoleculeType;
import org.nervos.molecule.descriptor.FieldDescriptor;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableGenerator extends AbstractConcreteGenerator {
    FieldSpec fieldCount;
    List<FieldSpec> fields = new ArrayList<>();
    List<Boolean> isOption = new ArrayList<>();

    public TableGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        superClassName = base.classNameTable;
        for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            String fieldName = snakeCaseToCamelCase(fieldDescriptor.getName());
            TypeName fieldTypeName = getTypeName(fieldDescriptor.getTypeDescriptor());
            FieldSpec field = FieldSpec.builder(fieldTypeName, fieldName)
                    .addModifiers(Modifier.PRIVATE).build();
            fields.add(field);
            isOption.add(fieldDescriptor.getTypeDescriptor().getMoleculeType() == MoleculeType.OPTION);
        }
    }

    @Override
    protected void fillType() {
        fieldCount = FieldSpec.builder(int.class, "FIELD_COUNT")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$L", descriptor.getFields().size())
                .build();
        typeBuilder.addField(fieldCount);

        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            MethodSpec getter = MethodSpec.methodBuilder("get" + upperFirstChar(field.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(field.type)
                    .addAnnotation(isOption.get(i) ? Nullable.class : Nonnull.class)
                    .addStatement("return $L", field.name)
                    .build();
            typeBuilder.addField(field).addMethod(getter);
        }
    }

    @Override
    protected void fillTypeBuilder() {
        MethodSpec.Builder constructorBuilder = constructorBuilder();
        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .addStatement("int size = $T.littleEndianBytes4ToInt(buf, 0)", base.classNameMoleculeUtils)
                .beginControlFlow("if (buf.length != size)")
                .addStatement("throw new $T(size, buf.length, $T.class)", base.classNameMoleculeException, name)
                .endControlFlow()
                .addStatement("int[] offsets = $T.getOffsets(buf)", base.classNameMoleculeUtils)
                .beginControlFlow("if (offsets.length - 1 != $N)", fieldCount)
                .addStatement("throw new $T(\"Raw data should have \" + $N + \" but find \" + (offsets.length -1) + \" offsets in header.\")",
                        base.classNameMoleculeException, fieldCount)
                .endControlFlow()
                .addStatement("byte[] itemBuf");

        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            typeBuilderBuilder.addField(field);
            if (field.type == TypeName.BYTE) {
                constructorBufBuilder.addStatement("$L = buf[offsets[$L]]", field.name, i);
            } else {
                if (!isOption.get(i)) {
                    constructorBuilder.addStatement("$L = $T.builder().build()", field.name, field.type);
                    constructorBufBuilder
                            .addStatement("itemBuf = $T.copyOfRange(buf, offsets[$L], offsets[$L])", Arrays.class, i, i + 1)
                            .addStatement("$L = $T.builder(itemBuf).build()", field.name, field.type);
                } else {
                    constructorBuilder.addStatement("$L = null", field.name);
                    constructorBufBuilder
                            .beginControlFlow("if (offsets[$L] != offsets[$L])", i, i + 1)
                            .addStatement("itemBuf = $T.copyOfRange(buf, offsets[$L], offsets[$L])", Arrays.class, i, i + 1)
                            .addStatement("$L = $T.builder(itemBuf).build()", field.name, field.type)
                            .endControlFlow();
                }
            }
        }
        typeBuilderBuilder
                .addMethod(constructorBuilder.build())
                .addMethod(constructorBufBuilder.build());

        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            MethodSpec.Builder setterBuilder = MethodSpec.methodBuilder("set" + upperFirstChar(field.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(builderName);

            if (!isOption.get(i)) {
                setterBuilder
                        .addParameter(ParameterSpec.builder(field.type, field.name)
                                .addAnnotation(Nonnull.class).build())
                        .addStatement("$T.requireNonNull($L)", Objects.class, field.name);
            } else {
                setterBuilder.addParameter(ParameterSpec.builder(field.type, field.name)
                        .addAnnotation(Nullable.class).build());
            }
            setterBuilder
                    .addStatement("this.$L = $L", field.name, field.name)
                    .addStatement("return this")
                    .build();
            typeBuilderBuilder.addMethod(setterBuilder.build());
        }

        MethodSpec.Builder buildBuilder = methodBuildBuilder();

        buildBuilder
                .addStatement("int[] offsets = new int[$N]", fieldCount)
                .addStatement("offsets[0] = 4 + 4 * $N", fieldCount);
        for (int i = 0; i < fields.size() - 1; i++) {
            FieldSpec field = fields.get(i);
            if (field.type == TypeName.BYTE) {
                buildBuilder.addStatement("offsets[$L] = offsets[$L] + 1", i + 1, i);
            } else {
                if (!isOption.get(i)) {
                    buildBuilder.addStatement("offsets[$L] = offsets[$L] + $N.getSize()", i + 1, i, field.name);
                } else {
                    buildBuilder.addStatement("offsets[$L] = offsets[$L] + ($L == null ? 0 : $L.getSize())",
                            i + 1, i, field.name, field.name);
                }
            }
        }

        buildBuilder.addStatement("int[] fieldsSize = new int[$N]", fieldCount);
        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            if (field.type == TypeName.BYTE) {
                buildBuilder.addStatement("fieldsSize[$L] = 1", i);
            } else {
                if (!isOption.get(i)) {
                    buildBuilder.addStatement("fieldsSize[$L] = $L.getSize()", i, field.name);
                } else {
                    buildBuilder.addStatement("fieldsSize[$L] = ($L == null ? 0 : $L.getSize())", i, field.name, field.name);
                }
            }
        }

        buildBuilder.addStatement("byte[][] fieldsBuf = new byte[$N][]", fieldCount);
        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            if (field.type == TypeName.BYTE) {
                buildBuilder.addStatement("fieldsBuf[$L] = new byte[]{$L}", i, field.name);
            } else {
                if (!isOption.get(i)) {
                    buildBuilder.addStatement("fieldsBuf[$L] = $L.getRawData()", i, field.name);
                } else {
                    buildBuilder.addStatement("fieldsBuf[$L] = ($L == null ? new byte[]{} : $L.getRawData())",
                            i, field.name, field.name);
                }
            }
        }

        buildBuilder
                .addStatement("int size = 4 + 4 * $N", fieldCount)
                .beginControlFlow("for (int i = 0; i < $N; i++)", fieldCount)
                .addStatement("size += fieldsSize[i]")
                .endControlFlow()
                .addStatement("byte[] buf = new byte[size];")
                .addStatement("$T.setSize(size, buf, 0)", base.classNameMoleculeUtils);

        buildBuilder
                .addStatement("int start = 4")
                .beginControlFlow("for (int i = 0; i < $N; i++)", fieldCount)
                .addStatement("$T.setSize(fieldsSize[i], buf, start)", base.classNameMoleculeUtils)
                .addStatement("start += 4")
                .endControlFlow();

        buildBuilder
                .beginControlFlow("for (int i = 0; i < $N; i++)", fieldCount)
                .addStatement("$T.setBytes(fieldsBuf[i], buf, offsets[i])", base.classNameMoleculeUtils)
                .endControlFlow();

        buildBuilder.addStatement("$T t = new $T()", name, name).addStatement("t.buf = buf");
        for (FieldSpec field : fields) {
            buildBuilder.addStatement("t.$L = $L", field.name, field.name);
        }
        buildBuilder.addStatement("return t");

        typeBuilderBuilder.addMethod(buildBuilder.build());
    }
}
