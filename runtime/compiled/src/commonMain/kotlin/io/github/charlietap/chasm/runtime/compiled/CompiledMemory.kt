@file:JvmName("CompiledMemory")
@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I6416SReader
import io.github.charlietap.chasm.memory.read.I6416UReader
import io.github.charlietap.chasm.memory.read.I6432SReader
import io.github.charlietap.chasm.memory.read.I6432UReader
import io.github.charlietap.chasm.memory.read.I648SReader
import io.github.charlietap.chasm.memory.read.I648UReader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64ToI16Writer
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import java.nio.BufferOverflowException
import java.nio.BufferUnderflowException
import kotlin.jvm.JvmName

fun i32Load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Int = load(memory, address, offset, Int.SIZE_BYTES) { I32Reader(memory.data, it) }

fun i64Load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Long.SIZE_BYTES) { I64Reader(memory.data, it) }

fun f32Load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Float = load(memory, address, offset, Float.SIZE_BYTES) { F32Reader(memory.data, it) }

fun f64Load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Double = load(memory, address, offset, Double.SIZE_BYTES) { F64Reader(memory.data, it) }

fun i32Load8S(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Int = load(memory, address, offset, Byte.SIZE_BYTES) { I328SReader(memory.data, it) }

fun i32Load8U(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Int = load(memory, address, offset, Byte.SIZE_BYTES) { I328UReader(memory.data, it) }

fun i32Load16S(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Int = load(memory, address, offset, Short.SIZE_BYTES) { I3216SReader(memory.data, it) }

fun i32Load16U(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Int = load(memory, address, offset, Short.SIZE_BYTES) { I3216UReader(memory.data, it) }

fun i64Load8S(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Byte.SIZE_BYTES) { I648SReader(memory.data, it) }

fun i64Load8U(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Byte.SIZE_BYTES) { I648UReader(memory.data, it) }

fun i64Load16S(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Short.SIZE_BYTES) { I6416SReader(memory.data, it) }

fun i64Load16U(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Short.SIZE_BYTES) { I6416UReader(memory.data, it) }

fun i64Load32S(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Int.SIZE_BYTES) { I6432SReader(memory.data, it) }

fun i64Load32U(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
): Long = load(memory, address, offset, Int.SIZE_BYTES) { I6432UReader(memory.data, it) }

fun i32Store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Int,
) = store(memory, address, offset, Int.SIZE_BYTES) { I32Writer(memory.data, it, value) }

fun i64Store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Long,
) = store(memory, address, offset, Long.SIZE_BYTES) { I64Writer(memory.data, it, value) }

fun f32Store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Float,
) = store(memory, address, offset, Float.SIZE_BYTES) { F32Writer(memory.data, it, value) }

fun f64Store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Double,
) = store(memory, address, offset, Double.SIZE_BYTES) { F64Writer(memory.data, it, value) }

fun i32Store8(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Int,
) = store(memory, address, offset, Byte.SIZE_BYTES) { I32ToI8Writer(memory.data, it, value) }

fun i32Store16(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Int,
) = store(memory, address, offset, Short.SIZE_BYTES) { I32ToI16Writer(memory.data, it, value) }

fun i64Store8(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Long,
) = store(memory, address, offset, Byte.SIZE_BYTES) { I64ToI8Writer(memory.data, it, value) }

fun i64Store16(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Long,
) = store(memory, address, offset, Short.SIZE_BYTES) { I64ToI16Writer(memory.data, it, value) }

fun i64Store32(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    value: Long,
) = store(memory, address, offset, Int.SIZE_BYTES) { I64ToI32Writer(memory.data, it, value) }

fun unsafe(): Any = UNSAFE ?: throw UnsupportedOperationException("sun.misc.Unsafe is not available")

fun byteArrayBase(): Long = BYTE_ARRAY_BASE

private val UNSAFE: Any? = try {
    Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null)
} catch (_: ReflectiveOperationException) {
    null
}

