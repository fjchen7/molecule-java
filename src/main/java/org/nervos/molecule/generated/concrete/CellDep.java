package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class CellDep extends Struct {
    public static int SIZE = 37;

    public static int FIELD_COUNT = 2;

    private OutPoint outPoint;

    private byte depType;

    private CellDep() {
    }

    @Nonnull
    public OutPoint getOutPoint() {
        return outPoint;
    }

    @Nonnull
    public byte getDepType() {
        return depType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private OutPoint outPoint;

        private byte depType;

        private Builder() {
            outPoint = OutPoint.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, CellDep.class);
            }
            byte[] itemBuf;
            itemBuf = Arrays.copyOfRange(buf, 0, 36);
            outPoint = OutPoint.builder(itemBuf).build();
            depType = buf[36];
        }

        public Builder setOutPoint(@Nonnull OutPoint outPoint) {
            Objects.requireNonNull(outPoint);
            this.outPoint = outPoint;
            return this;
        }

        public Builder setDepType(@Nonnull byte depType) {
            Objects.requireNonNull(depType);
            this.depType = depType;
            return this;
        }

        public CellDep build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + OutPoint.SIZE;
            byte[] buf = new byte[SIZE];
            MoleculeUtils.setBytes(outPoint.toByteArray(), buf, offsets[0]);
            buf[offsets[1]] = depType;
            CellDep s = new CellDep();
            s.buf = buf;
            s.outPoint = outPoint;
            s.depType = depType;
            return s;
        }
    }
}
