package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class StructC extends Struct {
    public static int SIZE = 8;

    public static int FIELD_COUNT = 4;

    private byte f1;

    private byte f2;

    private Byte2 f3;

    private Byte4 f4;

    private StructC() {
    }

    @Nonnull
    public byte getF1() {
        return f1;
    }

    @Nonnull
    public byte getF2() {
        return f2;
    }

    @Nonnull
    public Byte2 getF3() {
        return f3;
    }

    @Nonnull
    public Byte4 getF4() {
        return f4;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private byte f1;

        private byte f2;

        private Byte2 f3;

        private Byte4 f4;

        private Builder() {
            f3 = Byte2.builder().build();
            f4 = Byte4.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, StructC.class);
            }
            byte[] itemBuf;
            f1 = buf[0];
            f2 = buf[1];
            itemBuf = Arrays.copyOfRange(buf, 2, 4);
            f3 = Byte2.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, 4, 8);
            f4 = Byte4.builder(itemBuf).build();
        }

        public Builder setF1(@Nonnull byte f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull byte f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public Builder setF3(@Nonnull Byte2 f3) {
            Objects.requireNonNull(f3);
            this.f3 = f3;
            return this;
        }

        public Builder setF4(@Nonnull Byte4 f4) {
            Objects.requireNonNull(f4);
            this.f4 = f4;
            return this;
        }

        public StructC build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + 1;
            offsets[2] = offsets[1] + 1;
            offsets[3] = offsets[2] + Byte2.SIZE;
            byte[] buf = new byte[SIZE];
            buf[offsets[0]] = f1;
            buf[offsets[1]] = f2;
            MoleculeUtils.setBytes(f3.toByteArray(), buf, offsets[2]);
            MoleculeUtils.setBytes(f4.toByteArray(), buf, offsets[3]);
            StructC s = new StructC();
            s.buf = buf;
            s.f1 = f1;
            s.f2 = f2;
            s.f3 = f3;
            s.f4 = f4;
            return s;
        }
    }
}
