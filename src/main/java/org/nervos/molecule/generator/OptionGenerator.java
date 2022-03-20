package org.nervos.molecule.generator;

import com.squareup.javapoet.*;
import org.nervos.molecule.descriptor.TypeDescriptor;

import javax.lang.model.element.Modifier;

public class OptionGenerator extends AbstractConcreteGenerator {
    TypeName itemTypeName;

    public OptionGenerator(BaseTypeGenerator base, TypeDescriptor descriptor, String packageName) {
        super(base, descriptor, packageName);

        TypeDescriptor itemTypeDescriptor = descriptor.getFields().get(0).getTypeDescriptor();
        if (itemTypeDescriptor == TypeDescriptor.BYTE_TYPE_DESCRIPTOR) {
            itemTypeName = TypeName.BYTE.box();
        } else {
            itemTypeName = ClassName.get("", itemTypeDescriptor.getName());
        }
        superClassName = ParameterizedTypeName.get(base.classNameOption, itemTypeName);;
    }

    @Override
    protected void fillType() {
    }

    @Override
    protected void fillTypeBuilder() {
        FieldSpec item = FieldSpec.builder(itemTypeName, "item")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec.Builder constructorBuilder = constructorBuilder()
                .addStatement("item = null");

        MethodSpec.Builder constructorBufBuilder = constructorBufBuilder()
                .beginControlFlow("if (buf.length == 0)")
                .addStatement("item = null");

        if (itemTypeName == TypeName.BYTE.box()) {
            constructorBufBuilder
                    .nextControlFlow("else if (buf.length == 1)")
                    .addStatement("item = buf[0]")
                    .nextControlFlow("else")
                    .addStatement("throw new $T(1, buf.length, Byte.class)", base.classNameMoleculeException);
        } else {
            constructorBufBuilder
                    .nextControlFlow("else")
                    .addStatement("item = $T.builder(buf).build()", itemTypeName);
        }
        constructorBufBuilder.endControlFlow();

        typeBuilderBuilder
                .addField(item)
                .addMethod(constructorBuilder.build())
                .addMethod(constructorBufBuilder.build())
                .addMethod(
                        MethodSpec.methodBuilder("setItem")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(builderName)
                                .addParameter(itemTypeName, "item")
                                .addStatement("this.item = item")
                                .addStatement("return this")
                                .build()
                );

        MethodSpec.Builder buildBuilder = methodBuildBuilder()
                .addStatement("$T o  = new $T()", name, name)
                .addStatement("o.item = item")
                .beginControlFlow("if (item == null)")
                .addStatement("o.buf = new byte[0]")
                .nextControlFlow("else");

        if (itemTypeName == TypeName.BYTE.box()) {
            buildBuilder.addStatement("o.buf = new byte[]{item}");
        } else {
            buildBuilder.addStatement("o.buf = item.getRawData()");
        }
        buildBuilder
                .endControlFlow()
                .addStatement("return o");

        typeBuilderBuilder.addMethod(buildBuilder.build());
    }
}
