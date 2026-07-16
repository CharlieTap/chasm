package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal fun CanonicalFlatLifter(
    context: CanonicalCallContext,
    tuple: CanonicalValueTupleLayout,
    source: LongArray,
): List<ComponentValue> {
    val cursor = FlatCursor(source)
    val values = tuple.layouts.map { layout -> liftFlat(context, layout, cursor) }
    if (cursor.index != tuple.flatCount) invalidValue("canonical flat value count does not match its type")
    return values
}

internal fun CanonicalFlatLowerer(
    context: CanonicalCallContext,
    tuple: CanonicalValueTupleLayout,
    values: List<ComponentValue>,
    destination: LongArray,
): Int {
    if (values.size != tuple.layouts.size) invalidValue("component argument count does not match its function type")
    var offset = 0
    tuple.layouts.forEachIndexed { index, layout ->
        offset = lowerFlat(context, layout, values[index], destination, offset)
    }
    if (offset != tuple.flatCount) invalidValue("canonical flat value count does not match its type")
    return offset
}

internal fun CanonicalMemoryLifter(
    context: CanonicalCallContext,
    tuple: CanonicalValueTupleLayout,
    pointer: Int,
): List<ComponentValue> {
    checkRegion(context.memory(), pointer, tuple.size32.toInt(), tuple.alignment32.toInt())
    return tuple.layouts.mapIndexed { index, layout ->
        load(context, layout, pointer + tuple.offsets32[index].toInt(), regionChecked = true)
    }
}

internal fun CanonicalMemoryLowerer(
    context: CanonicalCallContext,
    tuple: CanonicalValueTupleLayout,
    values: List<ComponentValue>,
    pointer: Int,
) {
    if (values.size != tuple.layouts.size) invalidValue("component result count does not match its function type")
    checkRegion(context.memory(), pointer, tuple.size32.toInt(), tuple.alignment32.toInt())
    tuple.layouts.forEachIndexed { index, layout ->
        store(context, layout, values[index], pointer + tuple.offsets32[index].toInt(), regionChecked = true)
    }
}

internal fun CanonicalMemoryAllocator(
    context: CanonicalCallContext,
    tuple: CanonicalValueTupleLayout,
    values: List<ComponentValue>,
): Int {
    val pointer = context.realloc(tuple.alignment32.toInt(), tuple.size32.toInt())
    CanonicalMemoryLowerer(context, tuple, values, pointer)
    return pointer
}

private fun liftFlat(
    context: CanonicalCallContext,
    layoutIndex: Int,
    cursor: FlatCursor,
): ComponentValue {
    val layout = context.runtimeInfo.linearMemoryLayouts[layoutIndex]
    return when (layout.kind) {
        CanonicalLayoutKind.Bool -> componentBool(cursor.next().toInt())
        CanonicalLayoutKind.S8 -> ComponentValue.S8(cursor.next().toByte())
        CanonicalLayoutKind.U8 -> ComponentValue.U8(cursor.next().toUByte())
        CanonicalLayoutKind.S16 -> ComponentValue.S16(cursor.next().toShort())
        CanonicalLayoutKind.U16 -> ComponentValue.U16(cursor.next().toUShort())
        CanonicalLayoutKind.S32 -> ComponentValue.S32(cursor.next().toInt())
        CanonicalLayoutKind.U32 -> ComponentValue.U32(cursor.next().toUInt())
        CanonicalLayoutKind.S64 -> ComponentValue.S64(cursor.next())
        CanonicalLayoutKind.U64 -> ComponentValue.U64(cursor.next().toULong())
        CanonicalLayoutKind.F32 -> ComponentValue.F32(CanonicalFloat(Float.fromBits(cursor.next().toInt())))
        CanonicalLayoutKind.F64 -> ComponentValue.F64(CanonicalDouble(Double.fromBits(cursor.next())))
        CanonicalLayoutKind.Char -> componentChar(cursor.next().toUInt())
        CanonicalLayoutKind.String -> ComponentValue.StringValue(
            CanonicalStringLifter(context, cursor.next().toInt(), cursor.next().toInt()),
        )
        CanonicalLayoutKind.List -> {
            val pointer = cursor.next().toInt()
            val length = cursor.next().toInt()
            loadList(context, layout, pointer, length)
        }
        CanonicalLayoutKind.Record -> ComponentValue.Record(
            layout.children.map { child -> liftFlat(context, child, cursor) },
        )
        CanonicalLayoutKind.Tuple -> ComponentValue.Tuple(
            layout.children.map { child -> liftFlat(context, child, cursor) },
        )
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        -> liftFlatVariant(context, layout, cursor)
        CanonicalLayoutKind.Flags -> componentFlags(layout, cursor.next().toUInt())
        CanonicalLayoutKind.Enum -> componentEnum(layout, cursor.next().toInt())
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> CanonicalResourceLifter(context, layout, cursor.next().toInt())
    }
}

