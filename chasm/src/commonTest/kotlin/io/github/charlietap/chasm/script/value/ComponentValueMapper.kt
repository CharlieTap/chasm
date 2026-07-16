package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.sweet.lib.value.ComponentResult
import io.github.charlietap.sweet.lib.value.Value

typealias ComponentValueMapper = (Value, ComponentValueType) -> ComponentValue?

fun ComponentValueMapper(
    value: Value,
    type: ComponentValueType,
): ComponentValue? = when (type) {
    is ComponentValueType.Primitive -> mapPrimitive(value, type.type)
    is ComponentValueType.Defined -> {
        val defined = type.definition.type as? ComponentDefinedType.Value ?: return null
        mapDefined(value, defined.type)
    }
}

private fun mapPrimitive(
    value: Value,
    type: ComponentPrimitiveType,
): ComponentValue? = when (type) {
    ComponentPrimitiveType.Bool -> (value as? Value.Bool)?.let { ComponentValue.Bool(it.value) }
    ComponentPrimitiveType.S8 -> (value as? Value.S8)?.let { ComponentValue.S8(it.value.toByte()) }
    ComponentPrimitiveType.U8 -> (value as? Value.U8)?.let { ComponentValue.U8(it.value.toUByte()) }
    ComponentPrimitiveType.S16 -> (value as? Value.S16)?.let { ComponentValue.S16(it.value.toShort()) }
    ComponentPrimitiveType.U16 -> (value as? Value.U16)?.let { ComponentValue.U16(it.value.toUShort()) }
    ComponentPrimitiveType.S32 -> (value as? Value.S32)?.let { ComponentValue.S32(it.value.toInt()) }
    ComponentPrimitiveType.U32 -> (value as? Value.U32)?.let { ComponentValue.U32(it.value.toUInt()) }
    ComponentPrimitiveType.S64 -> (value as? Value.S64)?.let { ComponentValue.S64(it.value.toLong()) }
    ComponentPrimitiveType.U64 -> (value as? Value.U64)?.let { ComponentValue.U64(it.value.toULong()) }
    ComponentPrimitiveType.F32 -> (value as? Value.F32)?.value?.let(::componentF32)
    ComponentPrimitiveType.F64 -> (value as? Value.F64)?.value?.let(::componentF64)
    ComponentPrimitiveType.Char -> (value as? Value.Character)?.value?.componentCodePoint()?.let(ComponentValue::Char)
    ComponentPrimitiveType.String -> (value as? Value.StringValue)?.let { ComponentValue.StringValue(it.value) }
    ComponentPrimitiveType.ErrorContext -> null
}

private fun mapDefined(
    value: Value,
    type: ComponentDefinedValueType,
): ComponentValue? = when (type) {
    is ComponentDefinedValueType.Primitive -> mapPrimitive(value, type.type)
    is ComponentDefinedValueType.Record -> {
        val record = value as? Value.Record ?: return null
        ComponentValue.Record(
            type.fields.map { field ->
                val source = record.value.firstOrNull { candidate -> candidate.name == field.label } ?: return null
                ComponentValueMapper(source.value, field.type) ?: return null
            },
        )
    }
    is ComponentDefinedValueType.Variant -> {
        val variant = value as? Value.Variant ?: return null
        val caseIndex = type.cases.indexOfFirst { case -> case.label == variant.value.case }
        if (caseIndex < 0) return null
        val case = type.cases[caseIndex]
        val payload = case.type?.let { payloadType ->
            val source = variant.value.payload ?: return null
            ComponentValueMapper(source, payloadType) ?: return null
        }
        ComponentValue.Variant(caseIndex, payload)
    }
    is ComponentDefinedValueType.ListValue -> {
        val list = value as? Value.ListValue ?: return null
        if (type.element.isU8()) {
            ComponentValue.ByteList(
                ByteArray(list.value.size) { index ->
                    val element = list.value[index] as? Value.U8 ?: return null
                    element.value.toByte()
                },
            )
        } else {
            ComponentValue.ListValue(
                list.value.map { element -> ComponentValueMapper(element, type.element) ?: return null },
            )
        }
    }
    is ComponentDefinedValueType.Tuple -> {
        val tuple = value as? Value.Tuple ?: return null
        if (tuple.value.size != type.elements.size) return null
        ComponentValue.Tuple(
            tuple.value.mapIndexed { index, element ->
                ComponentValueMapper(element, type.elements[index]) ?: return null
            },
        )
    }
    is ComponentDefinedValueType.Flags -> {
        val flags = value as? Value.Flags ?: return null
        var bits = 0u
        flags.value.forEach { label ->
            val index = type.labels.indexOf(label)
            if (index < 0) return null
            bits = bits or (1u shl index)
        }
        ComponentValue.Flags(bits)
    }
    is ComponentDefinedValueType.Enum -> {
        val enum = value as? Value.Enum ?: return null
        val caseIndex = type.labels.indexOf(enum.value)
        if (caseIndex < 0) null else ComponentValue.Enum(caseIndex)
    }
    is ComponentDefinedValueType.Option -> {
        val option = value as? Value.Option ?: return null
        val element = option.value
        if (element == null) {
            ComponentValue.Option.None
        } else {
            ComponentValue.Option.Some(ComponentValueMapper(element, type.value) ?: return null)
        }
    }
    is ComponentDefinedValueType.Result -> {
        val result = value as? Value.Result ?: return null
        when (val resultValue = result.value) {
            is ComponentResult.Ok -> ComponentValue.Result.Ok(
                type.ok?.let { okType ->
                    val payload = resultValue.payload ?: return null
                    ComponentValueMapper(payload, okType) ?: return null
                },
            )
            is ComponentResult.Err -> ComponentValue.Result.Error(
                type.error?.let { errorType ->
                    val payload = resultValue.payload ?: return null
                    ComponentValueMapper(payload, errorType) ?: return null
                },
            )
        }
    }
    is ComponentDefinedValueType.FixedLengthList,
    is ComponentDefinedValueType.Map,
    is ComponentDefinedValueType.Own,
    is ComponentDefinedValueType.Borrow,
    is ComponentDefinedValueType.Stream,
    is ComponentDefinedValueType.Future,
    -> null
}

private fun componentF32(value: String): ComponentValue.F32 = ComponentValue.F32(
    if (value.contains("nan")) Float.NaN else Float.fromBits(value.toUInt().toInt()),
)

private fun componentF64(value: String): ComponentValue.F64 = ComponentValue.F64(
    if (value.contains("nan")) Double.NaN else Double.fromBits(value.toULong().toLong()),
)

private fun String.componentCodePoint(): UInt? = when {
    length == 1 && !this[0].isSurrogate() -> this[0].code.toUInt()
    length == 2 && this[0].isHighSurrogate() && this[1].isLowSurrogate() ->
        (0x10000 + ((this[0].code - 0xd800) shl 10) + (this[1].code - 0xdc00)).toUInt()
    else -> null
}

private fun ComponentValueType.isU8(): Boolean = when (this) {
    is ComponentValueType.Primitive -> type == ComponentPrimitiveType.U8
    is ComponentValueType.Defined ->
        (((definition.type as? ComponentDefinedType.Value)?.type as? ComponentDefinedValueType.Primitive)?.type) ==
            ComponentPrimitiveType.U8
}
