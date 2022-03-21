package org.nervos.molecule.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.lang.model.element.Modifier;
import java.util.Arrays;

public class FixedVectorGenerator extends VectorGenerator {
    FieldSpec itemSize;

    public FixedVectorGenerator(
            BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        superClassName = base.classNameFixedVector;

        isItemOption = false;
    }

    @Override
    protected void fillType() {
        FieldSpec.Builder itemSizeBuilder = FieldSpec.builder(int.class, "ITEM_SIZE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        if (itemTypeName == TypeName.BYTE) {
            itemSizeBuilder.initializer("1");
        } else {
            itemSizeBuilder.initializer("$T.SIZE", itemTypeName);
        }
        itemSize = itemSizeBuilder.build();
//
//        MethodSpec methodGetItemSize = MethodSpec.methodBuilder("getItemSize")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(int.class)
//                .addAnnotation(Override.class)
//                .addStatement("return $N", itemSize)
//                .build();

        typeBuilder.addField(itemSize)
                .addMethod(MethodSpec.methodBuilder("getItemSize")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(int.class)
                        .addAnnotation(Override.class)
                        .addStatement("return $N", itemSize)
                        .build());
        super.fillType();
    }

    @Override
    void fillTypeBuilderConstructor() {
        MethodSpec constructor = constructorBuilder()
                .addStatement("this.items = new $T[0]", itemTypeName).build();

        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .addStatement("int itemCount = $T.littleEndianBytes4ToInt(buf, 0)", base.classNameMoleculeUtils)
                .addStatement("int size = 4 + itemCount * $N", itemSize)
                .beginControlFlow("if (buf.length != size)")
                .addStatement("throw new $T(size, buf.length, $T.class)", base.classNameMoleculeException, name)
                .endControlFlow();
        if (itemTypeName != TypeName.BYTE) {
            constructorBufBuilder
                    .addStatement("int start = 4")
                    .addStatement("items = new $T[itemCount]", itemTypeName)
                    .beginControlFlow("for (int i = 0; i < itemCount; i++)")
                    .addStatement("byte[] itemBuf = $T.copyOfRange(buf, start, start + $N)", Arrays.class, itemSize)
                    .addStatement("items[i] = $T.builder(itemBuf).build()", itemTypeName)
                    .addStatement("start += $N", itemSize)
                    .endControlFlow();
        } else {
            constructorBufBuilder
                    .addStatement("items = $T.copyOfRange(buf, 4, buf.length)", Arrays.class);
        }

        typeBuilderBuilder
                .addMethod(constructor)
                .addMethod(constructorBufBuilder.build());
    }

    @Override
    void fillTypeBuilderMethodBuild() {
        MethodSpec.Builder buildBuilder = methodBuildBuilder()
                .addStatement("byte[] buf = new byte[4 + items.length * $N]", itemSize)
                .addStatement("$T.setInt(items.length, buf, 0);", base.classNameMoleculeUtils);
        if (itemTypeName != TypeName.BYTE) {
            buildBuilder
                    .addStatement("int start = 4")
                    .beginControlFlow("for (int i = 0; i < items.length; i++)")
                    .addStatement("$T.setBytes(items[i].getRawData(), buf, start)", base.classNameMoleculeUtils)
                    .addStatement("start += $N", itemSize)
                    .endControlFlow();
        } else {
            buildBuilder.addStatement("$T.setBytes(items, buf, 4)", base.classNameMoleculeUtils);
        }

        buildBuilder
                .addStatement("$T v = new $T()", name, name)
                .addStatement("v.buf = buf")
                .addStatement("v.items = items")
                .addStatement("return v");

        typeBuilderBuilder.addMethod(buildBuilder.build());
    }
}
