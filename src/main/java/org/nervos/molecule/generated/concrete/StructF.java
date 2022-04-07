package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class StructF extends Struct {
    public static int SIZE = 5;

    public static int FIELD_COUNT = 3;

    private byte f1;

    private Byte3 f2;

    private byte f3;

    private StructF() {
    }

    @Nonnull
    public byte getF1() {
        return f1;
    }

    @Nonnull
    public Byte3 getF2() {
        return f2;
    }

    @Nonnull
    public byte getF3() {
        return f3;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private byte f1;

        private Byte3 f2;

        private byte f3;

        private Builder() {
            f2 = Byte3.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, StructF.class);
            }
            byte[] itemBuf;
            f1 = buf[0];
            itemBuf = Arrays.copyOfRange(buf, 1, 4);
            f2 = Byte3.builder(itemBuf).build();
            f3 = buf[4];
        }

        public Builder setF1(@Nonnull byte f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull Byte3 f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public Builder setF3(@Nonnull byte f3) {
            Objects.requireNonNull(f3);
            this.f3 = f3;
            return this;
        }

        public StructF build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + 1;
            offsets[2] = offsets[1] + Byte3.SIZE;
            byte[] buf = new byte[SIZE];
            buf[offsets[0]] = f1;
            MoleculeUtils.setBytes(f2.toByteArray(), buf, offsets[1]);
            buf[offsets[2]] = f3;
            StructF s = new StructF();
            s.buf = buf;
            s.f1 = f1;
            s.f2 = f2;
            s.f3 = f3;
            return s;
        }
    }
}
