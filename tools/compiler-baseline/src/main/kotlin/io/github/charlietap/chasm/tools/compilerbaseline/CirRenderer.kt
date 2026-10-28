package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.CirTag
import io.github.charlietap.chasm.cir.ExceptionTable
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.Provenance
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.TransferSource
import io.github.charlietap.chasm.cir.ValueType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf

internal class CirRenderer {

    private val layouts = HashMap<KClass<*>, List<Field>>()

    fun module(module: CirModule): JsonObject = buildJsonObject {
        put("schema_version", module.schemaVersion)
        put("functions", JsonArray(module.functions.map(::function)))
    }

    private fun function(function: CirFunction): JsonObject = buildJsonObject {
        put("index", function.index.index)
        put("type", function.type.index)
        put("frame", frame(function.frame))
        put("instructions", value(function.instructions, typeOf<Array<Instruction>>()))
        put("checkpoints", value(function.checkpoints, typeOf<IntArray>()))
        function.exceptions?.let { exceptions -> put("exceptions", value(exceptions, typeOf<ExceptionTable>())) }
        function.provenance?.let { provenance -> put("provenance", value(provenance, typeOf<Provenance>())) }
    }

    private fun frame(frame: Frame): JsonObject = buildJsonObject {
        put("frame_slots", frame.frameSlots)
        put("activation_header_slot", frame.activationHeaderSlot)
        if (frame.locals.isNotEmpty()) put("locals", value(frame.locals, typeOf<List<ValueType>>()))
        if (frame.initialisedLocalCount != 0) put("initialised_local_count", frame.initialisedLocalCount)
    }

    private fun value(value: Any?, type: KType): JsonElement = when (value) {
        null -> JsonNull
        is ValueType -> JsonPrimitive(valueType(value))
        is ReferenceType -> JsonPrimitive(referenceType(value))
        is HeapType -> JsonPrimitive(heapType(value))
        is OperandTransfer -> transfer(value)
        is Boolean -> JsonPrimitive(value)
        is Float -> JsonPrimitive("0x" + value.toRawBits().toUInt().toString(16).padStart(8, '0'))
        is Double -> JsonPrimitive("0x" + value.toRawBits().toULong().toString(16).padStart(16, '0'))
        is Number -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        is Enum<*> -> JsonPrimitive(value.name)
        is IntArray -> JsonArray(value.map(::JsonPrimitive))
        is LongArray -> JsonArray(value.map(::JsonPrimitive))
        is BooleanArray -> JsonArray(value.map(::JsonPrimitive))
        is Array<*> -> JsonArray(value.map { element -> value(element, elementType(type)) })
        is List<*> -> JsonArray(value.map { element -> value(element, elementType(type)) })
        is Map<*, *> -> error("maps have no CIR rendering: $type")
        else -> record(value, type)
    }

    private fun record(value: Any, type: KType): JsonElement {
        val kClass = value::class
        if (kClass.isValue) {
            val field = layout(kClass).single()
            return value(field.property.get(value), field.type)
        }
        val declared = type.classifier as KClass<*>
        return buildJsonObject {
            if (declared.isSealed) {
                val tag = checkNotNull(kClass.findAnnotation<CirTag>()) { "${kClass.qualifiedName} has no CirTag" }
                put(discriminator(declared), tag.name)
            }
            for (field in layout(kClass)) {
                put(field.name, value(field.property.get(value), field.type))
            }
        }
    }

    private fun transfer(transfer: OperandTransfer): JsonObject = buildJsonObject {
        put("sources", value(transfer.sources(), typeOf<List<TransferSource>>()))
        put("destination_slot_base", transfer.destinationSlotBase)
    }

    private fun layout(kClass: KClass<*>): List<Field> = layouts.getOrPut(kClass) {
        if (kClass.objectInstance != null) return@getOrPut emptyList()
        val constructor = checkNotNull(kClass.primaryConstructor) { "${kClass.qualifiedName} has no primary constructor" }
        val properties = kClass.memberProperties.associateBy { property -> property.name }
        val names = constructor.parameters.map { parameter -> parameter.name }.toSet()
        val stored = properties.values.filter { property ->
            property.javaField != null && property.name !in names && property.name != Instruction::opcode.name
        }
        check(stored.isEmpty()) { "${kClass.qualifiedName} stores ${stored.map { property -> property.name }} outside its constructor" }
        constructor.parameters.map { parameter ->
            val name = checkNotNull(parameter.name)
            check(!parameter.isOptional) { "${kClass.qualifiedName}.$name has a default value" }
            @Suppress("UNCHECKED_CAST")
            Field(snakeCase(name), properties.getValue(name) as KProperty1<Any, *>, parameter.type)
        }
    }

    private fun discriminator(declared: KClass<*>): String = when (declared) {
        FusedOperand::class, NumericCondition::class, TransferSource::class -> "kind"
        else -> "tag"
    }

    private fun elementType(type: KType): KType = checkNotNull(type.arguments.single().type) { "$type has no element type" }

    private fun valueType(type: ValueType): String = when (type) {
        ValueType.I32 -> "i32"
        ValueType.I64 -> "i64"
        ValueType.F32 -> "f32"
        ValueType.F64 -> "f64"
        ValueType.V128 -> "v128"
        is ValueType.Reference -> referenceType(type.type)
    }

    private fun referenceType(type: ReferenceType): String = if (type.nullable) {
        "ref null ${heapType(type.heapType)}"
    } else {
        "ref ${heapType(type.heapType)}"
    }

    private fun heapType(type: HeapType): String = when (type) {
        is HeapType.Abstract -> type.type.text
        is HeapType.Concrete -> type.type.index.toString()
    }

    private fun snakeCase(name: String): String = buildString {
        for (char in name) {
            if (char.isUpperCase()) append('_').append(char.lowercaseChar()) else append(char)
        }
    }

    private class Field(
        val name: String,
        val property: KProperty1<Any, *>,
        val type: KType,
    )
}
