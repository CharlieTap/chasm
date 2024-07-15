package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun TypeIndexDecoder(
    context: DecoderContext,
): Result<Index.TypeIndex, WasmDecodeError> = IndexDecoder(context, Index::TypeIndex)
