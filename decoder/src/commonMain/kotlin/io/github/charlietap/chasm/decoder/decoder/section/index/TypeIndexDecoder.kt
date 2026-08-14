package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.ReaderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TypeIndexDecoder(
    context: ReaderContext,
): Result<Index.TypeIndex, WasmDecodeError> = IndexDecoder(context, Index::TypeIndex)
