package io.github.charlietap.chasm.decoder.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias InstructionDecoder = (WasmBinaryReader, UByte) -> Result<Instruction, WasmDecodeError>
