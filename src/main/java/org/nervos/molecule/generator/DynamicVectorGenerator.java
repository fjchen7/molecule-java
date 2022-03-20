package org.nervos.molecule.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.nervos.molecule.MoleculeType;
import org.nervos.molecule.descriptor.TypeDescriptor;

import java.util.Arrays;

public class DynamicVectorGenerator extends VectorGenerator {
    private boolean isItemBoxByte;

    public DynamicVectorGenerator(
            BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        superClassName = base.classNameDynamicVector;
        isItemOption = itemDescriptor.getMoleculeType() == MoleculeType.OPTION;
        isItemBoxByte = itemTypeName == TypeName.BYTE.box();
    }

    @Override
    protected void fillType() {
        super.fillType();
    }

    @Override
    void fillTypeBuilderConstructor() {
        MethodSpec constructor = constructorBuilder()
                .addStatement("items = new $T[0]", itemTypeName).build();

        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .addStatement("int size = $T.littleEndianBytes4ToInt(buf, 0)", base.classNameMoleculeUtils)
                .beginControlFlow("if (buf.length != size)")
                .addStatement("throw new $T(size, buf.length, $T.class)", base.classNameMoleculeException, name)
                .endControlFlow();

        constructorBufBuilder
                .addStatement("int[] offsets = $T.getOffsets(buf)", base.classNameMoleculeUtils)
                .addStatement("items = new $T[offsets.length - 1]", itemTypeName);

        constructorBufBuilder
                .beginControlFlow("for (int i = 0; i < items.length; i++)")
                .addStatement("byte[] itemBuf = $T.copyOfRange(buf, offsets[i], offsets[i + 1])", Arrays.class);

        if (isItemOption) {
            if (isItemBoxByte) {
                constructorBufBuilder
                        .addStatement("items[i] = (itemBuf.length == 0 ? null: buf[0])", itemTypeName);
            } else {
                constructorBufBuilder
                        .addStatement("items[i] = (itemBuf.length == 0 ? null: $T.builder(itemBuf).build())", itemTypeName);
            }
        } else {
            constructorBufBuilder
                    .addStatement("items[i] = $T.builder(itemBuf).build()", itemTypeName);
        }
        constructorBufBuilder.endControlFlow();

        typeBuilderBuilder
                .addMethod(constructor)
                .addMethod(constructorBufBuilder.build());
    }

    @Override
    void fillTypeBuilderMethodBuild() {
        MethodSpec.Builder buildBuilder = methodBuildBuilder()
                .addStatement("int size = 4 + 4 * items.length")
                .beginControlFlow("for (int i = 0; i < items.length; i++)");

        if (isItemOption) {
            buildBuilder.beginControlFlow("if (items[i] != null)");
            if (isItemBoxByte) {
                buildBuilder.addStatement("size += 1");
            } else {
                buildBuilder.addStatement("size += items[i].getSize()");
            }
            buildBuilder.endControlFlow();
        } else {
            buildBuilder.addStatement("size += items[i].getSize()");
        }
        buildBuilder.endControlFlow();

        buildBuilder
                .addStatement("byte[] buf = new byte[size]")
                .addStatement("$T.setSize(size, buf, 0);", base.classNameMoleculeUtils);

        buildBuilder
                .addStatement("int offset = 4 + 4 * items.length")
                .addStatement("int start = 4")
                .beginControlFlow("for (int i = 0; i < items.length; i++)")
                .addStatement("$T.setSize(offset, buf, start);", base.classNameMoleculeUtils);
        if (isItemOption) {
            buildBuilder.beginControlFlow("if (items[i] != null)");
            if (isItemBoxByte) {
                buildBuilder.addStatement("offset += 1");
            } else {
                buildBuilder.addStatement("offset += items[i].getSize()");
            }
            buildBuilder.endControlFlow();
        } else {
            buildBuilder.addStatement("offset += items[i].getSize()");
        }
        buildBuilder
                .addStatement("start += 4")
                .endControlFlow();

        buildBuilder
                .beginControlFlow("for (int i = 0; i < items.length; i++)");

        if (isItemOption) {
            buildBuilder.beginControlFlow("if (items[i] != null)");
            if (isItemBoxByte) {
                buildBuilder
                        .addStatement("$T.setBytes(new byte[]{items[i]}, buf, start);", base.classNameMoleculeUtils)
                        .addStatement("start += 1");
            } else {
                buildBuilder
                        .addStatement("$T.setBytes(items[i].getRawData(), buf, start);", base.classNameMoleculeUtils)
                        .addStatement("start += items[i].getSize()");
            }
            buildBuilder.endControlFlow();
        } else {
            buildBuilder
                    .addStatement("$T.setBytes(items[i].getRawData(), buf, start);", base.classNameMoleculeUtils)
                    .addStatement("start += items[i].getSize()");
        }
        buildBuilder.endControlFlow();

        buildBuilder
                .addStatement("$T v = new $T()", name, name)
                .addStatement("v.buf = buf")
                .addStatement("v.items = items")
                .addStatement("return v");

        typeBuilderBuilder.addMethod(buildBuilder.build());
    }
}
