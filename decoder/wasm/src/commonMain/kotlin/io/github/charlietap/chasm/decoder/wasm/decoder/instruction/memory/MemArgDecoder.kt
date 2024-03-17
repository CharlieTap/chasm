package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias MemArgDecoder = (WasmBinaryReader) -> Result<MemArg, WasmDecodeError>