private fun liftFlatVariant(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    cursor: FlatCursor,
): ComponentValue {
    val caseIndex = cursor.next().toInt()
    if (caseIndex !in layout.children.indices) invalidValue("canonical variant discriminant is out of range")
    val payloadStart = cursor.index
    val payloadSlots = layout.shape.flatTypes.size - 1
    val child = layout.children[caseIndex]
    val value = if (child == ABSENT_LAYOUT) null else liftFlat(context, child, cursor)
    cursor.index = payloadStart + payloadSlots
    return variantValue(layout.kind, caseIndex, value)
}

private fun lowerFlat(
    context: CanonicalCallContext,
    layoutIndex: Int,
    value: ComponentValue,
    destination: LongArray,
    offset: Int,
): Int {
    val layout = context.runtimeInfo.linearMemoryLayouts[layoutIndex]
    return when (layout.kind) {
        CanonicalLayoutKind.Bool -> put(destination, offset, value.bool().let { if (it) 1L else 0L })
        CanonicalLayoutKind.S8 -> put(destination, offset, value.s8().toLong())
        CanonicalLayoutKind.U8 -> put(destination, offset, value.u8().toLong())
        CanonicalLayoutKind.S16 -> put(destination, offset, value.s16().toLong())
        CanonicalLayoutKind.U16 -> put(destination, offset, value.u16().toLong())
        CanonicalLayoutKind.S32 -> put(destination, offset, value.s32().toLong())
        CanonicalLayoutKind.U32 -> put(destination, offset, value.u32().toLong())
        CanonicalLayoutKind.S64 -> put(destination, offset, value.s64())
        CanonicalLayoutKind.U64 -> put(destination, offset, value.u64().toLong())
        CanonicalLayoutKind.F32 -> put(destination, offset, CanonicalFloat(value.f32()).toRawBits().toLong())
        CanonicalLayoutKind.F64 -> put(destination, offset, CanonicalDouble(value.f64()).toRawBits())
        CanonicalLayoutKind.Char -> put(destination, offset, value.char().toLong())
        CanonicalLayoutKind.String -> {
            val lowered = CanonicalStringLowerer(context, value.string())
            destination[offset] = lowered.toUInt().toLong()
            destination[offset + 1] = (lowered ushr Int.SIZE_BITS).toUInt().toLong()
            offset + 2
        }
        CanonicalLayoutKind.List -> {
            val lowered = storeList(context, layout, value)
            destination[offset] = lowered.toUInt().toLong()
            destination[offset + 1] = (lowered ushr Int.SIZE_BITS).toUInt().toLong()
            offset + 2
        }
        CanonicalLayoutKind.Record -> lowerFields(context, layout, value.record(), destination, offset)
        CanonicalLayoutKind.Tuple -> lowerFields(context, layout, value.tuple(), destination, offset)
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        -> lowerFlatVariant(context, layout, value, destination, offset)
        CanonicalLayoutKind.Flags -> put(destination, offset, checkedFlags(layout, value.flags()).toLong())
        CanonicalLayoutKind.Enum -> put(destination, offset, checkedEnum(layout, value.enum()).toLong())
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> put(destination, offset, CanonicalResourceLowerer(context, layout, value).toLong())
    }
}

