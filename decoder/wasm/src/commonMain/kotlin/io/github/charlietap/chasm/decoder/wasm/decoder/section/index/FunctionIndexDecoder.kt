package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun FunctionIndexDecoder(
    context: DecoderContext,
): Result<Index.FunctionIndex, WasmDecodeError> = IndexDecoder(context, Index::FunctionIndex)
