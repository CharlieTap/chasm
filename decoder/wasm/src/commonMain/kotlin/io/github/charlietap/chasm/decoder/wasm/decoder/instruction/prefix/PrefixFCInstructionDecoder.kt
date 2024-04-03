package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.prefix

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias PrefixFCInstructionDecoder = (WasmBinaryReader, UInt) -> Result<Instruction, WasmDecodeError>