private fun lowerFields(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    values: List<ComponentValue>,
    destination: LongArray,
    start: Int,
): Int {
    if (values.size != layout.children.size) invalidValue("component compound value has the wrong field count")
    var offset = start
    layout.children.forEachIndexed { index, child ->
        offset = lowerFlat(context, child, values[index], destination, offset)
    }
    return offset
}

private fun lowerFlatVariant(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    value: ComponentValue,
    destination: LongArray,
    offset: Int,
): Int {
    val variant = variant(value, layout.kind)
    if (variant.caseIndex !in layout.children.indices) invalidValue("component variant discriminant is out of range")
    destination[offset] = variant.caseIndex.toLong()
    val payloadSlots = layout.shape.flatTypes.size - 1
    destination.fill(0L, offset + 1, offset + 1 + payloadSlots)
    val child = layout.children[variant.caseIndex]
    if (child == ABSENT_LAYOUT) {
        if (variant.value != null) invalidValue("component variant case does not accept a payload")
    } else {
        val payloadEnd = lowerFlat(
            context,
            child,
            variant.value ?: invalidValue("component variant case requires a payload"),
            destination,
            offset + 1,
        )
        coerceVariantPayload(
            child = context.runtimeInfo.linearMemoryLayouts[child],
            variant = layout,
            destination = destination,
            offset = offset + 1,
            count = payloadEnd - offset - 1,
        )
    }
    return offset + 1 + payloadSlots
}

private fun load(
    context: CanonicalCallContext,
    layoutIndex: Int,
    pointer: Int,
    regionChecked: Boolean = false,
): ComponentValue {
    val layout = context.runtimeInfo.linearMemoryLayouts[layoutIndex]
    val memory = context.memory()
    if (!regionChecked) checkRegion(memory, pointer, layout.size32.toInt(), layout.alignment32.toInt())
    return when (layout.kind) {
        CanonicalLayoutKind.Bool -> componentBool(I328UReader(memory.data, pointer))
        CanonicalLayoutKind.S8 -> ComponentValue.S8(I328SReader(memory.data, pointer).toByte())
        CanonicalLayoutKind.U8 -> ComponentValue.U8(I328UReader(memory.data, pointer).toUByte())
        CanonicalLayoutKind.S16 -> ComponentValue.S16(I3216SReader(memory.data, pointer).toShort())
        CanonicalLayoutKind.U16 -> ComponentValue.U16(I3216UReader(memory.data, pointer).toUShort())
        CanonicalLayoutKind.S32 -> ComponentValue.S32(I32Reader(memory.data, pointer))
        CanonicalLayoutKind.U32 -> ComponentValue.U32(I32Reader(memory.data, pointer).toUInt())
        CanonicalLayoutKind.S64 -> ComponentValue.S64(I64Reader(memory.data, pointer))
        CanonicalLayoutKind.U64 -> ComponentValue.U64(I64Reader(memory.data, pointer).toULong())
        CanonicalLayoutKind.F32 -> ComponentValue.F32(CanonicalFloat(F32Reader(memory.data, pointer)))
        CanonicalLayoutKind.F64 -> ComponentValue.F64(CanonicalDouble(F64Reader(memory.data, pointer)))
        CanonicalLayoutKind.Char -> componentChar(I32Reader(memory.data, pointer).toUInt())
        CanonicalLayoutKind.String -> ComponentValue.StringValue(
            CanonicalStringLifter(
                context,
                I32Reader(memory.data, pointer),
                I32Reader(memory.data, pointer + Int.SIZE_BYTES),
            ),
        )
        CanonicalLayoutKind.List -> loadList(
            context,
            layout,
            I32Reader(memory.data, pointer),
            I32Reader(memory.data, pointer + Int.SIZE_BYTES),
        )
        CanonicalLayoutKind.Record -> ComponentValue.Record(loadFields(context, layout, pointer))
        CanonicalLayoutKind.Tuple -> ComponentValue.Tuple(loadFields(context, layout, pointer))
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        -> loadVariant(context, layout, pointer)
        CanonicalLayoutKind.Flags -> componentFlags(layout, readUnsigned(memory, pointer, layout.size32.toInt()).toUInt())
        CanonicalLayoutKind.Enum -> componentEnum(layout, readUnsigned(memory, pointer, layout.discriminantSize32.toInt()))
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> CanonicalResourceLifter(context, layout, I32Reader(memory.data, pointer))
    }
}

