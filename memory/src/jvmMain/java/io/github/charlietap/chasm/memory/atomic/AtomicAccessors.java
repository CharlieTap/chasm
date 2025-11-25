package io.github.charlietap.chasm.memory.atomic;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class AtomicAccessors {

    private static final VarHandle INT_HANDLE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle SHORT_HANDLE = MethodHandles.byteBufferViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    public static final int BYTE_MASK = 0xFF;
    public static final int SHORT_MASK = 0xFFFF;
    public static final long INT_MASK = 0xFFFFFFFFL;
    public static final long LONG_BYTE_MASK = 0xFFL;
    public static final long LONG_SHORT_MASK = 0xFFFFL;

    private AtomicAccessors() {}

    public static void checkAlignment(ByteBuffer buffer) {
        if (buffer.capacity() >= 8) {
            LONG_HANDLE.getVolatile(buffer, 0);
        }
    }

    public static int loadI32(ByteBuffer buffer, int address) {
        return (int) INT_HANDLE.getVolatile(buffer, address);
    }

    public static int loadI32_8U(ByteBuffer buffer, int address) {
        int alignedAddress = address & ~0x3;
        int value = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
        int shift = (address & 0x3) << 3;
        return (value >>> shift) & BYTE_MASK;
    }

    public static int loadI32_16U(ByteBuffer buffer, int address) {
        return ((int) SHORT_HANDLE.getVolatile(buffer, address)) & SHORT_MASK;
    }

    public static long loadI64(ByteBuffer buffer, int address) {
        return (long) LONG_HANDLE.getVolatile(buffer, address);
    }

    public static long loadI64_8U(ByteBuffer buffer, int address) {
        int alignedAddress = address & ~0x3;
        int value = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
        int shift = (address & 0x3) << 3;
        return ((long) (value >>> shift)) & LONG_BYTE_MASK;
    }

    public static long loadI64_16U(ByteBuffer buffer, int address) {
        return ((long) SHORT_HANDLE.getVolatile(buffer, address)) & LONG_SHORT_MASK;
    }

    public static long loadI64_32U(ByteBuffer buffer, int address) {
        return ((long) INT_HANDLE.getVolatile(buffer, address)) & INT_MASK;
    }

    public static void storeI32(ByteBuffer buffer, int address, int value) {
        INT_HANDLE.setVolatile(buffer, address, value);
    }

    public static void storeI32_8(ByteBuffer buffer, int address, int value) {
        storeSubWord(buffer, address, value, BYTE_MASK);
    }

    public static void storeI32_16(ByteBuffer buffer, int address, int value) {
        storeSubWord(buffer, address, value, SHORT_MASK);
    }

    public static void storeI64(ByteBuffer buffer, int address, long value) {
        LONG_HANDLE.setVolatile(buffer, address, value);
    }

    public static void storeI64_8(ByteBuffer buffer, int address, long value) {
        storeSubWord(buffer, address, (int) value, BYTE_MASK);
    }

    public static void storeI64_16(ByteBuffer buffer, int address, long value) {
        storeSubWord(buffer, address, (int) value, SHORT_MASK);
    }

    public static void storeI64_32(ByteBuffer buffer, int address, long value) {
        int narrowed = (int) (value & INT_MASK);
        INT_HANDLE.setVolatile(buffer, address, narrowed);
    }

    private static void storeSubWord(ByteBuffer buffer, int address, int value, int valueMask) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = valueMask << shift;
        int bits = (value & valueMask) << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int updated = (current & ~mask) | bits;
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return;
            }
        }
    }

    public static int compareExchangeI32(ByteBuffer buffer, int address, int expected, int replacement) {
        return (int) INT_HANDLE.compareAndExchange(buffer, address, expected, replacement);
    }

    public static int compareExchangeI32_8(ByteBuffer buffer, int address, int expected, int replacement) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = BYTE_MASK << shift;
        int expectedByte = expected & BYTE_MASK;
        int replacementByte = (replacement & BYTE_MASK) << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentByte = (current >>> shift) & BYTE_MASK;
            if (currentByte != expectedByte) {
                return currentByte;
            }
            int updated = (current & ~mask) | replacementByte;
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentByte;
            }
        }
    }

    public static int compareExchangeI32_16(ByteBuffer buffer, int address, int expected, int replacement) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = SHORT_MASK << shift;
        int expectedShort = expected & SHORT_MASK;
        int replacementShort = (replacement & SHORT_MASK) << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentShort = (current >>> shift) & SHORT_MASK;
            if (currentShort != expectedShort) {
                return currentShort;
            }
            int updated = (current & ~mask) | replacementShort;
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentShort;
            }
        }
    }

    public static long compareExchangeI64(ByteBuffer buffer, int address, long expected, long replacement) {
        return (long) LONG_HANDLE.compareAndExchange(buffer, address, expected, replacement);
    }

    public static long compareExchangeI64_8(ByteBuffer buffer, int address, long expected, long replacement) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = BYTE_MASK << shift;
        int expectedByte = (int) expected & BYTE_MASK;
        int replacementByte = ((int) replacement & BYTE_MASK) << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentByte = (current >>> shift) & BYTE_MASK;
            if (currentByte != expectedByte) {
                return currentByte;
            }
            int updated = (current & ~mask) | replacementByte;
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentByte;
            }
        }
    }

    public static long compareExchangeI64_16(ByteBuffer buffer, int address, long expected, long replacement) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = SHORT_MASK << shift;
        int expectedShort = (int) expected & SHORT_MASK;
        int replacementShort = ((int) replacement & SHORT_MASK) << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentShort = (current >>> shift) & SHORT_MASK;
            if (currentShort != expectedShort) {
                return currentShort;
            }
            int updated = (current & ~mask) | replacementShort;
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentShort;
            }
        }
    }

    public static long compareExchangeI64_32(ByteBuffer buffer, int address, long expected, long replacement) {
        int expectedInt = (int) (expected & INT_MASK);
        int replacementInt = (int) (replacement & INT_MASK);
        int result = (int) INT_HANDLE.compareAndExchange(buffer, address, expectedInt, replacementInt);
        return ((long) result) & INT_MASK;
    }

    public static int rmwAddI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndAdd(buffer, address, value);
    }

    public static int rmwSubI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndAdd(buffer, address, -value);
    }

    public static int rmwAndI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndBitwiseAnd(buffer, address, value);
    }

    public static int rmwOrI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndBitwiseOr(buffer, address, value);
    }

    public static int rmwXorI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndBitwiseXor(buffer, address, value);
    }

    public static int rmwExchangeI32(ByteBuffer buffer, int address, int value) {
        return (int) INT_HANDLE.getAndSet(buffer, address, value);
    }

    public static int rmwAddI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.ADD);
    }

    public static int rmwSubI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.SUB);
    }

    public static int rmwAndI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.AND);
    }

    public static int rmwOrI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.OR);
    }

    public static int rmwXorI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.XOR);
    }

    public static int rmwExchangeI32_8(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, BYTE_MASK, RmwOp.XCHG);
    }

    public static int rmwAddI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.ADD);
    }

    public static int rmwSubI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.SUB);
    }

    public static int rmwAndI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.AND);
    }

    public static int rmwOrI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.OR);
    }

    public static int rmwXorI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.XOR);
    }

    public static int rmwExchangeI32_16(ByteBuffer buffer, int address, int value) {
        return rmwSubWordI32(buffer, address, value, SHORT_MASK, RmwOp.XCHG);
    }

    private enum RmwOp { ADD, SUB, AND, OR, XOR, XCHG }

    private static int rmwSubWordI32(ByteBuffer buffer, int address, int value, int valueMask, RmwOp op) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = valueMask << shift;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentSubWord = (current >>> shift) & valueMask;
            int newSubWord;
            switch (op) {
                case ADD:  newSubWord = (currentSubWord + (value & valueMask)) & valueMask; break;
                case SUB:  newSubWord = (currentSubWord - (value & valueMask)) & valueMask; break;
                case AND:  newSubWord = currentSubWord & (value & valueMask); break;
                case OR:   newSubWord = currentSubWord | (value & valueMask); break;
                case XOR:  newSubWord = currentSubWord ^ (value & valueMask); break;
                case XCHG: newSubWord = value & valueMask; break;
                default:   newSubWord = currentSubWord; break;
            }
            int updated = (current & ~mask) | (newSubWord << shift);
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentSubWord;
            }
        }
    }

    public static long rmwAddI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndAdd(buffer, address, value);
    }

    public static long rmwSubI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndAdd(buffer, address, -value);
    }

    public static long rmwAndI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndBitwiseAnd(buffer, address, value);
    }

    public static long rmwOrI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndBitwiseOr(buffer, address, value);
    }

    public static long rmwXorI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndBitwiseXor(buffer, address, value);
    }

    public static long rmwExchangeI64(ByteBuffer buffer, int address, long value) {
        return (long) LONG_HANDLE.getAndSet(buffer, address, value);
    }

    public static long rmwAddI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.ADD);
    }

    public static long rmwSubI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.SUB);
    }

    public static long rmwAndI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.AND);
    }

    public static long rmwOrI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.OR);
    }

    public static long rmwXorI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.XOR);
    }

    public static long rmwExchangeI64_8(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, BYTE_MASK, RmwOp.XCHG);
    }

    public static long rmwAddI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.ADD);
    }

    public static long rmwSubI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.SUB);
    }

    public static long rmwAndI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.AND);
    }

    public static long rmwOrI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.OR);
    }

    public static long rmwXorI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.XOR);
    }

    public static long rmwExchangeI64_16(ByteBuffer buffer, int address, long value) {
        return rmwSubWordI64(buffer, address, value, SHORT_MASK, RmwOp.XCHG);
    }

    public static long rmwAddI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndAdd(buffer, address, (int) value);
        return ((long) result) & INT_MASK;
    }

    public static long rmwSubI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndAdd(buffer, address, (int) -value);
        return ((long) result) & INT_MASK;
    }

    public static long rmwAndI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndBitwiseAnd(buffer, address, (int) value);
        return ((long) result) & INT_MASK;
    }

    public static long rmwOrI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndBitwiseOr(buffer, address, (int) value);
        return ((long) result) & INT_MASK;
    }

    public static long rmwXorI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndBitwiseXor(buffer, address, (int) value);
        return ((long) result) & INT_MASK;
    }

    public static long rmwExchangeI64_32(ByteBuffer buffer, int address, long value) {
        int result = (int) INT_HANDLE.getAndSet(buffer, address, (int) value);
        return ((long) result) & INT_MASK;
    }

    private static long rmwSubWordI64(ByteBuffer buffer, int address, long value, int valueMask, RmwOp op) {
        int alignedAddress = address & ~0x3;
        int shift = (address & 0x3) << 3;
        int mask = valueMask << shift;
        int valueInt = (int) value & valueMask;

        while (true) {
            int current = (int) INT_HANDLE.getVolatile(buffer, alignedAddress);
            int currentSubWord = (current >>> shift) & valueMask;
            int newSubWord;
            switch (op) {
                case ADD:  newSubWord = (currentSubWord + valueInt) & valueMask; break;
                case SUB:  newSubWord = (currentSubWord - valueInt) & valueMask; break;
                case AND:  newSubWord = currentSubWord & valueInt; break;
                case OR:   newSubWord = currentSubWord | valueInt; break;
                case XOR:  newSubWord = currentSubWord ^ valueInt; break;
                case XCHG: newSubWord = valueInt; break;
                default:   newSubWord = currentSubWord; break;
            }
            int updated = (current & ~mask) | (newSubWord << shift);
            if (INT_HANDLE.compareAndSet(buffer, alignedAddress, current, updated)) {
                return currentSubWord;
            }
        }
    }
}
