package io.github.charlietap.chasm.decoder.instruction.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias MemArgDecoder = (WasmBinaryReader) -> Result<MemArg, WasmDecodeError>
