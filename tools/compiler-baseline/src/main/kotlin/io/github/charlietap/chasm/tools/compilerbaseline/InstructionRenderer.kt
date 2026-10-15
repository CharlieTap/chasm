package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

internal class InstructionRenderer(
    private val symbols: ModuleSymbols,
) {

    fun immediates(instruction: LinkedInstruction, entryIp: Int): JsonObject = buildJsonObject {
        for ((name, value) in properties(instruction)) {
            put(snakeCase(name), field(instruction, name, value, entryIp))
        }
    }

    fun localInitialization(localInitialization: LocalInitialization): JsonElement =
        value(localInitialization, "local_initialization")

    fun exceptions(table: FunctionExceptionTable): JsonObject = buildJsonObject {
        put("instruction_count", table.instructionCount)
        put("activation_header_slot", table.interfaceSlotCount)
        put("result_count", table.resultCount)
        put(
            "regions",
            JsonArray(
                table.regions.map { region ->
                    buildJsonObject {
                        put("start", target(region.startOffset))
                        put("end", target(region.endOffset))
                        put("parent", region.parentRegion)
                        put(
                            "catches",
                            JsonArray(
                                region.catches.map { handler ->
                                    buildJsonObject {
                                        put("tag", if (handler.tagAddress < 0) "all" else symbols.tag(handler.tagAddress))
                                        put("target", target(handler.targetOffset))
                                        put("payload_slots", value(handler.payloadSlots, "payload_slots"))
                                        put("exception_reference", handler.includeExceptionReference)
                                        put("stack_slots", handler.stackSlotCount)
                                    }
                                },
                            ),
                        )
                    }
                },
            ),
        )
        put("tail_calls", JsonArray(table.tailCallOffsets.map(::target)))
    }

    private fun field(
        instruction: LinkedInstruction,
        name: String,
        value: Any?,
        entryIp: Int,
    ): JsonElement = when {
        instruction is AdminInstruction && name == "targetIp" -> target((value as Int) - entryIp)
        instruction is AdminInstruction && name == "targetIps" -> JsonArray((value as IntArray).map { ip -> target(ip - entryIp) })
        instruction is ReferenceInstruction.RefFuncS && name == "reference" -> reference((value as Long).toReferenceValue())
        instruction is ReferenceInstruction.RefNullS && name == "reference" -> reference((value as Long).toReferenceValue())
        else -> value(value, name)
    }

    private fun value(value: Any?, path: String): JsonElement = when (value) {
        null -> JsonNull
        is Boolean -> JsonPrimitive(value)
        is Byte, is Short, is Int, is Long -> JsonPrimitive(value as Number)
        is UByte, is UShort, is UInt, is ULong -> JsonPrimitive(value.toString().toBigInteger())
        is Float -> JsonPrimitive("$value (0x${value.toRawBits().toUInt().toString(HEX).padStart(8, '0')})")
        is Double -> JsonPrimitive("$value (0x${value.toRawBits().toULong().toString(HEX).padStart(16, '0')})")
        is String -> JsonPrimitive(value)
        is Enum<*> -> JsonPrimitive(snakeCase(value.name))
        is IntArray -> JsonArray(value.map(::JsonPrimitive))
        is LongArray -> JsonArray(value.map(::JsonPrimitive))
        is ByteArray -> JsonPrimitive(value.joinToString("") { byte -> byte.toUByte().toString(HEX).padStart(2, '0') })
        is Array<*> -> JsonArray(value.mapIndexed { index, element -> value(element, "$path[$index]") })
        is List<*> -> JsonArray(value.mapIndexed { index, element -> value(element, "$path[$index]") })
        is RTT -> JsonPrimitive(symbols.type(value))
        is ReferenceTypeTest -> referenceTypeTest(value)
        is Address.Function -> JsonPrimitive(symbols.function(value))
        is Address.Tag -> JsonPrimitive(symbols.tag(value))
        is OperandTransfer -> transfer(value, path)
        is LocalInitialization.ConstantStores -> buildJsonObject {
            put("kind", "constant_stores")
            put("values", value(value.values, "$path.values"))
        }
        is ReferenceValue -> reference(value)
        else -> symbols.nameOf(value)?.let(::JsonPrimitive) ?: record(value, path)
    }

    private fun record(value: Any, path: String): JsonElement {
        val type = value::class
        val kind = type.simpleName?.takeIf { type.allSuperclasses.any(KClass<*>::isSealed) }?.let(::snakeCase)
        if (type.objectInstance != null) {
            return JsonPrimitive(snakeCase(checkNotNull(kind ?: type.simpleName) { "unnamed object at $path" }))
        }
        check(type.isData || type.isValue) {
            "no rendering for ${type.qualifiedName} at $path; extend InstructionRenderer"
        }
        val properties = properties(value)
        if (type.isValue && kind == null) return value(properties.single().second, path)
        return buildJsonObject {
            kind?.let { put("kind", it) }
            for ((name, property) in properties) put(snakeCase(name), value(property, "$path.$name"))
        }
    }

    private fun referenceTypeTest(test: ReferenceTypeTest): JsonObject = buildJsonObject {
        put("nullable", test.nullable)
        if (test.isDefined) {
            put("type", symbols.type(test.rtt))
        } else {
            put("accepted_reference_tags", test.acceptedReferenceTags)
        }
    }

    private fun reference(reference: ReferenceValue): JsonElement = when (reference) {
        is ReferenceValue.Function -> JsonPrimitive(symbols.function(reference.address))
        else -> record(reference, "reference")
    }

    private fun transfer(transfer: OperandTransfer, path: String): JsonObject {
        val schedule = checkNotNull(scheduleProperty.get(transfer)) { "operand transfer without a schedule at $path" }
        val operations = scheduleField(schedule, "operations") as IntArray
        val values = scheduleField(schedule, "values") as LongArray
        return buildJsonObject {
            put("sources", value(transfer.sources, "$path.sources"))
            put(
                "moves",
                JsonArray(
                    operations.indices.map { index ->
                        JsonPrimitive(move(operations[index], values[index]))
                    },
                ),
            )
        }
    }

    private fun move(operation: Int, value: Long): String {
        val destination = operation and TRANSFER_INDEX_MASK
        return when (operation ushr TRANSFER_OPERATION_SHIFT) {
            TRANSFER_COPY_SLOT -> "+$destination = slot $value"
            TRANSFER_COPY_IMMEDIATE -> "+$destination = $value"
            TRANSFER_SAVE_SLOT -> "scratch = slot $value"
            TRANSFER_RESTORE -> "+$destination = scratch"
            else -> error("unknown operand transfer operation $operation")
        }
    }

    private fun target(offset: Int): JsonPrimitive = JsonPrimitive("@$offset")

    private fun snakeCase(name: String): String {
        val snake = if (name.all { it.isUpperCase() || it.isDigit() || it == '_' }) {
            name.lowercase()
        } else {
            name.replace(Regex("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), "_").lowercase()
        }
        return NAME_REPLACEMENTS[snake] ?: snake.split('_').joinToString("_", transform = ::wholeWord)
    }

    private fun wholeWord(word: String): String {
        WORD_REPLACEMENTS[word]?.let { return it }
        val numbered = NUMBERED_WORD.matchEntire(word) ?: return word
        val (stem, number) = numbered.destructured
        return "${WORD_REPLACEMENTS[stem] ?: stem}_$number"
    }

    private companion object {
        const val HEX = 16
        const val TRANSFER_COPY_SLOT = 0
        const val TRANSFER_COPY_IMMEDIATE = 1
        const val TRANSFER_SAVE_SLOT = 2
        const val TRANSFER_RESTORE = 3
        const val TRANSFER_OPERATION_SHIFT = 30
        const val TRANSFER_INDEX_MASK = (1 shl TRANSFER_OPERATION_SHIFT) - 1

        val NAME_REPLACEMENTS = mapOf("mem_arg" to "memory_offset")
        val WORD_REPLACEMENTS = mapOf("val" to "value", "src" to "source", "dst" to "destination", "dest" to "destination")
        val NUMBERED_WORD = Regex("(zero|val)([0-9]+)")

        @Suppress("UNCHECKED_CAST")
        val scheduleProperty = OperandTransfer::class.declaredMemberProperties
            .single { property -> property.name == "schedule" }
            .apply { isAccessible = true } as KProperty1<OperandTransfer, Any?>

        fun scheduleField(schedule: Any, name: String): Any? = schedule::class.memberProperties
            .single { property -> property.name == name }
            .apply { isAccessible = true }
            .getter
            .call(schedule)

        fun properties(value: Any): List<Pair<String, Any?>> {
            val type = value::class
            if (type.objectInstance != null) return emptyList()
            val constructor = checkNotNull(type.primaryConstructor) { "${type.qualifiedName} has no primary constructor" }
            val members = type.memberProperties.associateBy(KProperty1<out Any, *>::name)
            return constructor.parameters.map { parameter ->
                val name = checkNotNull(parameter.name)
                val property = checkNotNull(members[name]) {
                    "${type.qualifiedName}.$name is not stored, so it cannot be rendered"
                }
                property.isAccessible = true
                name to property.getter.call(value)
            }
        }
    }
}
