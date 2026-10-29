@file:JvmName("CompiledMemory")
@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
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
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.operations.effectiveAddress
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
) = LinearMemoryCopier(source.data, destination.data, sourceOffset, destinationOffset, count, source.size, destination.size)

fun memoryFill(
    memory: MemoryInstance,
    offset: Int,
    value: Int,
    count: Int,
) = LinearMemoryFiller(memory.data, offset, count, value.toByte(), memory.size)

private inline fun <T> load(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline read: (Int) -> T,
): T {
    val effectiveAddress = effectiveAddress(address, offset)
    return OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) { read(effectiveAddress) }
}

private inline fun store(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline write: (Int) -> Unit,
) {
    val effectiveAddress = effectiveAddress(address, offset)
    OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) { write(effectiveAddress) }
}