private fun store(
    context: CanonicalCallContext,
    layoutIndex: Int,
    value: ComponentValue,
    pointer: Int,
    regionChecked: Boolean = false,
) {
    val layout = context.runtimeInfo.linearMemoryLayouts[layoutIndex]
    val memory = context.memory()
    if (!regionChecked) checkRegion(memory, pointer, layout.size32.toInt(), layout.alignment32.toInt())
    when (layout.kind) {
        CanonicalLayoutKind.Bool -> I32ToI8Writer(memory.data, pointer, if (value.bool()) 1 else 0)
        CanonicalLayoutKind.S8 -> I32ToI8Writer(memory.data, pointer, value.s8().toInt())
        CanonicalLayoutKind.U8 -> I32ToI8Writer(memory.data, pointer, value.u8().toInt())
        CanonicalLayoutKind.S16 -> I32ToI16Writer(memory.data, pointer, value.s16().toInt())
        CanonicalLayoutKind.U16 -> I32ToI16Writer(memory.data, pointer, value.u16().toInt())
        CanonicalLayoutKind.S32 -> I32Writer(memory.data, pointer, value.s32())
        CanonicalLayoutKind.U32 -> I32Writer(memory.data, pointer, value.u32().toInt())
        CanonicalLayoutKind.S64 -> I64Writer(memory.data, pointer, value.s64())
        CanonicalLayoutKind.U64 -> I64Writer(memory.data, pointer, value.u64().toLong())
        CanonicalLayoutKind.F32 -> F32Writer(memory.data, pointer, CanonicalFloat(value.f32()))
        CanonicalLayoutKind.F64 -> F64Writer(memory.data, pointer, CanonicalDouble(value.f64()))
        CanonicalLayoutKind.Char -> I32Writer(memory.data, pointer, value.char().toInt())
        CanonicalLayoutKind.String -> {
            val lowered = CanonicalStringLowerer(context, value.string())
            I32Writer(memory.data, pointer, lowered.toInt())
            I32Writer(memory.data, pointer + Int.SIZE_BYTES, (lowered ushr Int.SIZE_BITS).toInt())
        }
        CanonicalLayoutKind.List -> {
            val lowered = storeList(context, layout, value)
            I32Writer(memory.data, pointer, lowered.toInt())
            I32Writer(memory.data, pointer + Int.SIZE_BYTES, (lowered ushr Int.SIZE_BITS).toInt())
        }
        CanonicalLayoutKind.Record -> storeFields(context, layout, value.record(), pointer)
        CanonicalLayoutKind.Tuple -> storeFields(context, layout, value.tuple(), pointer)
        CanonicalLayoutKind.Variant,
        CanonicalLayoutKind.Option,
        CanonicalLayoutKind.Result,
        -> storeVariant(context, layout, value, pointer)
        CanonicalLayoutKind.Flags -> writeUnsigned(
            memory,
            pointer,
            layout.size32.toInt(),
            checkedFlags(layout, value.flags()).toLong(),
        )
        CanonicalLayoutKind.Enum -> writeUnsigned(
            memory,
            pointer,
            layout.discriminantSize32.toInt(),
            checkedEnum(layout, value.enum()).toLong(),
        )
        CanonicalLayoutKind.Own,
        CanonicalLayoutKind.Borrow,
        -> I32Writer(memory.data, pointer, CanonicalResourceLowerer(context, layout, value))
    }
}

