package org.nervos.molecule.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractGenerator {
    protected String packageName = "com.molecule.generated";
    protected String indent = "    ";

    protected JavaFile newJavaFile(TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .indent(indent).skipJavaLangImports(true).build();
        return javaFile;
    }

    public abstract void generateAndWriteTo(Path path) throws IOException;
}
