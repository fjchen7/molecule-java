package org.nervos.molecule.descriptor;

public class FieldDescriptor {
  private String name;
  private TypeDescriptor typeDescriptor;

  public String getName() {
    return name;
  }

  public TypeDescriptor getTypeDescriptor() {
    return typeDescriptor;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
    this.typeDescriptor = typeDescriptor;
  }

  public FieldDescriptor(String name, TypeDescriptor typeDescriptor) {
    this.name = name;
    this.typeDescriptor = typeDescriptor;
  }
}
