package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun <T : Index> IndexDecoder(
    context: DecoderContext,
    indexFactory: (UInt) -> T,
): Result<T, WasmDecodeError> = binding {
    val idx = context.reader.uint().bind()
    indexFactory(idx)
}
