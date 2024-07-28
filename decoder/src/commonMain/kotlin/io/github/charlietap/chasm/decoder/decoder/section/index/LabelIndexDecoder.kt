package io.github.charlietap.chasm.decoder.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun LabelIndexDecoder(
    context: DecoderContext,
): Result<Index.LabelIndex, WasmDecodeError> = IndexDecoder(context, Index::LabelIndex)
