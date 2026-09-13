package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

internal enum class KotlinValueType {
    I32,
    I64,
    F32,
    F64,
    BOOL,
    ;

    fun decode(raw: String): String = when (this) {
        I32 -> "($raw).toInt()"
        I64 -> raw
        F32 -> "Float.fromBits(($raw).toInt())"
        F64 -> "Double.fromBits($raw)"
        BOOL -> "($raw != 0L)"
    }

    fun encode(value: String): String = when (this) {
        I32 -> "($value).toLong()"
        I64 -> value
        F32 -> "($value).toRawBits().toLong()"
        F64 -> "($value).toRawBits()"
        BOOL -> "(if ($value) 1L else 0L)"
    }
}

internal sealed interface KotlinValueInput {
    val type: KotlinValueType

    data class Slot(val slot: Int, override val type: KotlinValueType) : KotlinValueInput

    data class Field(val field: String, override val type: KotlinValueType) : KotlinValueInput
}

internal data class KotlinValueInstruction(
    val destinationSlot: Int,
    val inputs: List<KotlinValueInput>,
    val expression: String,
    val resultType: KotlinValueType,
) {
    fun expression(index: Int, read: (Int) -> String): String {
        var result = expression
        inputs.forEachIndexed { inputIndex, input ->
            val value = when (input) {
                is KotlinValueInput.Slot -> input.type.decode(read(input.slot))
                is KotlinValueInput.Field -> "i$index.${input.field}"
            }
            result = result.replace("@$inputIndex@", value)
        }
        return resultType.encode(result)
    }
}

internal fun valueInstruction(instruction: LinkedInstruction): KotlinValueInstruction? {
    fun field(name: String, type: KotlinValueType) = listOf(KotlinValueInput.Field(name, type))
    return when (instruction) {
        is NumericInstruction.I32ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("value", KotlinValueType.I32), "@0@", KotlinValueType.I32)
        is NumericInstruction.I64ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("value", KotlinValueType.I64), "@0@", KotlinValueType.I64)
        is NumericInstruction.F32ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("bits", KotlinValueType.I32), "@0@", KotlinValueType.I32)
        is NumericInstruction.F64ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("bits", KotlinValueType.I64), "@0@", KotlinValueType.I64)
        is AdminInstruction.CopySlot -> KotlinValueInstruction(instruction.destinationSlot, listOf(KotlinValueInput.Slot(instruction.sourceSlot, KotlinValueType.I64)), "@0@", KotlinValueType.I64)
        is VariableInstruction.GlobalGetS -> KotlinValueInstruction(instruction.destinationSlot, field("global.value", KotlinValueType.I64), "@0@", KotlinValueType.I64)
        else -> numericValueInstruction(instruction)
    }
}

/** Raw Long locals preserve the frame representation, including reused slots. */
internal class KotlinBlockValues(private val output: StringBuilder) {
    private val values = mutableMapOf<Int, String>()
    private val dirty = mutableSetOf<Int>()
    private var nextValue = 0

    fun emit(index: Int, instruction: KotlinValueInstruction) {
        val expression = instruction.expression(index, ::read)
        val destination = values[instruction.destinationSlot]
        if (destination == null) {
            val name = "v${nextValue++}"
            values[instruction.destinationSlot] = name
            output.appendLine("        var $name: Long = $expression")
        } else {
            output.appendLine("        $destination = $expression")
        }
        dirty.add(instruction.destinationSlot)
    }

    private fun read(slot: Int): String = values.getOrPut(slot) {
        "v${nextValue++}".also { output.appendLine("        var $it = vstack.getFrameSlot($slot)") }
    }

    fun flush() {
        dirty.forEach { slot -> output.appendLine("        vstack.setFrameSlot($slot, ${values.getValue(slot)})") }
        dirty.clear()
    }

    fun invalidate() {
        check(dirty.isEmpty())
        values.clear()
    }
}
