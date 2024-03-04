package io.github.charlietap.chasm.decoder.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ControlInstructionDecoder = (WasmBinaryReader, UByte) -> Result<Instruction, WasmDecodeError>
