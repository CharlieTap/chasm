package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.ParallelWasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.module.ModuleFactoryError
import io.github.charlietap.chasm.module.WasmModuleFactory
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor

@InternalChasmApi
suspend fun _module(
    bytes: ByteArray,
    config: ModuleConfig,
    taskExecutor: ParallelTaskExecutor,
): ChasmResult<Module, ModuleError> {
    return ParallelWasmModuleDecoder(config, bytes, taskExecutor).fold(
        success = { internal ->
            Success(_module(config, internal, bytes))
        },
        failure = { error ->
            Error(ModuleError.Malformed(error.toString()))
        },
    )
}

@InternalChasmApi
suspend fun _module(
    bytes: ByteArray,
    config: ModuleConfig,
    taskExecutor: ParallelTaskExecutor,
    validate: Boolean,
): ChasmResult<Module, ModuleError> {

    if (!validate) {
        return _module(bytes, config, taskExecutor)
    }

    return WasmModuleFactory(config, bytes, taskExecutor).fold(
        success = { internal ->
            Success(_module(config, internal, bytes, validated = true))
        },
        failure = { error ->
            when (error) {
                is ModuleFactoryError.Malformed -> Error(ModuleError.Malformed(error.error.toString()))
                is ModuleFactoryError.Invalid -> Error(ModuleError.Invalid(error.error.toString()))
            }
        },
    )
}
