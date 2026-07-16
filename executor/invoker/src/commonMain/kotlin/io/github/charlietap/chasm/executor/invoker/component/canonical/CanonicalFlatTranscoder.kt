package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo

internal fun CanonicalFlatTranscoder(
    runtimeInfo: ComponentRuntimeInfo,
    tuple: CanonicalValueTupleLayout,
    source: LongArray,
    sourceCount: Int,
    destination: LongArray,
): Int {
    if (sourceCount != tuple.flatCount || source.size < sourceCount || destination.size < sourceCount) {
        invalidValue("canonical flat value count does not match its type")
    }

    var offset = 0
    tuple.layouts.forEach { layout ->
        offset = transcodeFlat(runtimeInfo, layout, source, destination, offset)
    }
    if (offset != sourceCount) invalidValue("canonical flat value count does not match its type")
    return offset
}

private fun transcodeFlat(
    runtimeInfo: ComponentRuntimeInfo,
    layoutIndex: Int,
    source: LongArray,
    destination: LongArray,
    offset: Int,
): Int {
    val layout = runtimeInfo.linearMemoryLayouts[layoutIndex]
    return when (layout.kind) {
        CanonicalLayoutKind.Bool -> put(destination, offset, if (source[offset].toInt() == 0) 0L else 1L)
        CanonicalLayoutKind.S8 -> put(destination, offset, source[offset].toByte().toLong())
        CanonicalLayoutKind.U8 -> put(destination, offset, source[offset].toUByte().toLong())
        CanonicalLayoutKind.S16 -> put(destination, offset, source[offset].toShort().toLong())
        CanonicalLayoutKind.U16 -> put(destination, offset, source[offset].toUShort().toLong())
        CanonicalLayoutKind.S32 -> put(destination, offset, source[offset].toInt().toLong())
        CanonicalLayoutKind.U32 -> put(destination, offset, source[offset].toUInt().toLong())
        CanonicalLayoutKind.S64,
        CanonicalLayoutKind.U64,
        -> put(destination, offset, source[offset])
        CanonicalLayoutKind.F32 -> put(
            destination,
            offset,
            CanonicalFloat(Float.fromBits(source[offset].toInt())).toRawBits().toLong(),
        )
        CanonicalLayoutKind.F64 -> put(
            destination,
            offset,
            CanonicalDouble(Double.fromBits(source[offset])).toRawBits(),
        )
        CanonicalLayoutKind.Char -> {
            val codePoint = source[offset].toUInt()
            if (codePoint > MAX_UNICODE_CODE_POINT || codePoint in SURROGATE_RANGE) {
                invalidValue("canonical char is not a Unicode scalar value")
            }
            put(destination, offset, codePoint.toLong())
        }
        CanonicalLayoutKind.Flags -> {
            val bits = source[offset].toUInt()
            val mask = if (layout.elementCount == UInt.SIZE_BITS) UInt.MAX_VALUE else (1u shl layout.elementCount) - 1u
            put(destination, offset, (bits and mask).toLong())
        }
        CanonicalLayoutKind.Enum -> {
            val caseIndex = source[offset].toInt()
            if (caseIndex !in 0 until layout.elementCount) invalidValue("canonical enum discriminant is out of range")
            put(destination, offset, caseIndex.toLong())
        }
        CanonicalLayoutKind.Record,
        CanonicalLayoutKind.Tuple,
        -> layout.children.fold(offset) { next, child ->
            transcodeFlat(runtimeInfo, child, source, destination, next)
        }
        CanonicalLayoutKind.String,
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.List,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> invalidValue("canonical value is not eligible for direct adapter fusion")
    }
}

private fun put(
    destination: LongArray,
    offset: Int,
    value: Long,
): Int {
    destination[offset] = value
    return offset + 1
}

private const val MAX_UNICODE_CODE_POINT = 0x10ffffu
private val SURROGATE_RANGE = 0xd800u..0xdfffu
