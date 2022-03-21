package com.nervos.molecule;

import org.junit.jupiter.api.Test;
import org.nervos.molecule.Generator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        Path schemaPath = Paths.get("src/main/resources/blockchain.mol");
        Path codePath = Paths.get("src/main/java");
        Generator.generate(schemaPath, "org.nervos.molecule.generated", codePath);
    }

    @Test
    public void testGenerator() throws IOException {
        Path schemaPath = Paths.get("src/main/resources/types.mol");
        Path codePath = Paths.get("src/main/java");
        Generator.generate(schemaPath, "org.nervos.molecule.generated", codePath);
    }

}
