package generator;

import org.junit.jupiter.api.Test;
import org.nervos.molecule.descriptor.TypeDescriptor;
import org.nervos.molecule.generator.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        Path schemaPath = Paths.get("src/main/resources/blockchain.mol");
        Path codePath = Paths.get("src/main/java");
        generate(schemaPath, "org.nervos.molecule.generated", codePath);
    }

    private void generate(Path schemaPath, String packageName, Path codePath) throws IOException {
        Map<String, TypeDescriptor> schemas = new MoleculeResolver(schemaPath).resolve();

        BaseTypeGenerator base = new BaseTypeGenerator(packageName + ".base");
        base.generateAndWriteTo(codePath);

        String concretePackageName = packageName + ".concrete";
        for (TypeDescriptor descriptor : schemas.values()) {
            switch (descriptor.getMoleculeType()) {
                case ARRAY:
                    ArrayGenerator ag = new ArrayGenerator(base, descriptor, concretePackageName);
                    ag.generateAndWriteTo(codePath);
                    break;
                case STRUCT:
                    StructGenerator sg = new StructGenerator(base, descriptor, concretePackageName);
                    sg.generateAndWriteTo(codePath);
                    break;
                case VECTOR:
                    if (descriptor.getFields().get(0).getTypeDescriptor().isFixedType()) {
                        FixedVectorGenerator fg =
                                new FixedVectorGenerator(base, descriptor, concretePackageName);
                        fg.generateAndWriteTo(codePath);
                    } else {
                        DynamicVectorGenerator dg =
                                new DynamicVectorGenerator(base, descriptor, concretePackageName);
                        dg.generateAndWriteTo(codePath);
                    }
                    break;
                case TABLE:
                    TableGenerator tg = new TableGenerator(base, descriptor, concretePackageName);
                    tg.generateAndWriteTo(codePath);
                    break;
                case OPTION:
                    break;
                default:
            }
        }
    }
}
