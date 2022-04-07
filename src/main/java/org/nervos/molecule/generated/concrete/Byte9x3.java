package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.Array;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;

public final class Byte9x3 extends Array {
    public static Class ITEM_TYPE = Byte9.class;

    public static int ITEM_SIZE = Byte9.SIZE;

    public static int ITEM_COUNT = 3;

    public static int SIZE = ITEM_SIZE * ITEM_COUNT;

    private Byte9[] items;

    private Byte9x3() {
    }

    @Nonnull
    public Byte9 get(int i) {
        return items[i];
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    @Override
    public int getItemSize() {
        return ITEM_SIZE;
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
        private Byte9[] items;

        private Builder() {
            items = new Byte9[ITEM_COUNT];
            for (int i = 0; i < ITEM_COUNT; i++) {
                items[i] = Byte9.builder().build();
            }
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, Byte9x3.class);
            }
            items = new Byte9[ITEM_COUNT];
            for (int i = 0; i < ITEM_COUNT; i++) {
                byte[] itemBuf = Arrays.copyOfRange(buf, i * ITEM_SIZE, (i + 1) * ITEM_SIZE);
                items[i] = Byte9.builder(itemBuf).build();
            }
        }

        public Builder set(int i, @Nonnull Byte9 item) {
            Objects.requireNonNull(item);
            items[i] = item;
            return this;
        }

        public Byte9x3 build() {
            byte[] buf = new byte[SIZE];
            for (int i = 0; i < ITEM_COUNT; i++) {
                MoleculeUtils.setBytes(items[i].toByteArray(), buf, i * ITEM_SIZE);
            }
            Byte9x3 a = new Byte9x3();
            a.buf = buf;
            a.items = items;
            return a;
        }
    }
}
