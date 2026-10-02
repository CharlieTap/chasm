package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.stream.SourceReader

fun module(
    sourceReader: SourceReader,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<Module, DecodeError> {
    return module(
        source = sourceReader,
        config = config,
        decoder = ::WasmModuleDecoder,
    )
}

fun module(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<Module, DecodeError> {
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
): ChasmResult<Module, DecodeError> {
    return decoder(config, source)
        .mapError(ModuleDecoderError::toString)
        .mapError(::DecodeError)
        .map { internal ->
            Module(
                config = config,
                module = internal,
                binary = source as? ByteArray,
            )
        }.fold(::Success, ::Error)
}
