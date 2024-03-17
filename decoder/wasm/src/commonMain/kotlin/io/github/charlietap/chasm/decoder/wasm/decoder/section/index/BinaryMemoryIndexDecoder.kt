package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMemoryIndexDecoder(
    reader: WasmBinaryReader,
): Result<Index.MemoryIndex, WasmDecodeError> = BinaryIndexDecoder(reader, Index::MemoryIndex)
