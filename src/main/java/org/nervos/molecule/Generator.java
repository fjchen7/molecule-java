package org.nervos.molecule;

import org.nervos.molecule.descriptor.TypeDescriptor;
import org.nervos.molecule.generator.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Generator {
    public static void generate(Path schemaPath, String packageName, Path codePath) throws IOException {
        BaseTypeGenerator base = new BaseTypeGenerator(packageName + ".base");
        base.generateAndWriteTo(codePath);

        Map<String, TypeDescriptor> schemas = new MoleculeResolver(schemaPath).resolve();
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
                case UNION:
                    UnionGenerator ug = new UnionGenerator(base, descriptor, concretePackageName);
                    ug.generateAndWriteTo(codePath);
                    break;
                case OPTION:
                    break;
                default:
            }
        }
    }
}
