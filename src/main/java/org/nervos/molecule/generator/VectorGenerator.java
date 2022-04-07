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
    FieldSpec itemType =
        FieldSpec.builder(Class.class, "ITEM_TYPE")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .initializer("$T.class", itemTypeName)
            .build();
    FieldSpec items =
        FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
            .addModifiers(Modifier.PRIVATE)
            .build();
    MethodSpec get =
        MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC)
            .returns(itemTypeName)
            .addAnnotation(isItemOption ? Nullable.class : Nonnull.class)
            .addParameter(int.class, "i")
            .addStatement("return $N[i]", items)
            .build();

    MethodSpec getItemCount =
        MethodSpec.methodBuilder("getItemCount")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addAnnotation(Override.class)
            .addStatement("return $N.length", items)
            .build();

    MethodSpec getItemType =
        MethodSpec.methodBuilder("getItemType")
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

    MethodSpec.Builder methodAddBuilder =
        MethodSpec.methodBuilder("add").addModifiers(Modifier.PUBLIC).returns(builderName);
    if (!isItemOption) {
      methodAddBuilder
          .addParameter(
              ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nonnull.class).build())
          .addStatement("$T.requireNonNull(item)", Objects.class);
    } else {
      methodAddBuilder.addParameter(
          ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nullable.class).build());
    }
    methodAddBuilder
        .addStatement("$T[] originalItems = items", itemTypeName)
        .addStatement("items = new $T[originalItems.length + 1]", itemTypeName)
        .addStatement("$T.arraycopy(originalItems, 0, items, 0, originalItems.length)", System.class)
        .addStatement("items[items.length - 1] = item;")
        .addStatement("return this");
    MethodSpec methodAdd = methodAddBuilder.build();

    MethodSpec.Builder methodAddBatchBuilder =
        MethodSpec.methodBuilder("add")
            .addModifiers(Modifier.PUBLIC)
            .returns(builderName)
            .addParameter(
                ParameterSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                    .addAnnotation(Nonnull.class).build());

    methodAddBatchBuilder
        .addStatement("$T.requireNonNull(items)", Objects.class)
        .addStatement("$T[] originalItems = this.items", itemTypeName)
        .addStatement("this.items = new $T[originalItems.length + items.length]", itemTypeName)
        .addStatement("$T.arraycopy(originalItems, 0, this.items, 0, originalItems.length)", System.class)
        .addStatement("$T.arraycopy(items, 0, this.items, originalItems.length, items.length)", System.class)
        .addStatement("return this");
    MethodSpec methodAddBatch = methodAddBatchBuilder.build();

    MethodSpec.Builder methodSetBuilder =
        MethodSpec.methodBuilder("set")
            .addModifiers(Modifier.PUBLIC)
            .returns(builderName)
            .addParameter(int.class, "i");
    if (!isItemOption) {
      methodSetBuilder
          .addParameter(
              ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nonnull.class).build())
          .addStatement("$T.requireNonNull(item)", Objects.class);
    } else {
      methodSetBuilder.addParameter(
          ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nullable.class).build());
    }
    MethodSpec methodSet =
        methodSetBuilder.addStatement("items[i] = item").addStatement("return this").build();

    MethodSpec methodRemove =
        MethodSpec.methodBuilder("remove")
            .addModifiers(Modifier.PUBLIC)
            .returns(builderName)
            .addParameter(int.class, "i")
            .beginControlFlow("if (i < 0 || i >= items.length)")
            .addStatement("throw new $T(i)", ArrayIndexOutOfBoundsException.class)
            .endControlFlow()
            .addStatement("$T[] originalItems = items", itemTypeName)
            .addStatement("items = new $T[originalItems.length - 1]", itemTypeName)
            .addStatement("$T.arraycopy(originalItems, 0, items, 0, i)", System.class)
            .addStatement(
                "$T.arraycopy(originalItems, i + 1, items, i, originalItems.length - i -1)", System.class)
            .addStatement("return this")
            .build();

    typeBuilderBuilder
        .addField(
            FieldSpec.builder(ArrayTypeName.of(itemTypeName), "items")
                .addModifiers(Modifier.PRIVATE)
                .build())
        .addMethod(methodAdd)
        .addMethod(methodAddBatch)
        .addMethod(methodSet)
        .addMethod(methodRemove);

    fillTypeBuilderMethodBuild();
  }

  abstract void fillTypeBuilderConstructor();

  abstract void fillTypeBuilderMethodBuild();
}
