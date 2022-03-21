package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.FixedVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class CellDepVec extends FixedVector {
    public static int ITEM_SIZE = CellDep.SIZE;

    public static Class ITEM_TYPE = CellDep.class;

    private CellDep[] items;

    private CellDepVec() {
    }

    @Override
    public int getItemSize() {
        return ITEM_SIZE;
    }

    @Nonnull
    public CellDep get(int i) {
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
        private CellDep[] items;

        private Builder() {
            this.items = new CellDep[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int itemCount = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            int size = 4 + itemCount * ITEM_SIZE;
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, CellDepVec.class);
            }
            int start = 4;
            items = new CellDep[itemCount];
            for (int i = 0; i < itemCount; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, start, start + ITEM_SIZE);
                items[i] = CellDep.builder(itemBuf).build();
                start += ITEM_SIZE;
            }
        }

        public Builder add(@Nonnull CellDep item) {
            Objects.requireNonNull(item);
            CellDep[] tempItems = new CellDep[items.length + 1];
            System.arraycopy(items, 0, tempItems, 0, items.length);
            tempItems[items.length] = item;;
            items = tempItems;
            return this;
        }

        public Builder set(int i, @Nonnull CellDep item) {
            Objects.requireNonNull(item);
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i < 0 || i >= items.length) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            CellDep[] tempItems = new CellDep[items.length - 1];
            System.arraycopy(items, 0, tempItems, 0, i);
            System.arraycopy(items, i + 1, tempItems, i, items.length - i -1);
            items = tempItems;
            return this;
        }

        public CellDepVec build() {
            byte[] buf = new byte[4 + items.length * ITEM_SIZE];
            MoleculeUtils.setInt(items.length, buf, 0);;
            int start = 4;
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setBytes(items[i].getRawData(), buf, start);
                start += ITEM_SIZE;
            }
            CellDepVec v = new CellDepVec();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
