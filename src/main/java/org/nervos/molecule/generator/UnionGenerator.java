package org.nervos.molecule.generator;

import com.squareup.javapoet.*;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import org.nervos.molecule.MoleculeType;
import org.nervos.molecule.descriptor.FieldDescriptor;
import org.nervos.molecule.descriptor.TypeDescriptor;

public class UnionGenerator extends AbstractConcreteGenerator {
  FieldSpec itemClasses;
  List<TypeDescriptor> fieldTypeDescriptors;

  public UnionGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
    super(base, descriptor, packageName);
    superClassName = base.classNameUnion;
    fieldTypeDescriptors = new ArrayList<>();
    for (int i = 0; i < descriptor.getFields().size(); i++) {
      fieldTypeDescriptors.add(descriptor.getFields().get(i).getTypeDescriptor());
    }
  }

  @Override
  protected void fillType() {
    itemClasses =
        FieldSpec.builder(ParameterizedTypeName.get(List.class, Class.class), "ITEM_CLASSES")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .initializer("new $T<>()", ArrayList.class)
            .build();

    CodeBlock.Builder staticBlockBuilder = CodeBlock.builder();

    for (FieldDescriptor fieldDescriptor : descriptor.getFields()) {
      TypeDescriptor fieldTypeDescriptor = fieldDescriptor.getTypeDescriptor();
      TypeName fieldTypeName = getTypeName(fieldTypeDescriptor);
      staticBlockBuilder.addStatement("$N.add($T.class)", itemClasses, fieldTypeName);
    }
    staticBlockBuilder.addStatement(
        "$N = $T.unmodifiableList($N)", itemClasses, Collections.class, itemClasses);

    typeBuilder.addField(itemClasses).addStaticBlock(staticBlockBuilder.build());
  }

  @Override
  protected void fillTypeBuilder() {
    FieldSpec typeId =
        FieldSpec.builder(int.class, "typeId").addModifiers(Modifier.PRIVATE).build();
    FieldSpec item = FieldSpec.builder(Object.class, "item").addModifiers(Modifier.PRIVATE).build();

    typeBuilderBuilder.addField(typeId).addField(item);

    MethodSpec.Builder constructorBuilder = constructorBuilder().addStatement("$N = 0", typeId);

    TypeDescriptor td = fieldTypeDescriptors.get(0);
    if (td.getMoleculeType() == MoleculeType.OPTION) {
      constructorBuilder.addStatement("item = null");
    } else if (td == TypeDescriptor.BYTE_TYPE_DESCRIPTOR) {
      constructorBuilder.addStatement("item = (byte) 0");
    } else {
      TypeName typeName = getTypeName(td);
      constructorBuilder.addStatement("$N = $T.builder().build()", item, typeName);
    }

    MethodSpec.Builder constructorBufBuilder =
        constructorBufBuilder()
            .addStatement(
                "$N = $T.littleEndianBytes4ToInt(buf, 0)", typeId, base.classNameMoleculeUtils)
            .beginControlFlow("if ($N >= $N.size())", typeId, itemClasses)
            .addStatement(
                "throw new $T(\"Type Id of Union type $T should range from 0 to \" + ($N.size() -1))",
                base.classNameMoleculeException,
                name,
                itemClasses)
            .endControlFlow()
            .addStatement("byte[] itemBuf = new byte[buf.length - 4]")
            .addStatement("$T.arraycopy(buf, 4, itemBuf, 0, buf.length - 4)", System.class);

    for (int i = 0; i < fieldTypeDescriptors.size(); i++) {
      if (i == 0) {
        constructorBufBuilder.beginControlFlow("if ($N == $L)", typeId, i);
      } else {
        constructorBufBuilder.nextControlFlow("else if ($N == $L)", typeId, i);
      }
      TypeDescriptor field = fieldTypeDescriptors.get(i);
      TypeName typeName = getTypeName(field);
      if (field.getMoleculeType() == MoleculeType.OPTION) {
        constructorBufBuilder
            .beginControlFlow("if (itemBuf.length == 0)")
            .addStatement("$N = null", item)
            .nextControlFlow("else");
        if (typeName == TypeName.BYTE.box()) {
          constructorBufBuilder
              .beginControlFlow("if (itemBuf.length == 1)")
              .addStatement("item = itemBuf[0]")
              .nextControlFlow("else")
              .addStatement(
                  "throw new $T(1, itemBuf.length, Byte.class)", base.classNameMoleculeException)
              .endControlFlow();
        } else {
          constructorBufBuilder.addStatement("$N = $T.builder(itemBuf).build()", item, typeName);
        }
        constructorBufBuilder.endControlFlow();
      } else {
        if (typeName == TypeName.BYTE) {
          constructorBufBuilder
              .beginControlFlow("if (itemBuf.length == 1)")
              .addStatement("item = itemBuf[0]")
              .nextControlFlow("else")
              .addStatement(
                  "throw new $T(1, itemBuf.length, byte.class)", base.classNameMoleculeException)
              .endControlFlow();
        } else {
          constructorBufBuilder.addStatement("$N = $T.builder(itemBuf).build()", item, typeName);
        }
      }
      if (i == fieldTypeDescriptors.size() - 1) {
        constructorBufBuilder.endControlFlow();
      }
    }

    typeBuilderBuilder
        .addMethod(constructorBuilder.build())
        .addMethod(constructorBufBuilder.build());

    for (int i = 0; i < fieldTypeDescriptors.size(); i++) {
      TypeDescriptor itemTypeDescriptor = fieldTypeDescriptors.get(i);

      String itemName = snakeCaseToCamelCase(itemTypeDescriptor.getName());
      TypeName itemTypeName = getTypeName(itemTypeDescriptor);

      MethodSpec.Builder setterBuilder =
          MethodSpec.methodBuilder("to" + upperFirstChar(itemName))
              .addModifiers(Modifier.PUBLIC)
              .returns(builderName);

      if (!(fieldTypeDescriptors.get(i).getMoleculeType() == MoleculeType.OPTION)) {
        setterBuilder
            .addParameter(
                ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nonnull.class).build())
            .addStatement("$T.requireNonNull(item)", Objects.class);
      } else {
        setterBuilder.addParameter(
            ParameterSpec.builder(itemTypeName, "item").addAnnotation(Nullable.class).build());
      }
      setterBuilder
          .addStatement("this.typeId = $L", i)
          .addStatement("this.item = item", itemName)
          .addStatement("return this")
          .build();
      typeBuilderBuilder.addMethod(setterBuilder.build());
    }

    MethodSpec.Builder buildBuilder =
        methodBuildBuilder()
            .addStatement("byte[] buf")
            .addStatement("$T clazz = $N.get(typeId)", Class.class, itemClasses);

    buildBuilder.beginControlFlow("if (item == null)").addStatement("buf = new byte[4]");

    boolean hasByte =
        fieldTypeDescriptors.stream().map(this::getTypeName).anyMatch(tn -> tn == TypeName.BYTE);
    boolean hasByteBox =
        fieldTypeDescriptors
            .stream()
            .map(this::getTypeName)
            .anyMatch(tn -> tn == TypeName.BYTE.box());

    if (hasByte || hasByteBox) {
      if (hasByte || hasByteBox) {
        if (hasByte && hasByteBox) {
          buildBuilder.nextControlFlow("else if (clazz == byte.class || clazz == Byte.class)");
        } else if (hasByte) {
          buildBuilder.nextControlFlow("else if (clazz == byte.class)");
        } else if (hasByte) {
          buildBuilder.nextControlFlow("else if if (clazz == Byte.class)");
        }
        buildBuilder.addStatement("buf = new byte[5]").addStatement("buf[4] = (byte) item");
      }
    }

    buildBuilder
        .nextControlFlow("else")
        .addStatement("$T molecule = ($T) item", base.classNameMolecule, base.classNameMolecule)
        .addStatement("buf = new byte[4 + molecule.getSize()]")
        .addStatement("$T.setBytes(molecule.getRawData(), buf, 4)", base.classNameMoleculeUtils)
        .endControlFlow()
        .addStatement("$T.setInt(typeId, buf, 0)", base.classNameMoleculeUtils);

    buildBuilder
        .addStatement("$T u  = new $T()", name, name)
        .addStatement("u.typeId = typeId")
        .addStatement("u.item = item")
        .addStatement("u.buf = buf")
        .addStatement("return u");

    typeBuilderBuilder.addMethod(buildBuilder.build());
  }
}
