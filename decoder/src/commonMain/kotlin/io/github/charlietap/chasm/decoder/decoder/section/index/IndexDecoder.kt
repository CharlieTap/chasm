package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.ReaderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun <T> IndexDecoder(
    context: ReaderContext,
    indexFactory: (UInt) -> T,
): Result<T, WasmDecodeError> {
    val idx = context.reader.uint()
    return Ok(indexFactory(idx))
}
