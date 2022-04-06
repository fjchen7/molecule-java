package org.nervos.molecule.descriptor;

import java.util.ArrayList;
import java.util.List;
import org.nervos.molecule.MoleculeType;

public class TypeDescriptor {
  private MoleculeType moleculeType;
  private String name;
  private List<FieldDescriptor> fields;
  private int size = -1;

  public static TypeDescriptor BYTE_TYPE_DESCRIPTOR = new TypeDescriptor();

  static {
    BYTE_TYPE_DESCRIPTOR.moleculeType = MoleculeType.BYTE;
    BYTE_TYPE_DESCRIPTOR.name = "byte";
    BYTE_TYPE_DESCRIPTOR.size = 1;
    BYTE_TYPE_DESCRIPTOR.fields = new ArrayList<>();
  }

  public boolean isFixedType() {
    return moleculeType.isFixed();
  }

  public String getName() {
    return name;
  }

  public MoleculeType getMoleculeType() {
    return moleculeType;
  }

  public List<FieldDescriptor> getFields() {
    return fields;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public static TypeDescriptorBuilder builder() {
    return new TypeDescriptorBuilder();
  }

  public static final class TypeDescriptorBuilder {
    private MoleculeType moleculeType;
    private String name;
    private List<FieldDescriptor> fields;
    private int size = -1;

    private TypeDescriptorBuilder() {
      fields = new ArrayList<>();
    }

    public TypeDescriptorBuilder setMoleculeType(MoleculeType moleculeType) {
      this.moleculeType = moleculeType;
      return this;
    }

    public TypeDescriptorBuilder setMoleculeType(String moleculeType) {
      this.moleculeType = MoleculeType.from(moleculeType);
      return this;
    }

    public TypeDescriptorBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public TypeDescriptorBuilder addField(FieldDescriptor field) {
      this.fields.add(field);
      return this;
    }

    public TypeDescriptorBuilder addField(String fieldName, TypeDescriptor typeDescriptor) {
      FieldDescriptor field = new FieldDescriptor(fieldName, typeDescriptor);
      this.fields.add(field);
      return this;
    }

    public TypeDescriptorBuilder addFieldPlaceholder(String filedName, String typeName) {

      FieldDescriptor field =
          new FieldDescriptor(filedName, TypeDescriptor.builder().setName(typeName).build());
      this.fields.add(field);
      return this;
    }

    public TypeDescriptorBuilder setSize(int size) {
      this.size = size;
      return this;
    }

    public TypeDescriptor build() {
      TypeDescriptor typeDescriptor = new TypeDescriptor();
      typeDescriptor.setSize(size);
      typeDescriptor.fields = this.fields;
      typeDescriptor.name = this.name;
      typeDescriptor.moleculeType = this.moleculeType;
      return typeDescriptor;
    }
  }
}
