package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.CodeBodyDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.control.ControlInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.numeric.NumericInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.parametric.ParametricInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.prefix.PREFIX_FB
import io.github.charlietap.chasm.decoder.decoder.instruction.prefix.PREFIX_FE
import io.github.charlietap.chasm.decoder.decoder.instruction.prefix.PrefixedInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.REF_AS_NON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.REF_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.ReferenceInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.table.TableInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.variable.VariableInstructionDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun InstructionDecoder(
    context: CodeBodyDecoderContext,
): Result<Instruction, WasmDecodeError> = InstructionDecoder(
    context = context,
    numericInstructionDecoder = ::NumericInstructionDecoder,
    referenceInstructionDecoder = ::ReferenceInstructionDecoder,
    parametricInstructionDecoder = ::ParametricInstructionDecoder,
    variableInstructionDecoder = ::VariableInstructionDecoder,
    tableInstructionDecoder = ::TableInstructionDecoder,
    memoryInstructionDecoder = ::MemoryInstructionDecoder,
    controlInstructionDecoder = ::ControlInstructionDecoder,
    prefixInstructionDecoder = ::PrefixedInstructionDecoder,
)

internal inline fun InstructionDecoder(
    context: CodeBodyDecoderContext,
    crossinline numericInstructionDecoder: CodeBodyDecoder<NumericInstruction>,
    crossinline referenceInstructionDecoder: CodeBodyDecoder<ReferenceInstruction>,
    crossinline parametricInstructionDecoder: CodeBodyDecoder<ParametricInstruction>,
    crossinline variableInstructionDecoder: CodeBodyDecoder<VariableInstruction>,
    crossinline tableInstructionDecoder: CodeBodyDecoder<TableInstruction>,
    crossinline memoryInstructionDecoder: CodeBodyDecoder<MemoryInstruction>,
    crossinline controlInstructionDecoder: CodeBodyDecoder<ControlInstruction>,
    crossinline prefixInstructionDecoder: CodeBodyDecoder<Instruction>,
): Result<Instruction, WasmDecodeError> {
    val opcode = context.reader
        .peekUByte()
    return when (OPCODE_GROUPS[opcode.toInt()]) {
        NUMERIC_GROUP -> numericInstructionDecoder(context)
        REFERENCE_GROUP -> referenceInstructionDecoder(context)
        PARAMETRIC_GROUP -> parametricInstructionDecoder(context)
        VARIABLE_GROUP -> variableInstructionDecoder(context)
        TABLE_GROUP -> tableInstructionDecoder(context)
        MEMORY_GROUP -> memoryInstructionDecoder(context)
        CONTROL_GROUP -> controlInstructionDecoder(context)
        PREFIXED_GROUP -> prefixInstructionDecoder(context)

        else -> Err(InstructionDecodeError.UnknownInstruction(opcode))
    }
}

internal const val NUMERIC_GROUP: Byte = 1
internal const val REFERENCE_GROUP: Byte = 2
internal const val PARAMETRIC_GROUP: Byte = 3
internal const val VARIABLE_GROUP: Byte = 4
internal const val TABLE_GROUP: Byte = 5
internal const val MEMORY_GROUP: Byte = 6
internal const val CONTROL_GROUP: Byte = 7
internal const val PREFIXED_GROUP: Byte = 8

internal val CONTROL_OPCODES: Set<UIntRange> by lazy {
    setOf(
        UNREACHABLE..IF,
        THROW..THROW,
        THROW_REF..THROW_REF,
        BR..RETURN_CALL_REF,
        TRY_TABLE..TRY_TABLE,
        BR_ON_NULL..BR_ON_NON_NULL,
    )
}

internal val NUMERIC_OPCODES: Set<UIntRange> by lazy {
    setOf(
        I32_CONST..I64_EXTEND32_S,
    )
}

internal val MEMORY_OPCODES: Set<UIntRange> by lazy {
    setOf(
        I32_LOAD..MEMORY_GROW,
    )
}

internal val PARAMETRIC_OPCODES: Set<UIntRange> by lazy {
    setOf(
        DROP..SELECT_W_TYPE,
    )
}

internal val REFERENCE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        REF_NULL..REF_AS_NON_NULL,
    )
}

internal val TABLE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        TABLE_GET..TABLE_SET,
    )
}

internal val VARIABLE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        LOCAL_GET..GLOBAL_SET,
    )
}

internal val PREFIXED_OPCODES by lazy {
    setOf(
        PREFIX_FB..PREFIX_FE,
    )
}

// Maps every opcode to the group that decodes it so dispatch is a single lookup per instruction
internal val OPCODE_GROUPS: ByteArray = ByteArray(UByte.MAX_VALUE.toInt() + 1).also { groups ->
    fun Set<UIntRange>.assign(group: Byte) = forEach { range ->
        range.forEach { opcode -> groups[opcode.toInt()] = group }
    }
    PREFIXED_OPCODES.assign(PREFIXED_GROUP)
    CONTROL_OPCODES.assign(CONTROL_GROUP)
    MEMORY_OPCODES.assign(MEMORY_GROUP)
    TABLE_OPCODES.assign(TABLE_GROUP)
    VARIABLE_OPCODES.assign(VARIABLE_GROUP)
    PARAMETRIC_OPCODES.assign(PARAMETRIC_GROUP)
    REFERENCE_OPCODES.assign(REFERENCE_GROUP)
    NUMERIC_OPCODES.assign(NUMERIC_GROUP)
}