private val BYTE_ARRAY_BASE: Long = UNSAFE?.let { unsafe ->
    (unsafe.javaClass.getMethod("arrayBaseOffset", Class::class.java).invoke(unsafe, ByteArray::class.java) as Int).toLong()
} ?: -1L

fun memoryCopy(
    source: MemoryInstance,
    destination: MemoryInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) = try {
    LinearMemoryCopier(source.data, destination.data, sourceOffset, destinationOffset, count, source.size, destination.size)
} catch (exception: InvocationException) {
    throw copyFailed(exception, source, destination, sourceOffset, destinationOffset, count)
}

fun memoryFill(
    memory: MemoryInstance,
    offset: Int,
    value: Int,
    count: Int,
) = try {
    LinearMemoryFiller(memory.data, offset, count, value.toByte(), memory.size)
} catch (exception: InvocationException) {
    throw accessFailed(exception, FailedMemoryAccess.Kind.FILL, memory, unsigned(offset), unsigned(count))
}

/** Traps for an inline access compiled code found out of bounds, recording which access it was. */
fun memoryAccessOutOfBounds(
    memory: MemoryInstance,
    kind: Int,
    address: Long,
    width: Int,
): Nothing = throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    .failedAccess(FailedMemoryAccess(FailedMemoryAccess.Kind.entries[kind], memory, address, width.toLong()))

// The JVM checks each access against the memory's bounds, so a negative address or offset, which Wasm reads as unsigned,
// is the only check left. Failures are handled out of line to keep these small enough to inline into compiled code.
private inline fun <T> load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline read: (Int) -> T,
): T {
    if ((address or offset) < 0) throw outOfBounds(FailedMemoryAccess.Kind.READ, memory, address, offset, bytes)
    return try {
        read(address + offset)
    } catch (exception: RuntimeException) {
        throw outOfBounds(exception, FailedMemoryAccess.Kind.READ, memory, address, offset, bytes)
    }
}

private inline fun store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline write: (Int) -> Unit,
) {
    if ((address or offset) < 0) throw outOfBounds(FailedMemoryAccess.Kind.WRITE, memory, address, offset, bytes)
    try {
        write(address + offset)
    } catch (exception: RuntimeException) {
        throw outOfBounds(exception, FailedMemoryAccess.Kind.WRITE, memory, address, offset, bytes)
    }
}

private fun outOfBounds(
    kind: FailedMemoryAccess.Kind,
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
): InvocationException = accessFailed(
    InvocationException(InvocationError.MemoryOperationOutOfBounds),
    kind,
    memory,
    unsigned(address) + unsigned(offset),
    bytes.toLong(),
)

// Only what the JVM's bounds checks throw is a trap; anything else is rethrown as it was.
private fun outOfBounds(
    exception: RuntimeException,
    kind: FailedMemoryAccess.Kind,
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
): Exception = when (exception) {
    is IndexOutOfBoundsException,
    is IllegalArgumentException,
    is BufferOverflowException,
    is BufferUnderflowException,
    -> outOfBounds(kind, memory, address, offset, bytes)
    else -> exception
}

internal fun accessFailed(
    exception: InvocationException,
    kind: FailedMemoryAccess.Kind,
    memory: MemoryInstance,
    address: Long,
    width: Long,
    source: Boolean = false,
): InvocationException = exception.failedAccess(FailedMemoryAccess(kind, memory, address, width, source))

// An invalid source range is checked first, so it is the one reported when both are.
private fun copyFailed(
    exception: InvocationException,
    source: MemoryInstance,
    destination: MemoryInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
): InvocationException = if (unsigned(sourceOffset) + unsigned(count) > source.size) {
    accessFailed(exception, FailedMemoryAccess.Kind.COPY, source, unsigned(sourceOffset), unsigned(count), source = true)
} else {
    accessFailed(exception, FailedMemoryAccess.Kind.COPY, destination, unsigned(destinationOffset), unsigned(count))
}

internal fun unsigned(
    value: Int,
): Long = value.toLong() and 0xFFFF_FFFFL
