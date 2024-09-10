package io.github.charlietap.chasm.decoder.decoder.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.control.ControlInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.numeric.NumericInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.parametric.ParametricInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.prefix.PrefixInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.REF_AS_NON_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.REF_NULL
import io.github.charlietap.chasm.decoder.decoder.instruction.reference.ReferenceInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.table.TableInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.variable.VariableInstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.vector.VectorInstructionDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun InstructionDecoder(
    context: DecoderContext,
): Result<Instruction, WasmDecodeError> = InstructionDecoder(
    context = context,
    numericInstructionDecoder = ::NumericInstructionDecoder,
    referenceInstructionDecoder = ::ReferenceInstructionDecoder,
    parametricInstructionDecoder = ::ParametricInstructionDecoder,
    variableInstructionDecoder = ::VariableInstructionDecoder,
    tableInstructionDecoder = ::TableInstructionDecoder,
    memoryInstructionDecoder = ::MemoryInstructionDecoder,
    controlInstructionDecoder = ::ControlInstructionDecoder,
    prefixInstructionDecoder = ::PrefixInstructionDecoder,
    vectorInstructionDecoder = ::VectorInstructionDecoder,
)

internal fun InstructionDecoder(
    context: DecoderContext,
    numericInstructionDecoder: Decoder<NumericInstruction>,
    referenceInstructionDecoder: Decoder<ReferenceInstruction>,
    parametricInstructionDecoder: Decoder<ParametricInstruction>,
    variableInstructionDecoder: Decoder<VariableInstruction>,
    tableInstructionDecoder: Decoder<TableInstruction>,
    memoryInstructionDecoder: Decoder<MemoryInstruction>,
    controlInstructionDecoder: Decoder<ControlInstruction>,
    prefixInstructionDecoder: Decoder<Instruction>,
    vectorInstructionDecoder: Decoder<VectorInstruction>,
): Result<Instruction, WasmDecodeError> = binding {
    val opcode = context.reader.peek().ubyte().bind()
    when {
        NUMERIC_OPCODES.contains(opcode) -> numericInstructionDecoder(context).bind()
        REFERENCE_OPCODES.contains(opcode) -> referenceInstructionDecoder(context).bind()
        PARAMETRIC_OPCODES.contains(opcode) -> parametricInstructionDecoder(context).bind()
        VARIABLE_OPCODES.contains(opcode) -> variableInstructionDecoder(context).bind()
        TABLE_OPCODES.contains(opcode) -> tableInstructionDecoder(context).bind()
        MEMORY_OPCODES.contains(opcode) -> memoryInstructionDecoder(context).bind()
        CONTROL_OPCODES.contains(opcode) -> controlInstructionDecoder(context).bind()
        PREFIXED_OPCODES.contains(opcode) -> prefixInstructionDecoder(context).bind()
        VECTOR_OPCODES.contains(opcode) -> vectorInstructionDecoder(context).bind()

        else -> Err(InstructionDecodeError.UnknownInstruction(opcode)).bind<Instruction>()
    }
}

private fun Set<UIntRange>.contains(opcode: UByte): Boolean = any { range ->
    range.contains(opcode)
}

internal val NUMERIC_OPCODES: Set<UIntRange> by lazy {
    setOf(
        I32_CONST..I64_EXTEND32_S,
    )
}

internal val REFERENCE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        REF_NULL..REF_AS_NON_NULL,
    )
}

internal val PARAMETRIC_OPCODES: Set<UIntRange> by lazy {
    setOf(
        DROP..SELECT_W_TYPE,
    )
}

internal val VARIABLE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        LOCAL_GET..GLOBAL_SET,
    )
}

internal val TABLE_OPCODES: Set<UIntRange> by lazy {
    setOf(
        TABLE_GET..TABLE_SET,
    )
}

internal val MEMORY_OPCODES: Set<UIntRange> by lazy {
    setOf(
        I32_LOAD..MEMORY_GROW,
    )
}

internal val CONTROL_OPCODES: Set<UIntRange> by lazy {
    setOf(
        UNREACHABLE..ELSE,
        THROW..THROW,
        THROW_REF..RETURN_CALL_REF,
        TRY_TABLE..TRY_TABLE,
        BR_ON_NULL..BR_ON_NON_NULL,
    )
}

internal val VECTOR_OPCODES by lazy {
    setOf(
        PREFIX_FD..PREFIX_FD,
    )
}

internal val PREFIXED_OPCODES by lazy {
    setOf(
        PREFIX_FB..PREFIX_FC,
    )
}
