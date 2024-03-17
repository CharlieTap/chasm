package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.reference

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ReferenceInstructionDecoder = (WasmBinaryReader, UByte) -> Result<Instruction, WasmDecodeError>
