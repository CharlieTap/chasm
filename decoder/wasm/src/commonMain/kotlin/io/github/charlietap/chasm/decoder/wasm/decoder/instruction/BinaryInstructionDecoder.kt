package io.github.charlietap.chasm.decoder.wasm.decoder.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.BinaryControlInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control.ControlInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory.BinaryMemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory.MemoryInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric.BinaryNumericInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric.NumericInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.parametric.BinaryParametricInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.parametric.ParametricInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix.BinaryPrefixInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix.PrefixInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference.BinaryReferenceInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference.ReferenceInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.table.BinaryTableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.table.TableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.variable.BinaryVariableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.variable.VariableInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.vector.BinaryVectorInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.vector.VectorInstructionDecoder
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> = BinaryInstructionDecoder(
    reader = reader,
    opcode = opcode,
    numericInstructionDecoder = ::BinaryNumericInstructionDecoder,
    referenceInstructionDecoder = ::BinaryReferenceInstructionDecoder,
    parametricInstructionDecoder = ::BinaryParametricInstructionDecoder,
    variableInstructionDecoder = ::BinaryVariableInstructionDecoder,
    tableInstructionDecoder = ::BinaryTableInstructionDecoder,
    memoryInstructionDecoder = ::BinaryMemoryInstructionDecoder,
    controlInstructionDecoder = ::BinaryControlInstructionDecoder,
    prefixInstructionDecoder = ::BinaryPrefixInstructionDecoder,
    vectorInstructionDecoder = ::BinaryVectorInstructionDecoder,
)

internal fun BinaryInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    numericInstructionDecoder: NumericInstructionDecoder,
    referenceInstructionDecoder: ReferenceInstructionDecoder,
    parametricInstructionDecoder: ParametricInstructionDecoder,
    variableInstructionDecoder: VariableInstructionDecoder,
    tableInstructionDecoder: TableInstructionDecoder,
    memoryInstructionDecoder: MemoryInstructionDecoder,
    controlInstructionDecoder: ControlInstructionDecoder,
    prefixInstructionDecoder: PrefixInstructionDecoder,
    vectorInstructionDecoder: VectorInstructionDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when {
        NUMERIC_OPCODES.contains(opcode) -> numericInstructionDecoder(reader, opcode).bind()
        REFERENCE_OPCODES.contains(opcode) -> referenceInstructionDecoder(reader, opcode).bind()
        PARAMETRIC_OPCODES.contains(opcode) -> parametricInstructionDecoder(reader, opcode).bind()
        VARIABLE_OPCODES.contains(opcode) -> variableInstructionDecoder(reader, opcode).bind()
        TABLE_OPCODES.contains(opcode) -> tableInstructionDecoder(reader, opcode).bind()
        MEMORY_OPCODES.contains(opcode) -> memoryInstructionDecoder(reader, opcode).bind()
        CONTROL_OPCODES.contains(opcode) -> controlInstructionDecoder(reader, opcode).bind()
        PREFIXED_OPCODES.contains(opcode) -> prefixInstructionDecoder(reader, opcode).bind()
        VECTOR_OPCODES.contains(opcode) -> vectorInstructionDecoder(reader, opcode).bind()

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
        UNREACHABLE..IF,
        BR..RETURN_CALL_REF,
        BR_ON_NULL..BR_ON_NON_NULL,
    )
}

internal val VECTOR_OPCODES by lazy {
    setOf(
        PREFIX_VECTOR..PREFIX_VECTOR,
    )
}

internal val PREFIXED_OPCODES by lazy {
    setOf(
        PREFIX_MISC..PREFIX_MISC,
    )
}
