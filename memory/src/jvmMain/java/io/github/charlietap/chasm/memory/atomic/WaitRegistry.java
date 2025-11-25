package io.github.charlietap.chasm.memory.atomic;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class WaitRegistry {

    private static final VarHandle INT_HANDLE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    public static final int WAIT_OK = 0;
    public static final int WAIT_MISMATCH = 1;
    public static final int WAIT_TIMEOUT = 2;

    private final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<WaitEntry>> waitQueues = new ConcurrentHashMap<>();

    private static final class WaitEntry {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        volatile boolean notified = false;
    }

    public int waitI32(ByteBuffer buffer, int address, int expected, long timeoutNanos) {
        int current = (int) INT_HANDLE.getVolatile(buffer, address);
        if (current != expected) {
            return WAIT_MISMATCH;
        }

        return doWait(address, timeoutNanos);
    }

    public int waitI64(ByteBuffer buffer, int address, long expected, long timeoutNanos) {
        long current = (long) LONG_HANDLE.getVolatile(buffer, address);
        if (current != expected) {
            return WAIT_MISMATCH;
        }

        return doWait(address, timeoutNanos);
    }

    private int doWait(int address, long timeoutNanos) {

        WaitEntry entry = new WaitEntry();
        ConcurrentLinkedQueue<WaitEntry> queue = waitQueues.computeIfAbsent(address, k -> new ConcurrentLinkedQueue<>());
        queue.add(entry);

        entry.lock.lock();
        try {
            if (timeoutNanos < 0) {
                // Infinite wait
                while (!entry.notified) {
                    entry.condition.awaitUninterruptibly();
                }
                return WAIT_OK;
            } else {
                long remaining = timeoutNanos;
                while (!entry.notified && remaining > 0) {
                    try {
                        remaining = entry.condition.awaitNanos(remaining);
                    } catch (InterruptedException e) {
                        // Continue waiting, don't allow spurious wakeups
                        Thread.currentThread().interrupt();
                    }
                }
                return entry.notified ? WAIT_OK : WAIT_TIMEOUT;
            }
        } finally {
            entry.lock.unlock();
            if (!entry.notified) {
                queue.remove(entry);
            }
        }
    }

    public int notify(int address, int count) {
        ConcurrentLinkedQueue<WaitEntry> queue = waitQueues.get(address);
        if (queue == null) {
            return 0;
        }

        int woken = 0;
        for (int i = 0; i < count; i++) {
            WaitEntry entry = queue.poll();
            if (entry == null) {
                break;
            }

            entry.lock.lock();
            try {
                entry.notified = true;
                entry.condition.signal();
            } finally {
                entry.lock.unlock();
            }
            woken++;
        }

        return woken;
    }

    public static void fence() {
        VarHandle.fullFence();
    }
}

