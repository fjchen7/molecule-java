package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.nervos.molecule.generated.base.DynamicVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class ByteOptVec extends DynamicVector {
    public static Class ITEM_TYPE = Byte.class;

    private Byte[] items;

    private ByteOptVec() {
    }

    @Nullable
    public Byte get(int i) {
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
        private Byte[] items;

        private Builder() {
            items = new Byte[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, ByteOptVec.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            items = new Byte[offsets.length - 1];
            for (int i = 0; i < items.length; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, offsets[i], offsets[i + 1]);
                items[i] = (itemBuf.length == 0 ? null: buf[0]);
            }
        }

        public Builder add(@Nullable Byte item) {
            Byte[] originalItems = items;
            items = new Byte[originalItems.length + 1];
            System.arraycopy(originalItems, 0, items, 0, originalItems.length);
            items[items.length - 1] = item;;
            return this;
        }

        public Builder add(@Nonnull Byte[] items) {
            Objects.requireNonNull(items);
            Byte[] originalItems = this.items;
            this.items = new Byte[originalItems.length + items.length];
            System.arraycopy(originalItems, 0, this.items, 0, originalItems.length);
            System.arraycopy(items, 0, this.items, originalItems.length, items.length);
            return this;
        }

        public Builder set(int i, @Nullable Byte item) {
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i < 0 || i >= items.length) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            Byte[] originalItems = items;
            items = new Byte[originalItems.length - 1];
            System.arraycopy(originalItems, 0, items, 0, i);
            System.arraycopy(originalItems, i + 1, items, i, originalItems.length - i -1);
            return this;
        }

        public ByteOptVec build() {
            int size = 4 + 4 * items.length;
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    size += 1;
                }
            }
            byte[] buf = new byte[size];
            MoleculeUtils.setInt(size, buf, 0);;
            int offset = 4 + 4 * items.length;
            int start = 4;
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setInt(offset, buf, start);;
                if (items[i] != null) {
                    offset += 1;
                }
                start += 4;
            }
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    MoleculeUtils.setBytes(new byte[]{items[i]}, buf, start);;
                    start += 1;
                }
            }
            ByteOptVec v = new ByteOptVec();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
