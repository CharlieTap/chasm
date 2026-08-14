package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.ReaderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun LocalIndexDecoder(
    context: ReaderContext,
): Result<Index.LocalIndex, WasmDecodeError> = IndexDecoder(context, Index::LocalIndex)
