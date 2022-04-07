package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Table;

public final class Table3 extends Table {
    public static int FIELD_COUNT = 3;

    private byte f1;

    private Word2 f2;

    private StructA f3;

    private Table3() {
    }

    @Nonnull
    public byte getF1() {
        return f1;
    }

    @Nonnull
    public Word2 getF2() {
        return f2;
    }

    @Nonnull
    public StructA getF3() {
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

        private Word2 f2;

        private StructA f3;

        private Builder() {
            f2 = Word2.builder().build();
            f3 = StructA.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, Table3.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            if (offsets.length - 1 != FIELD_COUNT) {
                throw new MoleculeException("Raw data should have " + FIELD_COUNT + " but find " + (offsets.length -1) + " offsets in header.");
            }
            byte[] itemBuf;
            f1 = buf[offsets[0]];
            itemBuf = Arrays.copyOfRange(buf, offsets[1], offsets[2]);
            f2 = Word2.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, offsets[2], offsets[3]);
            f3 = StructA.builder(itemBuf).build();
        }

        public Builder setF1(@Nonnull byte f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull Word2 f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public Builder setF3(@Nonnull StructA f3) {
            Objects.requireNonNull(f3);
            this.f3 = f3;
            return this;
        }

        public Table3 build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 4 + 4 * FIELD_COUNT;
            offsets[1] = offsets[0] + 1;
            offsets[2] = offsets[1] + f2.getSize();
            int[] fieldsSize = new int[FIELD_COUNT];
            fieldsSize[0] = 1;
            fieldsSize[1] = f2.getSize();
            fieldsSize[2] = f3.getSize();
            byte[][] fieldsBuf = new byte[FIELD_COUNT][];
            fieldsBuf[0] = new byte[]{f1};
            fieldsBuf[1] = f2.toByteArray();
            fieldsBuf[2] = f3.toByteArray();
            int size = 4 + 4 * FIELD_COUNT;
            for (int i = 0; i < FIELD_COUNT; i++) {
                size += fieldsSize[i];
            }
            byte[] buf = new byte[size];;
            MoleculeUtils.setInt(size, buf, 0);
            int start = 4;
            for (int i = 0; i < FIELD_COUNT; i++) {
                MoleculeUtils.setInt(offsets[i], buf, start);
                start += 4;
            }
            for (int i = 0; i < FIELD_COUNT; i++) {
                MoleculeUtils.setBytes(fieldsBuf[i], buf, offsets[i]);
            }
            Table3 t = new Table3();
            t.buf = buf;
            t.f1 = f1;
            t.f2 = f2;
            t.f3 = f3;
            return t;
        }
    }
}
