package io.github.charlietap.chasm.decoder.instruction.parametric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.instruction.DROP
import io.github.charlietap.chasm.decoder.instruction.SELECT
import io.github.charlietap.chasm.decoder.instruction.SELECT_W_TYPE
import io.github.charlietap.chasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryParametricInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> =
    BinaryParametricInstructionDecoder(
        reader = reader,
        opcode = opcode,
        vectorDecoder = ::BinaryVectorDecoder,
        valueTypeDecoder = ::BinaryValueTypeDecoder,
    )

internal fun BinaryParametricInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
    vectorDecoder: VectorDecoder<ValueType>,
    valueTypeDecoder: ValueTypeDecoder,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        DROP -> {
            ParametricInstruction.Drop
        }
        SELECT -> {
            ParametricInstruction.Select
        }
        SELECT_W_TYPE -> {
            val valTypes = vectorDecoder(reader, valueTypeDecoder).bind()
            ParametricInstruction.SelectWithType(valTypes.vector)
        }

        else -> Err(InstructionDecodeError.InvalidParametricInstruction(opcode)).bind<Instruction>()
    }
}
