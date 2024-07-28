package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun TableIndexDecoder(
    context: DecoderContext,
): Result<Index.TableIndex, WasmDecodeError> = IndexDecoder(context, Index::TableIndex)
