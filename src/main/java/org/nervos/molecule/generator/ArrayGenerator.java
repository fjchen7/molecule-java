package org.nervos.molecule.generator;

import com.squareup.javapoet.*;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.Objects;

public class ArrayGenerator extends AbstractConcreteGenerator {
    TypeName itemTypeName;
    TypeDescriptor itemDescriptor;
    FieldSpec itemSize;
    FieldSpec itemCount;
    FieldSpec size;

    public ArrayGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        itemDescriptor = descriptor.getFields().get(0).getTypeDescriptor();
        itemTypeName = getTypeName(itemDescriptor);
        superClassName = base.classNameArray;
    }

    @Override
    protected void fillType() {
        FieldSpec itemType = FieldSpec.builder(Class.class, "ITEM_TYPE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$T.class", itemTypeName)
                .build();

        FieldSpec.Builder itemSizeBuilder = FieldSpec.builder(int.class, "ITEM_SIZE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        if (itemTypeName == TypeName.BYTE) {
            itemSizeBuilder.initializer("1");
        } else {
            itemSizeBuilder.initializer("$T.SIZE", itemTypeName);
        }
        itemSize = itemSizeBuilder.build();

        itemCount = FieldSpec.builder(int.class, "ITEM_COUNT")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$L", descriptor.getSize())
                .build();

        size = FieldSpec.builder(int.class, "SIZE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$N * $N", itemSize, itemCount)
                .build();

        FieldSpec items = FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec methodGet = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(itemTypeName)
                .addAnnotation(Nonnull.class)
                .addParameter(int.class, "i")
                .beginControlFlow("if (i >= $N)", itemCount)
                .addStatement("throw new $T(\"Index out of range: \" + $N)", IndexOutOfBoundsException.class, itemCount)
                .endControlFlow()
                .addStatement("return $N[i]", items)
                .build();

        MethodSpec methodGetItemCount = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addStatement("return $N", itemCount)
                .build();

        MethodSpec methodGetItemSize = MethodSpec.methodBuilder("getItemSize")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addStatement("return $N", itemSize)
                .build();

        MethodSpec methodGetItemType = MethodSpec.methodBuilder("getItemType")
                .addModifiers(Modifier.PUBLIC)
                .returns(Class.class)
                .addAnnotation(Override.class)
                .addStatement("return $N", itemType)
                .build();

        typeBuilder
                .addField(itemType)
                .addField(itemSize)
                .addField(itemCount)
                .addField(size)
                .addField(items)
                .addMethod(methodGet)
                .addMethod(methodGetItemCount)
                .addMethod(methodGetItemSize)
                .addMethod(methodGetItemType);
    }

    @Override
    protected void fillTypeBuilder() {
        FieldSpec items = FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec.Builder constructorBuilder = constructorBuilder()
                .addStatement("$N = new $T[$N]", items, itemTypeName, itemCount);
        if (itemTypeName != TypeName.BYTE) {
            constructorBuilder
                    .beginControlFlow("for (int i = 0; i < $N; i++)", itemCount)
                    .addStatement("items[i] = $T.builder().build()", itemTypeName)
                    .endControlFlow();
        }

        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .beginControlFlow("if (buf.length != $N)", size)
                .addStatement("throw new $T($N, buf.length, $T.class)", base.classNameMoleculeException, size, name)
                .endControlFlow();

        if (itemTypeName != TypeName.BYTE) {
            constructorBufBuilder
                    .beginControlFlow("for (int i = 0; i < $N; i++)", itemCount)
                    .addStatement("byte[] itemBuf = Arrays.copyOfRange(buf, i * $N, (i + 1) * $N)", itemSize, itemSize)
                    .addStatement("items[i] = $T.builder(itemBuf).build()", itemTypeName)
                    .endControlFlow();
        } else {
            constructorBufBuilder.addStatement("items = buf");
        }

        MethodSpec setItem = MethodSpec.methodBuilder("setItem")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderName)
                .addParameter(int.class, "i")
                .addParameter(ParameterSpec.builder(itemTypeName, "item")
                        .addAnnotation(Nonnull.class).build())
                .addStatement("$T.requireNonNull(item)", Objects.class)
                .addStatement("items[i] = item")
                .addStatement("return this")
                .build();

        MethodSpec.Builder buildBuilder = methodBuildBuilder();

        if (itemTypeName == TypeName.BYTE) {
            buildBuilder
                    .addStatement("$T a = new $T()", name, name)
                    .addStatement("a.buf = items");
        } else {
            buildBuilder
                    .addStatement("byte[] buf = new byte[$N]", size)
                    .beginControlFlow("for (int i = 0; i < $N; i++)", itemCount)
                    .addStatement("$T.setBytes($N[i].getRawData(), buf, i * $N)", base.classNameMoleculeUtils, items, itemSize)
                    .endControlFlow()
                    .addStatement("$T a = new $T()", itemTypeName, itemTypeName)
                    .addStatement("a.buf = buf");
        }
        buildBuilder.addStatement("a.items = items").addStatement("return a");

        typeBuilderBuilder
                .addField(items)
                .addMethod(constructorBuilder.build())
                .addMethod(constructorBufBuilder.build())
                .addMethod(setItem)
                .addMethod(buildBuilder.build());
    }
}
