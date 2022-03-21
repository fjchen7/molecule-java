package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.DynamicVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class CellOutputVec extends DynamicVector {
    public static Class ITEM_TYPE = CellOutput.class;

    private CellOutput[] items;

    private CellOutputVec() {
    }

    @Nonnull
    public CellOutput get(int i) {
        return items[i];
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    @Override
    public Class getItemType() {
        return ITEM_TYPE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private CellOutput[] items;

        private Builder() {
            items = new CellOutput[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, CellOutputVec.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            items = new CellOutput[offsets.length - 1];
            for (int i = 0; i < items.length; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, offsets[i], offsets[i + 1]);
                items[i] = CellOutput.builder(itemBuf).build();
            }
        }

        public Builder add(@Nonnull CellOutput item) {
            Objects.requireNonNull(item);
            CellOutput[] tempItems = new CellOutput[items.length + 1];
            System.arraycopy(items, 0, tempItems, 0, items.length);
            tempItems[items.length] = item;;
            items = tempItems;
            return this;
        }

        public Builder set(int i, @Nonnull CellOutput item) {
            Objects.requireNonNull(item);
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i < 0 || i >= items.length) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            CellOutput[] tempItems = new CellOutput[items.length - 1];
            System.arraycopy(items, 0, tempItems, 0, i);
            System.arraycopy(items, i + 1, tempItems, i, items.length - i -1);
            items = tempItems;
            return this;
        }

        public CellOutputVec build() {
            int size = 4 + 4 * items.length;
            for (int i = 0; i < items.length; i++) {
                size += items[i].getSize();
            }
            byte[] buf = new byte[size];
            MoleculeUtils.setInt(size, buf, 0);;
            int offset = 4 + 4 * items.length;
            int start = 4;
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setInt(offset, buf, start);;
                offset += items[i].getSize();
                start += 4;
            }
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setBytes(items[i].getRawData(), buf, start);;
                start += items[i].getSize();
            }
            CellOutputVec v = new CellOutputVec();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
