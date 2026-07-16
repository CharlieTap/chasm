package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

internal fun CanonicalComponentValueValidator(
    runtimeInfo: ComponentRuntimeInfo,
    tuple: CanonicalValueTupleLayout,
    values: List<ComponentValue>,
) {
    if (values.size != tuple.layouts.size) invalidValue("component value count does not match its function type")
    tuple.layouts.forEachIndexed { index, layout ->
        validate(runtimeInfo, layout, values[index])
    }
}

private fun validate(
    runtimeInfo: ComponentRuntimeInfo,
    layoutIndex: Int,
    value: ComponentValue,
) {
    val layout = runtimeInfo.linearMemoryLayouts[layoutIndex]
    when (layout.kind) {
        CanonicalLayoutKind.Bool -> requireType<ComponentValue.Bool>(value)
        CanonicalLayoutKind.S8 -> requireType<ComponentValue.S8>(value)
        CanonicalLayoutKind.U8 -> requireType<ComponentValue.U8>(value)
        CanonicalLayoutKind.S16 -> requireType<ComponentValue.S16>(value)
        CanonicalLayoutKind.U16 -> requireType<ComponentValue.U16>(value)
        CanonicalLayoutKind.S32 -> requireType<ComponentValue.S32>(value)
        CanonicalLayoutKind.U32 -> requireType<ComponentValue.U32>(value)
        CanonicalLayoutKind.S64 -> requireType<ComponentValue.S64>(value)
        CanonicalLayoutKind.U64 -> requireType<ComponentValue.U64>(value)
        CanonicalLayoutKind.F32 -> requireType<ComponentValue.F32>(value)
        CanonicalLayoutKind.F64 -> requireType<ComponentValue.F64>(value)
        CanonicalLayoutKind.Char -> requireType<ComponentValue.Char>(value)
        CanonicalLayoutKind.String -> requireType<ComponentValue.StringValue>(value)
        CanonicalLayoutKind.List -> validateList(runtimeInfo, layout, value)
        CanonicalLayoutKind.Record -> validateFields(runtimeInfo, layout, requireType<ComponentValue.Record>(value).fields)
        CanonicalLayoutKind.Tuple -> validateFields(runtimeInfo, layout, requireType<ComponentValue.Tuple>(value).elements)
        CanonicalLayoutKind.Variant -> requireType<ComponentValue.Variant>(value).let { variant ->
            validateVariant(runtimeInfo, layout, variant.caseIndex, variant.value)
        }
        CanonicalLayoutKind.Option -> when (value) {
            ComponentValue.Option.None -> validateVariant(runtimeInfo, layout, 0, null)
            is ComponentValue.Option.Some -> validateVariant(runtimeInfo, layout, 1, value.value)
            else -> invalidType()
        }
        CanonicalLayoutKind.Result -> when (value) {
            is ComponentValue.Result.Ok -> validateVariant(runtimeInfo, layout, 0, value.value)
            is ComponentValue.Result.Error -> validateVariant(runtimeInfo, layout, 1, value.value)
            else -> invalidType()
        }
        CanonicalLayoutKind.Flags -> {
            val flags = requireType<ComponentValue.Flags>(value).bits
            val allowed = if (layout.elementCount == UInt.SIZE_BITS) UInt.MAX_VALUE else (1u shl layout.elementCount) - 1u
            if (flags and allowed.inv() != 0u) invalidValue("component flags contain undeclared labels")
        }
        CanonicalLayoutKind.Enum -> {
            val caseIndex = requireType<ComponentValue.Enum>(value).caseIndex
            if (caseIndex !in 0 until layout.elementCount) invalidValue("component enum case is out of range")
        }
        CanonicalLayoutKind.Own -> requireType<ComponentValue.Resource.Own>(value)
        CanonicalLayoutKind.Borrow -> requireType<ComponentValue.Resource>(value)
    }
}

private fun validateList(
    runtimeInfo: ComponentRuntimeInfo,
    layout: LinearMemoryLayout,
    value: ComponentValue,
) {
    val child = layout.children.single()
    if (runtimeInfo.linearMemoryLayouts[child].kind == CanonicalLayoutKind.U8) {
        requireType<ComponentValue.ByteList>(value)
        return
    }
    val elements = requireType<ComponentValue.ListValue>(value).elements
    elements.forEach { element -> validate(runtimeInfo, child, element) }
}

private fun validateFields(
    runtimeInfo: ComponentRuntimeInfo,
    layout: LinearMemoryLayout,
    values: List<ComponentValue>,
) {
    if (values.size != layout.children.size) invalidValue("component compound value has the wrong field count")
    layout.children.forEachIndexed { index, child -> validate(runtimeInfo, child, values[index]) }
}

private fun validateVariant(
    runtimeInfo: ComponentRuntimeInfo,
    layout: LinearMemoryLayout,
    caseIndex: Int,
    value: ComponentValue?,
) {
    if (caseIndex !in layout.children.indices) invalidValue("component variant case is out of range")
    val child = layout.children[caseIndex]
    if (child == ABSENT_LAYOUT) {
        if (value != null) invalidValue("component variant case does not accept a payload")
    } else {
        validate(runtimeInfo, child, value ?: invalidValue("component variant case requires a payload"))
    }
}

private inline fun <reified T : ComponentValue> requireType(value: ComponentValue): T =
    value as? T ?: invalidType()

private fun invalidType(): Nothing = invalidValue("component value does not match its function type")

private const val ABSENT_LAYOUT = -1
