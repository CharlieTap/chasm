package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun <T : Index> BinaryIndexDecoder(
    reader: WasmBinaryReader,
    indexFactory: (UInt) -> T,
): Result<T, WasmDecodeError> = binding {
    val idx = reader.uint().bind()
    indexFactory(idx)
}