private fun loadFields(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    pointer: Int,
): List<ComponentValue> = layout.children.mapIndexed { index, child ->
    load(context, child, pointer + layout.offsets32[index].toInt(), regionChecked = true)
}

private fun storeFields(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    values: List<ComponentValue>,
    pointer: Int,
) {
    if (values.size != layout.children.size) invalidValue("component compound value has the wrong field count")
    layout.children.forEachIndexed { index, child ->
        store(context, child, values[index], pointer + layout.offsets32[index].toInt(), regionChecked = true)
    }
}

private fun loadVariant(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    pointer: Int,
): ComponentValue {
    val memory = context.memory()
    val caseIndex = readUnsigned(memory, pointer, layout.discriminantSize32.toInt())
    if (caseIndex !in layout.children.indices) invalidValue("canonical variant discriminant is out of range")
    val child = layout.children[caseIndex]
    val payload = if (child == ABSENT_LAYOUT) {
        null
    } else {
        load(context, child, pointer + layout.payloadOffset32.toInt(), regionChecked = true)
    }
    return variantValue(layout.kind, caseIndex, payload)
}

private fun storeVariant(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    value: ComponentValue,
    pointer: Int,
) {
    val variant = variant(value, layout.kind)
    if (variant.caseIndex !in layout.children.indices) invalidValue("component variant discriminant is out of range")
    writeUnsigned(context.memory(), pointer, layout.discriminantSize32.toInt(), variant.caseIndex.toLong())
    val child = layout.children[variant.caseIndex]
    if (child == ABSENT_LAYOUT) {
        if (variant.value != null) invalidValue("component variant case does not accept a payload")
    } else {
        store(
            context,
            child,
            variant.value ?: invalidValue("component variant case requires a payload"),
            pointer + layout.payloadOffset32.toInt(),
            regionChecked = true,
        )
    }
}

private fun loadList(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    pointer: Int,
    length: Int,
): ComponentValue {
    if (length < 0) invalidValue("canonical list length is out of range")
    val child = context.runtimeInfo.linearMemoryLayouts[layout.children.single()]
    val byteLength = checkedListByteLength(length, child.size32.toInt())
    checkRegion(context.memory(), pointer, byteLength, child.alignment32.toInt())
    if (child.kind == CanonicalLayoutKind.U8) {
        val bytes = ByteArray(length)
        if (length != 0) {
            BytesReader(context.memory().data, bytes, pointer, length, 0)
        }
        return ComponentValue.ByteList(bytes)
    }
    return ComponentValue.ListValue(
        List(length) { index ->
            load(context, layout.children[0], pointer + index * child.size32.toInt(), regionChecked = true)
        },
    )
}

private fun storeList(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    value: ComponentValue,
): Long {
    val childIndex = layout.children.single()
    val child = context.runtimeInfo.linearMemoryLayouts[childIndex]
    if (child.kind == CanonicalLayoutKind.U8) {
        val bytes = (value as? ComponentValue.ByteList)?.bytes
            ?: invalidValue("component byte list value expected")
        val pointer = context.realloc(Byte.SIZE_BYTES, bytes.size)
        if (bytes.isNotEmpty()) {
            checkRegion(context.memory(), pointer, bytes.size, Byte.SIZE_BYTES)
            BytesWriter(context.memory().data, context.memory().size, bytes, pointer, bytes.size, 0)
        }
        return packPointerAndLength(pointer, bytes.size)
    }
    val values = value.list()
    val byteLength = checkedListByteLength(values.size, child.size32.toInt())
    val pointer = context.realloc(child.alignment32.toInt(), byteLength)
    if (byteLength != 0) {
        checkRegion(context.memory(), pointer, byteLength, child.alignment32.toInt())
        values.forEachIndexed { index, element ->
            store(context, childIndex, element, pointer + index * child.size32.toInt(), regionChecked = true)
        }
    }
    return packPointerAndLength(pointer, values.size)
}

private fun packPointerAndLength(
    pointer: Int,
    length: Int,
): Long = pointer.toUInt().toLong() or (length.toLong() shl Int.SIZE_BITS)

