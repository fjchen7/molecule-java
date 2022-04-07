package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class StructJ extends Struct {
    public static int SIZE = 7;

    public static int FIELD_COUNT = 2;

    private Byte6 f1;

    private byte f2;

    private StructJ() {
    }

    @Nonnull
    public Byte6 getF1() {
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
        private Byte6 f1;

        private byte f2;

        private Builder() {
            f1 = Byte6.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, StructJ.class);
            }
            byte[] itemBuf;
            itemBuf = Arrays.copyOfRange(buf, 0, 6);
            f1 = Byte6.builder(itemBuf).build();
            f2 = buf[6];
        }

        public Builder setF1(@Nonnull Byte6 f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull byte f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public StructJ build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + Byte6.SIZE;
            byte[] buf = new byte[SIZE];
            MoleculeUtils.setBytes(f1.toByteArray(), buf, offsets[0]);
            buf[offsets[1]] = f2;
            StructJ s = new StructJ();
            s.buf = buf;
            s.f1 = f1;
            s.f2 = f2;
            return s;
        }
    }
}
