package org.nervos.molecule.generator;

import com.squareup.javapoet.*;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.Objects;

public abstract class VectorGenerator extends AbstractConcreteGenerator {
    protected TypeName itemTypeName;
    protected TypeDescriptor itemDescriptor;
    protected boolean isItemOption;

    public VectorGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);
        itemDescriptor = descriptor.getFields().get(0).getTypeDescriptor();
        itemTypeName = getTypeName(itemDescriptor);
    }

    @Override
    protected void fillType() {
        FieldSpec itemType = FieldSpec.builder(Class.class, "ITEM_TYPE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$T.class", itemTypeName)
                .build();
        FieldSpec items = FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                .addModifiers(Modifier.PRIVATE)
                .build();
        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(itemTypeName)
                .addAnnotation(isItemOption ? Nullable.class : Nonnull.class)
                .addParameter(int.class, "i")
                .beginControlFlow("if (i >= $N.length)", items)
                .addStatement("throw new $T(\"Index out of range: \" + $N.length)", IndexOutOfBoundsException.class, items)
                .endControlFlow()
                .addStatement("return $N[i]", items)
                .build();

        MethodSpec getItemCount = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addStatement("return $N.length", items)
                .build();

        MethodSpec getItemType = MethodSpec.methodBuilder("getItemType")
                .addModifiers(Modifier.PUBLIC)
                .returns(Class.class)
                .addAnnotation(Override.class)
                .addStatement("return $N", itemType)
                .build();

        typeBuilder
                .addField(itemType)
                .addField(items)
                .addMethod(get)
                .addMethod(getItemCount)
                .addMethod(getItemType);
    }

    @Override
    protected void fillTypeBuilder() {

        fillTypeBuilderConstructor();

        MethodSpec.Builder methodAddBuilder = MethodSpec.methodBuilder("add")
                .addModifiers(Modifier.PUBLIC).returns(builderName);
        if (!isItemOption) {
            methodAddBuilder
                    .addParameter(ParameterSpec.builder(itemTypeName, "item")
                            .addAnnotation(Nonnull.class).build())
                    .addStatement("$T.requireNonNull(item)", Objects.class);
        } else {
            methodAddBuilder.addParameter(itemTypeName, "item");
        }
        methodAddBuilder
                .addStatement("$T[] tempItems = new $T[items.length + 1]", itemTypeName, itemTypeName)
                .addStatement("$T.arraycopy(items, 0, tempItems, 0, items.length)", System.class)
                .addStatement("tempItems[items.length] = item;")
                .addStatement("items = tempItems")
                .addStatement("return this");
        MethodSpec methodAdd = methodAddBuilder.build();

        MethodSpec.Builder methodSetBuilder = MethodSpec.methodBuilder("set")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderName)
                .addParameter(int.class, "i");
        if (!isItemOption) {
            methodSetBuilder
                    .addParameter(ParameterSpec.builder(itemTypeName, "item")
                            .addAnnotation(Nonnull.class).build())
                    .addStatement("$T.requireNonNull(item)", Objects.class);
        } else {
            methodSetBuilder.addParameter(itemTypeName, "item");
        }
        MethodSpec methodSet = methodSetBuilder
                .beginControlFlow("if (i >= items.length)")
                .addStatement("throw new $T(\"Index out of range: \" + items.length)", IndexOutOfBoundsException.class)
                .endControlFlow()
                .addStatement("items[i] = item")
                .addStatement("return this")
                .build();

        MethodSpec methodRemove = MethodSpec.methodBuilder("remove")
                .addModifiers(Modifier.PUBLIC)
                .returns(builderName)
                .addParameter(int.class, "i")
                .beginControlFlow("if (i >= items.length)")
                .addStatement("throw new $T(\"Index out of range: \" + items.length)", IndexOutOfBoundsException.class)
                .endControlFlow()
                .addStatement("$T[] tempItems = new $T[items.length - 1]", itemTypeName, itemTypeName)
                .addStatement("$T.arraycopy(items, 0, tempItems, 0, i)", System.class)
                .addStatement("$T.arraycopy(items, i + 1, tempItems, i, items.length - i -1)", System.class)
                .addStatement("items = tempItems")
                .addStatement("return this")
                .build();

        typeBuilderBuilder
                .addField(FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(methodAdd)
                .addMethod(methodSet)
                .addMethod(methodRemove);

        fillTypeBuilderMethodBuild();
    }

    abstract void fillTypeBuilderConstructor();

    abstract void fillTypeBuilderMethodBuild();
}