private fun checkRegion(
    memory: MemoryInstance,
    pointer: Int,
    size: Int,
    alignment: Int,
) {
    if (alignment <= 0 || pointer and (alignment - 1) != 0) invalidValue("canonical memory pointer is misaligned")
    PessimisticBoundsChecker(pointer, size, memory.size) { Unit }
}

private fun readUnsigned(
    memory: MemoryInstance,
    pointer: Int,
    width: Int,
): Int = when (width) {
    1 -> I328UReader(memory.data, pointer)
    2 -> I3216UReader(memory.data, pointer)
    4 -> I32Reader(memory.data, pointer)
    else -> invalidValue("canonical integer width is unsupported")
}

private fun writeUnsigned(
    memory: MemoryInstance,
    pointer: Int,
    width: Int,
    value: Long,
) {
    when (width) {
        1 -> I32ToI8Writer(memory.data, pointer, value.toInt())
        2 -> I32ToI16Writer(memory.data, pointer, value.toInt())
        4 -> I32Writer(memory.data, pointer, value.toInt())
        else -> invalidValue("canonical integer width is unsupported")
    }
}

private fun componentChar(codePoint: UInt): ComponentValue.Char = try {
    ComponentValue.Char(codePoint)
} catch (_: IllegalArgumentException) {
    invalidValue("canonical char is not a Unicode scalar value")
}

private fun componentBool(value: Int): ComponentValue.Bool = ComponentValue.Bool(value != 0)

private fun componentFlags(
    layout: LinearMemoryLayout,
    bits: UInt,
): ComponentValue.Flags = ComponentValue.Flags(bits and flagsMask(layout))

private fun checkedFlags(
    layout: LinearMemoryLayout,
    bits: UInt,
): UInt {
    val mask = flagsMask(layout)
    if (bits and mask.inv() != 0u) invalidValue("canonical flags contain undefined bits")
    return bits
}

private fun flagsMask(layout: LinearMemoryLayout): UInt =
    if (layout.elementCount == UInt.SIZE_BITS) UInt.MAX_VALUE else (1u shl layout.elementCount) - 1u

private fun componentEnum(
    layout: LinearMemoryLayout,
    caseIndex: Int,
): ComponentValue.Enum = ComponentValue.Enum(checkedEnum(layout, caseIndex))

private fun checkedEnum(
    layout: LinearMemoryLayout,
    caseIndex: Int,
): Int {
    if (caseIndex !in 0 until layout.elementCount) invalidValue("canonical enum discriminant is out of range")
    return caseIndex
}

private fun variantValue(
    kind: CanonicalLayoutKind,
    caseIndex: Int,
    value: ComponentValue?,
): ComponentValue = when (kind) {
    CanonicalLayoutKind.Option -> if (caseIndex == 0) {
        ComponentValue.Option.None
    } else {
        ComponentValue.Option.Some(
            value ?: invalidValue("canonical option some case requires a payload"),
        )
    }
    CanonicalLayoutKind.Result -> if (caseIndex == 0) ComponentValue.Result.Ok(value) else ComponentValue.Result.Error(value)
    else -> ComponentValue.Variant(caseIndex, value)
}

private fun variant(
    value: ComponentValue,
    kind: CanonicalLayoutKind,
): VariantValue = when (kind) {
    CanonicalLayoutKind.Option -> when (value) {
        ComponentValue.Option.None -> VariantValue(0, null)
        is ComponentValue.Option.Some -> VariantValue(1, value.value)
        else -> invalidValue("component option value expected")
    }
    CanonicalLayoutKind.Result -> when (value) {
        is ComponentValue.Result.Ok -> VariantValue(0, value.value)
        is ComponentValue.Result.Error -> VariantValue(1, value.value)
        else -> invalidValue("component result value expected")
    }
    else -> (value as? ComponentValue.Variant)?.let { VariantValue(it.caseIndex, it.value) }
        ?: invalidValue("component variant value expected")
}

