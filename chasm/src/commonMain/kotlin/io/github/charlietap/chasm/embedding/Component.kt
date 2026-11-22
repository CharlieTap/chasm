package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.decoder.ComponentDecoder
import io.github.charlietap.chasm.decoder.WasmComponentDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.stream.SourceReader

suspend fun component(
    sourceReader: SourceReader,
    config: ComponentConfig = ComponentConfig(),
): ChasmResult<Component, ModuleError.Malformed> {
    return component(
        source = sourceReader,
        config = config,
        decoder = ::WasmComponentDecoder,
    )
}

suspend fun component(
    bytes: ByteArray,
    config: ComponentConfig = ComponentConfig(),
): ChasmResult<Component, ModuleError.Malformed> {
    return component(
        source = bytes,
        config = config,
        decoder = ::WasmComponentDecoder,
    )
}

internal fun <Source> component(
    source: Source,
    config: ComponentConfig,
    decoder: ComponentDecoder<Source>,
): ChasmResult<Component, ModuleError.Malformed> {
    return decoder(config, source).fold(
        success = { internal ->
            Success(Component(config, internal))
        },
        failure = { error ->
            Error(ModuleError.Malformed(error.toString()))
        },
    )
}
