@file:JvmName("DataIndexDecoderKt")

package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import kotlin.jvm.JvmName

internal fun DataIndexDecoder(
    context: DecoderContext,
): Result<Index.DataIndex, WasmDecodeError> = IndexDecoder(context, Index::DataIndex)
