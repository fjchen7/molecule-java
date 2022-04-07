package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Table;

public final class TableA extends Table {
    public static int FIELD_COUNT = 8;

    private Word2 f1;

    private StructA f2;

    private Bytes f3;

    private BytesVec f4;

    private Table1 f5;

    private Bytes f6;

    private UnionA f7;

    private byte f8;

    private TableA() {
    }

    @Nonnull
    public Word2 getF1() {
        return f1;
    }

    @Nonnull
    public StructA getF2() {
        return f2;
    }

    @Nonnull
    public Bytes getF3() {
        return f3;
    }

    @Nonnull
    public BytesVec getF4() {
        return f4;
    }

    @Nonnull
    public Table1 getF5() {
        return f5;
    }

    @Nullable
    public Bytes getF6() {
        return f6;
    }

    @Nonnull
    public UnionA getF7() {
        return f7;
    }

    @Nonnull
    public byte getF8() {
        return f8;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private Word2 f1;

        private StructA f2;

        private Bytes f3;

        private BytesVec f4;

        private Table1 f5;

        private Bytes f6;

        private UnionA f7;

        private byte f8;

        private Builder() {
            f1 = Word2.builder().build();
            f2 = StructA.builder().build();
            f3 = Bytes.builder().build();
            f4 = BytesVec.builder().build();
            f5 = Table1.builder().build();
            f6 = null;
            f7 = UnionA.builder().build();
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, TableA.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            if (offsets.length - 1 != FIELD_COUNT) {
                throw new MoleculeException("Raw data should have " + FIELD_COUNT + " but find " + (offsets.length -1) + " offsets in header.");
            }
            byte[] itemBuf;
            itemBuf = Arrays.copyOfRange(buf, offsets[0], offsets[1]);
            f1 = Word2.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, offsets[1], offsets[2]);
            f2 = StructA.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, offsets[2], offsets[3]);
            f3 = Bytes.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, offsets[3], offsets[4]);
            f4 = BytesVec.builder(itemBuf).build();
            itemBuf = Arrays.copyOfRange(buf, offsets[4], offsets[5]);
            f5 = Table1.builder(itemBuf).build();
            if (offsets[5] != offsets[6]) {
                itemBuf = Arrays.copyOfRange(buf, offsets[5], offsets[6]);
                f6 = Bytes.builder(itemBuf).build();
            }
            itemBuf = Arrays.copyOfRange(buf, offsets[6], offsets[7]);
            f7 = UnionA.builder(itemBuf).build();
            f8 = buf[offsets[7]];
        }

        public Builder setF1(@Nonnull Word2 f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Builder setF2(@Nonnull StructA f2) {
            Objects.requireNonNull(f2);
            this.f2 = f2;
            return this;
        }

        public Builder setF3(@Nonnull Bytes f3) {
            Objects.requireNonNull(f3);
            this.f3 = f3;
            return this;
        }

        public Builder setF4(@Nonnull BytesVec f4) {
            Objects.requireNonNull(f4);
            this.f4 = f4;
            return this;
        }

        public Builder setF5(@Nonnull Table1 f5) {
            Objects.requireNonNull(f5);
            this.f5 = f5;
            return this;
        }

        public Builder setF6(@Nullable Bytes f6) {
            this.f6 = f6;
            return this;
        }

        public Builder setF7(@Nonnull UnionA f7) {
            Objects.requireNonNull(f7);
            this.f7 = f7;
            return this;
        }

        public Builder setF8(@Nonnull byte f8) {
            Objects.requireNonNull(f8);
            this.f8 = f8;
            return this;
        }

        public TableA build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 4 + 4 * FIELD_COUNT;
            offsets[1] = offsets[0] + f1.getSize();
            offsets[2] = offsets[1] + f2.getSize();
            offsets[3] = offsets[2] + f3.getSize();
            offsets[4] = offsets[3] + f4.getSize();
            offsets[5] = offsets[4] + f5.getSize();
            offsets[6] = offsets[5] + (f6 == null ? 0 : f6.getSize());
            offsets[7] = offsets[6] + f7.getSize();
            int[] fieldsSize = new int[FIELD_COUNT];
            fieldsSize[0] = f1.getSize();
            fieldsSize[1] = f2.getSize();
            fieldsSize[2] = f3.getSize();
            fieldsSize[3] = f4.getSize();
            fieldsSize[4] = f5.getSize();
            fieldsSize[5] = (f6 == null ? 0 : f6.getSize());
            fieldsSize[6] = f7.getSize();
            fieldsSize[7] = 1;
            byte[][] fieldsBuf = new byte[FIELD_COUNT][];
            fieldsBuf[0] = f1.toByteArray();
            fieldsBuf[1] = f2.toByteArray();
            fieldsBuf[2] = f3.toByteArray();
            fieldsBuf[3] = f4.toByteArray();
            fieldsBuf[4] = f5.toByteArray();
            fieldsBuf[5] = (f6 == null ? new byte[]{} : f6.toByteArray());
            fieldsBuf[6] = f7.toByteArray();
            fieldsBuf[7] = new byte[]{f8};
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
            TableA t = new TableA();
            t.buf = buf;
            t.f1 = f1;
            t.f2 = f2;
            t.f3 = f3;
            t.f4 = f4;
            t.f5 = f5;
            t.f6 = f6;
            t.f7 = f7;
            t.f8 = f8;
            return t;
        }
    }
}
