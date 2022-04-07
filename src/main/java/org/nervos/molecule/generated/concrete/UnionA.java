package org.nervos.molecule.generated.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.nervos.molecule.generated.base.Molecule;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Union;

public final class UnionA extends Union {
    public static List<Class> ITEM_CLASSES = new ArrayList<>();

    static {
        ITEM_CLASSES.add(byte.class);
        ITEM_CLASSES.add(Word.class);
        ITEM_CLASSES.add(StructA.class);
        ITEM_CLASSES.add(Bytes.class);
        ITEM_CLASSES.add(Words.class);
        ITEM_CLASSES.add(Table0.class);
        ITEM_CLASSES.add(Table6.class);
        ITEM_CLASSES.add(Table6.class);
        ITEM_CLASSES = Collections.unmodifiableList(ITEM_CLASSES);
    }

    private UnionA() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private int typeId;

        private Object item;

        private Builder() {
            typeId = 0;
            item = (byte) 0;
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            typeId = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
            if (typeId >= ITEM_CLASSES.size()) {
                throw new MoleculeException("Type Id of Union type UnionA should range from 0 to " + (ITEM_CLASSES.size() -1));
            }
            byte[] itemBuf = new byte[buf.length - 4];
            System.arraycopy(buf, 4, itemBuf, 0, buf.length - 4);
            if (typeId == 0) {
                if (itemBuf.length == 1) {
                    item = itemBuf[0];
                } else {
                    throw new MoleculeException(1, itemBuf.length, byte.class);
                }
            } else if (typeId == 1) {
                item = Word.builder(itemBuf).build();
            } else if (typeId == 2) {
                item = StructA.builder(itemBuf).build();
            } else if (typeId == 3) {
                item = Bytes.builder(itemBuf).build();
            } else if (typeId == 4) {
                item = Words.builder(itemBuf).build();
            } else if (typeId == 5) {
                item = Table0.builder(itemBuf).build();
            } else if (typeId == 6) {
                item = Table6.builder(itemBuf).build();
            } else if (typeId == 7) {
                if (itemBuf.length == 0) {
                    item = null;
                } else {
                    item = Table6.builder(itemBuf).build();
                }
            }
        }

        public Builder toByte(@Nonnull byte item) {
            Objects.requireNonNull(item);
            this.typeId = 0;
            this.item = item;
            return this;
        }

        public Builder toWord(@Nonnull Word item) {
            Objects.requireNonNull(item);
            this.typeId = 1;
            this.item = item;
            return this;
        }

        public Builder toStructA(@Nonnull StructA item) {
            Objects.requireNonNull(item);
            this.typeId = 2;
            this.item = item;
            return this;
        }

        public Builder toBytes(@Nonnull Bytes item) {
            Objects.requireNonNull(item);
            this.typeId = 3;
            this.item = item;
            return this;
        }

        public Builder toWords(@Nonnull Words item) {
            Objects.requireNonNull(item);
            this.typeId = 4;
            this.item = item;
            return this;
        }

        public Builder toTable0(@Nonnull Table0 item) {
            Objects.requireNonNull(item);
            this.typeId = 5;
            this.item = item;
            return this;
        }

        public Builder toTable6(@Nonnull Table6 item) {
            Objects.requireNonNull(item);
            this.typeId = 6;
            this.item = item;
            return this;
        }

        public Builder toTable6Opt(@Nullable Table6 item) {
            this.typeId = 7;
            this.item = item;
            return this;
        }

        public UnionA build() {
            byte[] buf;
            Class clazz = ITEM_CLASSES.get(typeId);
            if (item == null) {
                buf = new byte[4];
            } else if (clazz == byte.class) {
                buf = new byte[5];
                buf[4] = (byte) item;
            } else {
                Molecule molecule = (Molecule) item;
                buf = new byte[4 + molecule.getSize()];
                MoleculeUtils.setBytes(molecule.toByteArray(), buf, 4);
            }
            MoleculeUtils.setInt(typeId, buf, 0);
            UnionA u  = new UnionA();
            u.typeId = typeId;
            u.item = item;
            u.buf = buf;
            return u;
        }
    }
}
