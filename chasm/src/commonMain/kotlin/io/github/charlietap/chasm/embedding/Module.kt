package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.reader.ByteArraySourceReader
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.stream.SourceReader

fun module(
    sourceReader: SourceReader,
    config: ModuleConfig = ModuleConfig.default(),
): ChasmResult<Module, DecodeError> {
    return module(sourceReader, config, ::WasmModuleDecoder)
}

fun module(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig.default(),
): ChasmResult<Module, DecodeError> {
    return module(ByteArraySourceReader(bytes), config, ::WasmModuleDecoder)
}

internal fun module(
    reader: SourceReader,
    config: ModuleConfig,
    decoder: ModuleDecoder,
): ChasmResult<Module, DecodeError> {
    return decoder(config, reader)
        .mapError(ModuleDecoderError::toString)
        .mapError(::DecodeError)
        .map { internal ->
            Module(
                config = config,
                module = internal,
            )
        }.fold(::Success, ::Error)
}
