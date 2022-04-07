package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.nervos.molecule.generated.base.DynamicVector;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class WordOptVec extends DynamicVector {
    public static Class ITEM_TYPE = Word.class;

    private Word[] items;

    private WordOptVec() {
    }

    @Nullable
    public Word get(int i) {
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
        private Word[] items;

        private Builder() {
            items = new Word[0];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (buf.length != size) {
                throw new MoleculeException(size, buf.length, WordOptVec.class);
            }
            int[] offsets = MoleculeUtils.getOffsets(buf);
            items = new Word[offsets.length - 1];
            for (int i = 0; i < items.length; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, offsets[i], offsets[i + 1]);
                items[i] = (itemBuf.length == 0 ? null: Word.builder(itemBuf).build());
            }
        }

        public Builder add(@Nullable Word item) {
            Word[] originalItems = items;
            items = new Word[originalItems.length + 1];
            System.arraycopy(originalItems, 0, items, 0, originalItems.length);
            items[items.length - 1] = item;;
            return this;
        }

        public Builder add(@Nonnull Word[] items) {
            Objects.requireNonNull(items);
            Word[] originalItems = this.items;
            this.items = new Word[originalItems.length + items.length];
            System.arraycopy(originalItems, 0, this.items, 0, originalItems.length);
            System.arraycopy(items, 0, this.items, originalItems.length, items.length);
            return this;
        }

        public Builder set(int i, @Nullable Word item) {
            items[i] = item;
            return this;
        }

        public Builder remove(int i) {
            if (i < 0 || i >= items.length) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            Word[] originalItems = items;
            items = new Word[originalItems.length - 1];
            System.arraycopy(originalItems, 0, items, 0, i);
            System.arraycopy(originalItems, i + 1, items, i, originalItems.length - i -1);
            return this;
        }

        public WordOptVec build() {
            int size = 4 + 4 * items.length;
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    size += items[i].getSize();
                }
            }
            byte[] buf = new byte[size];
            MoleculeUtils.setInt(size, buf, 0);;
            int offset = 4 + 4 * items.length;
            int start = 4;
            for (int i = 0; i < items.length; i++) {
                MoleculeUtils.setInt(offset, buf, start);;
                if (items[i] != null) {
                    offset += items[i].getSize();
                }
                start += 4;
            }
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    MoleculeUtils.setBytes(items[i].toByteArray(), buf, start);;
                    start += items[i].getSize();
                }
            }
            WordOptVec v = new WordOptVec();
            v.buf = buf;
            v.items = items;
            return v;
        }
    }
}
