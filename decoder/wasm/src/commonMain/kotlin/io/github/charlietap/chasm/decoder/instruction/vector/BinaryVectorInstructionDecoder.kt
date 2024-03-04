package io.github.charlietap.chasm.decoder.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

@Suppress("UNUSED_PARAMETER")
fun BinaryVectorInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
): Result<Instruction, WasmDecodeError> = Err(InstructionDecodeError.UnknownInstruction(prefix))
