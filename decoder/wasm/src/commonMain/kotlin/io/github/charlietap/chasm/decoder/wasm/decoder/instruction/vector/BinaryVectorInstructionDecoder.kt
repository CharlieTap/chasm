package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

@Suppress("UNUSED_PARAMETER")
internal fun BinaryVectorInstructionDecoder(
    reader: WasmBinaryReader,
    prefix: UByte,
): Result<Instruction, WasmDecodeError> = Err(InstructionDecodeError.UnknownInstruction(prefix))