private fun checkedListByteLength(
    length: Int,
    elementSize: Int,
): Int {
    if (length < 0 || elementSize < 0 || (elementSize != 0 && length > MAX_LIST_BYTE_LENGTH / elementSize)) {
        invalidValue("canonical list is too large")
    }
    return length * elementSize
}

private fun coerceVariantPayload(
    child: LinearMemoryLayout,
    variant: LinearMemoryLayout,
    destination: LongArray,
    offset: Int,
    count: Int,
) {
    repeat(count) { index ->
        val have = child.shape.flatTypes[index]
        val want = variant.shape.flatTypes[index + 1]
        if (want.isNumber(NumberType.I64) && (have.isNumber(NumberType.I32) || have.isNumber(NumberType.F32))) {
            destination[offset + index] = destination[offset + index] and UINT_MASK
        }
    }
}

private fun ValueType.isNumber(type: NumberType): Boolean =
    this is ValueType.Number && numberType == type

private fun put(
    destination: LongArray,
    offset: Int,
    value: Long,
): Int {
    destination[offset] = value
    return offset + 1
}

private fun ComponentValue.bool(): Boolean = (this as? ComponentValue.Bool)?.value
    ?: invalidValue("component bool value expected")

private fun ComponentValue.s8(): Byte = (this as? ComponentValue.S8)?.value
    ?: invalidValue("component s8 value expected")

private fun ComponentValue.u8(): UByte = (this as? ComponentValue.U8)?.value
    ?: invalidValue("component u8 value expected")

private fun ComponentValue.s16(): Short = (this as? ComponentValue.S16)?.value
    ?: invalidValue("component s16 value expected")

private fun ComponentValue.u16(): UShort = (this as? ComponentValue.U16)?.value
    ?: invalidValue("component u16 value expected")

private fun ComponentValue.s32(): Int = (this as? ComponentValue.S32)?.value
    ?: invalidValue("component s32 value expected")

private fun ComponentValue.u32(): UInt = (this as? ComponentValue.U32)?.value
    ?: invalidValue("component u32 value expected")

private fun ComponentValue.s64(): Long = (this as? ComponentValue.S64)?.value
    ?: invalidValue("component s64 value expected")

private fun ComponentValue.u64(): ULong = (this as? ComponentValue.U64)?.value
    ?: invalidValue("component u64 value expected")

private fun ComponentValue.f32(): Float = (this as? ComponentValue.F32)?.value
    ?: invalidValue("component f32 value expected")

private fun ComponentValue.f64(): Double = (this as? ComponentValue.F64)?.value
    ?: invalidValue("component f64 value expected")

private fun ComponentValue.char(): UInt = (this as? ComponentValue.Char)?.codePoint
    ?: invalidValue("component char value expected")

private fun ComponentValue.string(): String = (this as? ComponentValue.StringValue)?.value
    ?: invalidValue("component string value expected")

private fun ComponentValue.list(): List<ComponentValue> = (this as? ComponentValue.ListValue)?.elements
    ?: invalidValue("component list value expected")

private fun ComponentValue.record(): List<ComponentValue> = (this as? ComponentValue.Record)?.fields
    ?: invalidValue("component record value expected")

private fun ComponentValue.tuple(): List<ComponentValue> = (this as? ComponentValue.Tuple)?.elements
    ?: invalidValue("component tuple value expected")

private fun ComponentValue.flags(): UInt = (this as? ComponentValue.Flags)?.bits
    ?: invalidValue("component flags value expected")

private fun ComponentValue.enum(): Int = (this as? ComponentValue.Enum)?.caseIndex
    ?: invalidValue("component enum value expected")

private class FlatCursor(
    private val source: LongArray,
) {
    var index = 0

    fun next(): Long = source.getOrNull(index++) ?: invalidValue("canonical flat value is missing a slot")
}

private data class VariantValue(
    val caseIndex: Int,
    val value: ComponentValue?,
)

private const val ABSENT_LAYOUT = -1
private const val MAX_LIST_BYTE_LENGTH = (1 shl 28) - 1
private const val UINT_MASK = 0xffff_ffffL
