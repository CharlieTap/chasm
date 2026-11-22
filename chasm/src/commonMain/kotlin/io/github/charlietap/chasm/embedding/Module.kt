@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.stream.SourceReader

fun module(
    sourceReader: SourceReader,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<Module, ModuleError> {
    return module(
        source = sourceReader,
        config = config,
        decoder = ::WasmModuleDecoder,
    )
}

fun module(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<Module, ModuleError> {
    return module(
        source = bytes,
        config = config,
        decoder = ::WasmModuleDecoder,
    )
}

internal fun <Source> module(
    source: Source,
    config: ModuleConfig,
    decoder: ModuleDecoder<Source>,
): ChasmResult<Module, ModuleError.Malformed> {
    return decoder(config, source).fold(
        success = { internal ->
            Success(_module(config = config, module = internal, binary = source as? ByteArray))
        },
        failure = { error ->
            Error(ModuleError.Malformed(error.toString()))
        },
    )
}
