package io.github.charlietap.chasm.decoder.section.index

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

internal fun <T : Index> BinaryIndexDecoder(
    reader: WasmBinaryReader,
    indexFactory: (UInt) -> T,
): Result<T, WasmDecodeError> = binding {
    val idx = reader.uint().bind()
    indexFactory(idx)
}
