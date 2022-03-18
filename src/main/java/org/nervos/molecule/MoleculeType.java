package org.nervos.molecule;

public enum MoleculeType {
    BYTE(true),
    ARRAY(true),
    STRUCT(true),
    VECTOR,
    TABLE,
    OPTION,
    UNION;

    private boolean isFixed;

    MoleculeType(boolean isFixed) {
        this.isFixed = isFixed;
    }

    MoleculeType() {
        this(false);
    }

    public boolean isFixed() {
        return isFixed;
    }

    public static MoleculeType from(String value) {
        return MoleculeType.valueOf(value.toUpperCase());
    }
}
