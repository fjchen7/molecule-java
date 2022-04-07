package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Struct;

public final class StructG extends Struct {
    public static int SIZE = 10;

    public static int FIELD_COUNT = 4;

    private Byte3 f1;

    private byte f2;

    private Byte2 f3;

    private Word2 f4;

    private StructG() {
    }

    @Nonnull
    public Byte3 getF1() {
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
    public Word2 getF4() {
        return f4;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private Byte3 f1;

        private byte f2;

        private Byte2 f3;

        private Word2 f4;

        private Builder() {
            f1 = Byte3.builder().build();
            f3 = Byte2.builder().build();
            f4 = Word2.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, StructG.class);
            }
            byte[] itemBuf;
            itemBuf = Arrays.copyOfRange(buf, 0, 3);
            f1 = Byte3.builder(itemBuf).build();
            f2 = buf[3];
            itemBuf = Arrays.copyOfRange(buf, 4, 6);
            f3 = Byte2.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, 6, 10);
            f4 = Word2.builder(itemBuf).build();
        }

        public Builder setF1(@Nonnull Byte3 f1) {
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

        public Builder setF4(@Nonnull Word2 f4) {
            Objects.requireNonNull(f4);
            this.f4 = f4;
            return this;
        }

        public StructG build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 0;
            offsets[1] = offsets[0] + Byte3.SIZE;
            offsets[2] = offsets[1] + 1;
            offsets[3] = offsets[2] + Byte2.SIZE;
            byte[] buf = new byte[SIZE];
            MoleculeUtils.setBytes(f1.toByteArray(), buf, offsets[0]);
            buf[offsets[1]] = f2;
            MoleculeUtils.setBytes(f3.toByteArray(), buf, offsets[2]);
            MoleculeUtils.setBytes(f4.toByteArray(), buf, offsets[3]);
            StructG s = new StructG();
            s.buf = buf;
            s.f1 = f1;
            s.f2 = f2;
            s.f3 = f3;
            s.f4 = f4;
            return s;
        }
    }
}
