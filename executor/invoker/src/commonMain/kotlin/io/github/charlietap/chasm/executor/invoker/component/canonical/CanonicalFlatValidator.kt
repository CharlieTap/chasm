package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo

internal fun CanonicalFlatLiftValidator(
    runtimeInfo: ComponentRuntimeInfo,
    tuple: CanonicalValueTupleLayout,
    values: LongArray,
    valueCount: Int,
) = validateFlatTuple(runtimeInfo, tuple, values, valueCount, lifting = true)

internal fun CanonicalFlatLowerValidator(
    runtimeInfo: ComponentRuntimeInfo,
    tuple: CanonicalValueTupleLayout,
    values: LongArray,
    valueCount: Int,
) = validateFlatTuple(runtimeInfo, tuple, values, valueCount, lifting = false)

private fun validateFlatTuple(
    runtimeInfo: ComponentRuntimeInfo,
    tuple: CanonicalValueTupleLayout,
    values: LongArray,
    valueCount: Int,
    lifting: Boolean,
) {
    if (valueCount != tuple.flatCount || values.size < valueCount) {
        invalidValue("canonical flat value count does not match its type")
    }
    var offset = 0
    tuple.layouts.forEach { layout -> offset = validateFlat(runtimeInfo, layout, values, offset, lifting) }
    if (offset != valueCount) invalidValue("canonical flat value count does not match its type")
}

private fun validateFlat(
    runtimeInfo: ComponentRuntimeInfo,
    layoutIndex: Int,
    values: LongArray,
    offset: Int,
    lifting: Boolean,
): Int {
    val layout = runtimeInfo.linearMemoryLayouts[layoutIndex]
    return when (layout.kind) {
        CanonicalLayoutKind.Bool -> {
            val value = checkedValue(values, offset).toInt()
            if (lifting) {
                values[offset] = if (value == 0) 0L else 1L
            } else if (value !in 0..1) {
                invalidValue("canonical bool must be zero or one")
            }
            offset + 1
        }
        CanonicalLayoutKind.S8,
        CanonicalLayoutKind.U8,
        CanonicalLayoutKind.S16,
        CanonicalLayoutKind.U16,
        CanonicalLayoutKind.S32,
        CanonicalLayoutKind.U32,
        CanonicalLayoutKind.S64,
        CanonicalLayoutKind.U64,
        CanonicalLayoutKind.F32,
        CanonicalLayoutKind.F64,
        -> checkedNext(values, offset)
        CanonicalLayoutKind.Char -> {
            val codePoint = checkedValue(values, offset).toUInt()
            if (codePoint > MAX_UNICODE_CODE_POINT || codePoint in SURROGATE_RANGE) {
                invalidValue("canonical char is not a Unicode scalar value")
            }
            offset + 1
        }
        CanonicalLayoutKind.Enum -> {
            val caseIndex = checkedValue(values, offset).toInt()
            if (caseIndex !in 0 until layout.elementCount) invalidValue("canonical enum discriminant is out of range")
            offset + 1
        }
        CanonicalLayoutKind.Record,
        CanonicalLayoutKind.Tuple,
        -> layout.children.fold(offset) { next, child -> validateFlat(runtimeInfo, child, values, next, lifting) }
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        -> {
            val caseIndex = checkedValue(values, offset).toInt()
            if (caseIndex !in layout.children.indices) invalidValue("canonical variant discriminant is out of range")
            val child = layout.children[caseIndex]
            if (child != ABSENT_LAYOUT) validateFlat(runtimeInfo, child, values, offset + 1, lifting)
            offset + layout.shape.flatTypes.size
        }
        CanonicalLayoutKind.Flags -> {
            val bits = checkedValue(values, offset).toUInt()
            val mask = if (layout.elementCount == UInt.SIZE_BITS) UInt.MAX_VALUE else (1u shl layout.elementCount) - 1u
            if (lifting) {
                values[offset] = (bits and mask).toLong()
            } else if (bits and mask.inv() != 0u) {
                invalidValue("canonical flags contain undefined bits")
            }
            offset + 1
        }
        CanonicalLayoutKind.String,
        CanonicalLayoutKind.List,
        -> invalidValue("prepared host functions do not support memory values")
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> invalidValue("prepared host functions do not support resource values")
    }
}

private fun checkedNext(values: LongArray, offset: Int): Int {
    checkedValue(values, offset)
    return offset + 1
}

private fun checkedValue(values: LongArray, offset: Int): Long =
    values.getOrNull(offset) ?: invalidValue("canonical flat value count does not match its type")

private const val ABSENT_LAYOUT = -1
private const val MAX_UNICODE_CODE_POINT = 0x10ffffu
private val SURROGATE_RANGE = 0xd800u..0xdfffu
