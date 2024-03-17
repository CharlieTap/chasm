package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction.BlockType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias BlockTypeDecoder = (WasmBinaryReader) -> Result<BlockType, WasmDecodeError>
