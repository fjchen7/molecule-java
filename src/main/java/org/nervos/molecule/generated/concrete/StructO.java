package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class StructO extends Struct {
    public static int SIZE = 13;

    public static int FIELD_COUNT = 2;

    private StructIx3 f1;

    private byte f2;

    private StructO() {
    }

    @Nonnull
    public StructIx3 getF1() {
        return f1;
    }

    @Nonnull
    public byte getF2() {
        return f2;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private StructIx3 f1;

        private byte f2;

        private Builder() {
            f1 = StructIx3.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, StructO.class);
            }
            byte[] itemBuf;
            itemBuf = Arrays.copyOfRange(buf, 0, 12);
            f1 = StructIx3.builder(itemBuf).build();
            f2 = buf[12];
        }

        public Builder setF1(@Nonnull StructIx3 f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull byte f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public StructO build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + StructIx3.SIZE;
            byte[] buf = new byte[SIZE];
            MoleculeUtils.setBytes(f1.toByteArray(), buf, offsets[0]);
            buf[offsets[1]] = f2;
            StructO s = new StructO();
            s.buf = buf;
            s.f1 = f1;
            s.f2 = f2;
            return s;
        }
    }
}
