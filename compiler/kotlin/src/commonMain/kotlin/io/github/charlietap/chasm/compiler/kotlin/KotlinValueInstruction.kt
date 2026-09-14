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
    UNIT,
    ;

    fun decode(raw: String): String = when (this) {
        I32 -> "($raw).toInt()"
        I64 -> raw
        F32 -> "Float.fromBits(($raw).toInt())"
        F64 -> "Double.fromBits($raw)"
        BOOL -> "($raw != 0L)"
        UNIT -> error("Unit has no frame representation")
    }

    fun encode(value: String): String = when (this) {
        I32 -> "($value).toLong()"
        I64 -> value
        F32 -> "($value).toRawBits().toLong()"
        F64 -> "($value).toRawBits()"
        BOOL -> "(if ($value) 1L else 0L)"
        UNIT -> value
    }
}

internal sealed interface KotlinValueInput {
    val type: KotlinValueType

    data class Slot(val slot: Int, override val type: KotlinValueType, val rawWord: Boolean = false) : KotlinValueInput

    data class Field(val field: String, override val type: KotlinValueType) : KotlinValueInput

    data class Literal(val source: String, override val type: KotlinValueType) : KotlinValueInput
}

internal data class KotlinValueInstruction(
    val destinationSlot: Int?,
    val inputs: List<KotlinValueInput>,
    val expression: String,
    val resultType: KotlinValueType,
    // Null denotes an untyped word transfer; resultType still describes the
    // Kotlin operation's representation, which can differ for memory loads.
    val destinationType: KotlinValueType? = resultType,
    val canonicalResult: Boolean = true,
) {
    fun expression(index: Int, read: (Int) -> String): String = resultType.encode(operationExpression(index) { input -> input.type.decode(read(input.slot)) })

    fun operationExpression(index: Int, read: (KotlinValueInput.Slot) -> String): String {
        var result = expression.replace("%binding%", "i$index")
        inputs.forEachIndexed { inputIndex, input ->
            val value = when (input) {
                is KotlinValueInput.Slot -> read(input)
                is KotlinValueInput.Field -> "i$index.${input.field}"
                is KotlinValueInput.Literal -> input.source
            }
            result = result.replace("@$inputIndex@", value)
        }
        return result
    }
}

internal fun valueInstruction(instruction: LinkedInstruction): KotlinValueInstruction? {
    fun field(name: String, type: KotlinValueType) = listOf(KotlinValueInput.Field(name, type))
    return when (instruction) {
        is NumericInstruction.I32ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("value", KotlinValueType.I32), "@0@", KotlinValueType.I32)
        is NumericInstruction.I64ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("value", KotlinValueType.I64), "@0@", KotlinValueType.I64)
        is NumericInstruction.F32ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("bits", KotlinValueType.I32), "@0@", KotlinValueType.I32, KotlinValueType.F32, canonicalResult = false)
        is NumericInstruction.F64ConstS -> KotlinValueInstruction(instruction.destinationSlot, field("bits", KotlinValueType.I64), "@0@", KotlinValueType.I64, KotlinValueType.F64, canonicalResult = false)
        is AdminInstruction.CopySlot -> KotlinValueInstruction(instruction.destinationSlot, listOf(KotlinValueInput.Slot(instruction.sourceSlot, KotlinValueType.I64, rawWord = true)), "@0@", KotlinValueType.I64, null)
        is VariableInstruction.GlobalGetS -> KotlinValueInstruction(instruction.destinationSlot, field("global.value", KotlinValueType.I64), "@0@", KotlinValueType.I64, null)
        is VariableInstruction.GlobalSetI -> KotlinValueInstruction(null, field("value", KotlinValueType.I64), "%binding%.global.value = @0@", KotlinValueType.UNIT)
        is VariableInstruction.GlobalSetS -> KotlinValueInstruction(null, listOf(KotlinValueInput.Slot(instruction.sourceSlot, KotlinValueType.I64, rawWord = true)), "%binding%.global.value = @0@", KotlinValueType.UNIT)
        is NumericInstruction.I32BitFieldExtractS -> KotlinValueInstruction(
            instruction.destinationSlot,
            listOf(KotlinValueInput.Slot(instruction.operandSlot, KotlinValueType.I32)),
            "io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.valueI32BitFieldExtract(@0@, %binding%.shift, %binding%.mask)",
            KotlinValueType.I32,
        )
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
        if (instruction.destinationSlot == null) {
            output.appendLine("        $expression")
            return
        }
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
