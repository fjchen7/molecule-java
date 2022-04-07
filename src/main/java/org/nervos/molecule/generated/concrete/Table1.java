package org.nervos.molecule.generated.concrete;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Table;

public final class Table1 extends Table {
    public static int FIELD_COUNT = 1;

    private byte f1;

    private Table1() {
    }

    @Nonnull
    public byte getF1() {
        return f1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private byte f1;

        private Builder() {
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, Table1.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            if (offsets.length - 1 != FIELD_COUNT) {
                throw new MoleculeException("Raw data should have " + FIELD_COUNT + " but find " + (offsets.length -1) + " offsets in header.");
            }
            byte[] itemBuf;
            f1 = buf[offsets[0]];
        }

        public Builder setF1(@Nonnull byte f1) {
            Objects.requireNonNull(f1);
            this.f1 = f1;
            return this;
        }

        public Table1 build() {
            int[] offsets = new int[FIELD_COUNT];
            offsets[0] = 4 + 4 * FIELD_COUNT;
            int[] fieldsSize = new int[FIELD_COUNT];
            fieldsSize[0] = 1;
            byte[][] fieldsBuf = new byte[FIELD_COUNT][];
            fieldsBuf[0] = new byte[]{f1};
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
            Table1 t = new Table1();
            t.buf = buf;
            t.f1 = f1;
            return t;
        }
    }
}
