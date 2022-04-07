package org.nervos.molecule.generated.concrete;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.Struct;

public final class Struct0 extends Struct {
    public static int SIZE = 0;

    public static int FIELD_COUNT = 0;

    private Struct0() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, Struct0.class);
            }
        }

        public Struct0 build() {
            byte[] buf = new byte[SIZE];
            Struct0 s = new Struct0();
            s.buf = buf;
            return s;
        }
    }
}
