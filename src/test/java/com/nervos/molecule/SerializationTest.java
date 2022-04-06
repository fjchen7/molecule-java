package com.nervos.molecule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nervos.molecule.Generator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerializationTest {
  private static String concretePackageName = "org.nervos.molecule.generated.concrete";
  private static String basePackageName = "org.nervos.molecule.generated.base";

  public SerializationTest() throws ClassNotFoundException {}

  @BeforeAll
  public void generateCode() throws IOException, ClassNotFoundException {
    Path schemaPath = Paths.get("src/main/resources/types.mol");
    Path codePath = Paths.get("src/main/java");
    Generator.generate(schemaPath, "org.nervos.molecule.generated", codePath);
  }

  @Test
  void testDefault()
      throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException,
          ClassNotFoundException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    File source = new File("src/test/resources/default.yaml");
    List<Map<String, String>> cases =
        mapper.readValue(source, new TypeReference<List<Map<String, String>>>() {});
    System.out.println("[Default Cases] >>>");
    for (Map<String, String> c : cases) {
      testDefaultCase(c);
    }
  }

  @Test
  void testSimple()
      throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException,
          NoSuchFieldException, ClassNotFoundException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    File source = new File("src/test/resources/simple.yaml");
    List<Map<String, Object>> cases =
        mapper.readValue(source, new TypeReference<List<Map<String, Object>>>() {});

    System.out.println("[Simple Cases] >>>");
    for (Map<String, Object> c : cases) {
      testSimpleCase(c);
    }
  }

  private void testDefaultCase(Map<String, String> testData)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
          ClassNotFoundException {
    String name = testData.get("name");
    String expected = formatHexString(testData.get("expected"));

    Class clazz;
    try {
      clazz = Class.forName(concretePackageName + "." + name);
    } catch (ClassNotFoundException e) {
      System.out.println(name + ": SKIPPED");
      return;
    }
    Object instance = newDefaultMoleculeInstance(clazz);
    String hex = bytesToHex(getRawData(instance));
    Assertions.assertEquals(expected, hex);
    System.out.println(name + ": PASSED");
  }

  private void testSimpleCase(Map<String, Object> testData)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException,
          NoSuchFieldException, ClassNotFoundException {
    String name = (String) testData.get("name");

    Class clazz;
    Class builderClazz;
    try {
      clazz = Class.forName(concretePackageName + "." + name);
      builderClazz = Class.forName(clazz.getName() + "$Builder");
    } catch (ClassNotFoundException e) {
      System.out.println(name + ": SKIPPED");
      return;
    }
    Method m = clazz.getMethod("builder");
    Object builder = m.invoke(null);

    Class arrayClazz = Class.forName(basePackageName + ".Array");
    Class tableClazz = Class.forName(basePackageName + ".Table");
    Class vectorClazz = Class.forName(basePackageName + ".Vector");
    Class unionClazz = Class.forName(basePackageName + ".Union");
    Class structClazz = Class.forName(basePackageName + ".Struct");

    if (arrayClazz.isAssignableFrom(clazz)) {
      Class itemClazz = (Class) clazz.getField("ITEM_TYPE").get(null);
      Method f2 = builder.getClass().getMethod("set", int.class, itemClazz);
      Map<String, String> data = (Map<String, String>) testData.get("data");
      ;
      if (itemClazz == byte.class || itemClazz == Byte.class) {
        for (String key : data.keySet()) {
          int i = Integer.parseInt(key);
          byte v = hexToBytes(data.get(key))[0];
          f2.invoke(builder, i, v);
        }
      }
    } else if (vectorClazz.isAssignableFrom(clazz)) {
      Class itemClazz = (Class) clazz.getField("ITEM_TYPE").get(null);
      Method f2 = builder.getClass().getMethod("add", itemClazz);
      List<String> data = (List<String>) testData.get("data");

      for (String d : data) {
        byte[] v = hexToBytes(d);
        if (v.length == 0) {
          f2.invoke(builder, new Object[] {null});
        } else {
          if (itemClazz == byte.class || itemClazz == Byte.class) {
            f2.invoke(builder, v[0]);
          } else {
            Object field = newMoleculeInstance(itemClazz, v);
            f2.invoke(builder, itemClazz.cast(field));
          }
        }
      }
    } else if (structClazz.isAssignableFrom(clazz) || tableClazz.isAssignableFrom(clazz)) {
      Map<String, String> data = (Map<String, String>) testData.get("data");
      for (String k : data.keySet()) {
        byte[] v = hexToBytes(data.get(k));
        String kUpperFirst = k.substring(0, 1).toUpperCase() + k.substring(1);
        Class fieldClazz = clazz.getMethod("get" + kUpperFirst).getReturnType();
        Method f2 = builderClazz.getMethod("set" + kUpperFirst, fieldClazz);
        if (fieldClazz == byte.class || fieldClazz == Byte.class) {
          f2.invoke(builder, v[0]);
        } else {
          Object field = newMoleculeInstance(fieldClazz, v);
          f2.invoke(builder, fieldClazz.cast(field));
        }
      }
    } else if (unionClazz.isAssignableFrom(clazz)) {
      Map<String, String> data = (Map<String, String>) testData.get("item");
      String itemName = data.get("type");
      byte[] itemValue = hexToBytes(data.get("data"));
      String itemNameUpperFirst = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);

      Method f2 =
          Arrays.stream(builderClazz.getMethods())
              .filter(x -> Objects.equals(x.getName(), "to" + itemNameUpperFirst))
              .findAny()
              .get();
      Class itemClazz = f2.getParameterTypes()[0];

      if (itemClazz == byte.class || itemClazz == Byte.class) {
        f2.invoke(builder, itemValue[0]);
      } else {
        Object field = newMoleculeInstance(itemClazz, itemValue);
        f2.invoke(builder, itemClazz.cast(field));
      }
    } else {
      return;
    }

    Method f1 = builderClazz.getMethod("build");
    Object instance = f1.invoke(builder);
    String hex = bytesToHex(getRawData(instance));
    String expected = formatHexString((String) testData.get("expected"));
    Assertions.assertEquals(expected, hex);
    System.out.println(name + ": PASSED");
  }

  private byte[] getRawData(Object instance)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
          ClassNotFoundException {
    Class moleculeClazz = Class.forName(basePackageName + ".Molecule");
    Method m = moleculeClazz.getMethod("getRawData");
    return (byte[]) m.invoke(instance);
  }

  private Object newMoleculeInstance(Class clazz, byte[] buf)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
          IllegalAccessException {
    Class builderClass = Class.forName(clazz.getName() + "$Builder");
    Object builder;
    if (buf == null || buf.length == 0) {
      return null;
    } else {
      Method m = clazz.getMethod("builder", byte[].class);
      builder = m.invoke(null, buf);
    }
    Method f = builderClass.getMethod("build");
    Object instance = (Object) f.invoke(builder);
    return instance;
  }

  private Object newDefaultMoleculeInstance(Class clazz)
      throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException,
          IllegalAccessException {
    Class builderClass = Class.forName(clazz.getName() + "$Builder");
    Object builder;

    Method m = clazz.getMethod("builder");
    builder = m.invoke(null);

    Method f = builderClass.getMethod("build");
    Object instance = (Object) f.invoke(builder);
    return instance;
  }

  private String formatHexString(String hex) {
    return hex.substring(2).replace("_", "").replaceAll("/", "");
  }

  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  private byte[] hexToBytes(String s) {
    s = formatHexString(s);
    if (s.startsWith("0x")) {
      s = s.substring(2);
    }
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }
}
