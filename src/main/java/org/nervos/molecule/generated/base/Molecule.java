package org.nervos.molecule.generated.base;

public abstract class Molecule {
    protected byte[] buf;

    public byte[] getRawData() {
        return buf;
    }

    public int getSize() {
        return getRawData().length;
    }
}
