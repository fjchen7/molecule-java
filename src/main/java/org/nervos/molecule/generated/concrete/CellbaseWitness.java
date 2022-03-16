package org.nervos.molecule.generated.concrete;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.nervos.molecule.generated.base.MoleculeException;
import org.nervos.molecule.generated.base.MoleculeUtils;
import org.nervos.molecule.generated.base.Table;

public final class CellbaseWitness extends Table {
  public static int FIELD_COUNT = 2;

  private Script lock;

  private Bytes message;

  private CellbaseWitness() {
  }

  @Nonnull
  public Script getLock() {
    return lock;
  }

  @Nonnull
  public Bytes getMessage() {
    return message;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(@Nonnull byte[] buf) {
    return new Builder(buf);
  }

  public static final class Builder {
    private Script lock;

    private Bytes message;

    private Builder() {
      lock = Script.builder().build();
      message = Bytes.builder().build();
    }

    private Builder(@Nonnull byte[] buf) {
      Objects.requireNonNull(buf);
      int size = MoleculeUtils.littleEndianBytes4ToInt(buf, 0);
      if (buf.length != size) {
        throw new MoleculeException(size, buf.length, CellbaseWitness.class);
      }
      int[] offsets = MoleculeUtils.getOffsets(buf);
      if (offsets.length - 1 != FIELD_COUNT) {
        throw new MoleculeException("Raw data should have " + FIELD_COUNT + " but find " + (offsets.length -1) + " offsets in header.");
      }
      byte[] itemBuf;
      itemBuf = Arrays.copyOfRange(buf, offsets[0], offsets[1]);
      lock = Script.builder(itemBuf).build();
      itemBuf = Arrays.copyOfRange(buf, offsets[1], offsets[2]);
      message = Bytes.builder(itemBuf).build();
    }

    public Builder setLock(@Nonnull Script lock) {
      Objects.requireNonNull(lock);
      this.lock = lock;
      return this;
    }

    public Builder setMessage(@Nonnull Bytes message) {
      Objects.requireNonNull(message);
      this.message = message;
      return this;
    }

    public CellbaseWitness build() {
      int[] offsets = new int[FIELD_COUNT];
      offsets[0] = 4 + 4 * FIELD_COUNT;
      offsets[1] = offsets[0] + message.getSize();
      int[] fieldsSize = new int[FIELD_COUNT];
      fieldsSize[0] = lock.getSize();
      fieldsSize[1] = message.getSize();
      byte[][] fieldsBuf = new byte[FIELD_COUNT][];
      fieldsBuf[0] = lock.getRawData();
      fieldsBuf[1] = message.getRawData();
      int size = 4 + 4 * FIELD_COUNT;
      for (int i = 0; i < FIELD_COUNT; i++) {
        size += fieldsSize[i];
      }
      byte[] buf = new byte[size];;
      MoleculeUtils.setSize(size, buf, 0);
      int start = 4;
      for (int i = 0; i < FIELD_COUNT; i++) {
        MoleculeUtils.setSize(fieldsSize[i], buf, start);
        start += 4;
      }
      for (int i = 0; i < FIELD_COUNT; i++) {
        MoleculeUtils.setBytes(fieldsBuf[i], buf, offsets[i]);
      }
      CellbaseWitness t = new CellbaseWitness();
      t.buf = buf;
      t.lock = lock;
      t.message = message;
      return t;
    }
  }
}
