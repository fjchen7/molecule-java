package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.FixedVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class Bytes extends FixedVector {
    public static int ITEM_SIZE = 1;

    public static Class ITEM_TYPE = byte.class;

    private byte[] items;

    private Bytes() {
    }

    @Override
    public int getItemSize() {
        return ITEM_SIZE;
    }

    @Nonnull
    public byte get(int i) {
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
        private byte[] items;

        private Builder() {
            this.items = new byte[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int itemCount = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            int size = 4 + itemCount * ITEM_SIZE;
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, Bytes.class);
            }
            items = Arrays.copyOfRange(buf, 4, buf.length);
        }

        public Builder add(@Nonnull byte item) {
            Objects.requireNonNull(item);
            byte[] tempItems = new byte[items.length + 1];
            System.arraycopy(items, 0, tempItems, 0, items.length);
            tempItems[items.length] = item;;
            items = tempItems;
            return this;
        }

        public Builder set(int i, @Nonnull byte item) {
            Objects.requireNonNull(item);
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i < 0 || i >= items.length) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            byte[] tempItems = new byte[items.length - 1];
            System.arraycopy(items, 0, tempItems, 0, i);
            System.arraycopy(items, i + 1, tempItems, i, items.length - i -1);
            items = tempItems;
            return this;
        }

        public Bytes build() {
            byte[] buf = new byte[4 + items.length * ITEM_SIZE];
            MoleculeUtils.setInt(items.length, buf, 0);;
            MoleculeUtils.setBytes(items, buf, 4);
            Bytes v = new Bytes();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
