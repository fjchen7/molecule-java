package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.FixedVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class Byte32Vec extends FixedVector {
    public static int ITEM_SIZE = Byte32.SIZE;

    public static Class ITEM_TYPE = Byte32.class;

    private Byte32[] items;

    private Byte32Vec() {
    }

    @Nonnull
    public Byte32 get(int i) {
        if (i >= items.length) {
            throw new IndexOutOfBoundsException("Index out of range: " + items.length);
        }
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
        private Byte32[] items;

        private Builder() {
            this.items = new Byte32[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int itemCount = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            int size = 4 + itemCount * ITEM_SIZE;
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, Byte32Vec.class);
            }
            int start = 4;
            items = new Byte32[itemCount];
            for (int i = 0; i < itemCount; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, start, start + ITEM_SIZE);
                items[i] = Byte32.builder(itemBuf).build();
                start += ITEM_SIZE;
            }
        }

        public Builder add(@Nonnull Byte32 item) {
            Objects.requireNonNull(item);
            Byte32[] tempItems = new Byte32[items.length + 1];
            System.arraycopy(items, 0, tempItems, 0, items.length);
            tempItems[items.length] = item;;
            items = tempItems;
            return this;
        }

        public Builder set(int i, @Nonnull Byte32 item) {
            Objects.requireNonNull(item);
            if (i >= items.length) {
                throw new IndexOutOfBoundsException("Index out of range: " + items.length);
            }
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i >= items.length) {
                throw new IndexOutOfBoundsException("Index out of range: " + items.length);
            }
            Byte32[] tempItems = new Byte32[items.length - 1];
            System.arraycopy(items, 0, tempItems, 0, i);
            System.arraycopy(items, i + 1, tempItems, i, items.length - i -1);
            items = tempItems;
            return this;
        }

        public Byte32Vec build() {
            byte[] buf = new byte[4 + items.length * ITEM_SIZE];
            MoleculeUtils.setSize(items.length, buf, 0);;
            int start = 4;
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setBytes(items[i].getRawData(), buf, start);
                start += ITEM_SIZE;
            }
            Byte32Vec v = new Byte32Vec();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
