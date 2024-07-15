package io.github.charlietap.chasm.decoder.wasm.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.context.BlockContextImpl
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun BlockScope(
    context: DecoderContext,
    blockEndOpcode: UByte,
): Result<DecoderContext, WasmDecodeError> = context.copy(
    blockContext = BlockContextImpl(
        blockEndOpcode = blockEndOpcode,
    ),
).let(::Ok)
