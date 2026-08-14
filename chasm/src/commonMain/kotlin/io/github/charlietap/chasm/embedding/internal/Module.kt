package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ParallelWasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor

@InternalChasmApi
suspend fun _module(
    bytes: ByteArray,
    config: ModuleConfig,
    taskExecutor: ParallelTaskExecutor,
): ChasmResult<Module, DecodeError> {
    return ParallelWasmModuleDecoder(config, bytes, taskExecutor)
        .mapError(ModuleDecoderError::toString)
        .mapError(::DecodeError)
        .map { internal -> Module(config, internal) }
        .fold(::Success, ::Error)
}
