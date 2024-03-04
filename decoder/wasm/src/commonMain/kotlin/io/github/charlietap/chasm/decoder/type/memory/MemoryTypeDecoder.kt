package io.github.charlietap.chasm.decoder.type.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias MemoryTypeDecoder = (WasmBinaryReader) -> Result<MemoryType, WasmDecodeError>
