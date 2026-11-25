package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.decoder.ParallelWasmModuleDecoder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._decodedModule
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.module.ModuleFactoryError
import io.github.charlietap.chasm.module.WasmModuleFactory
import io.github.charlietap.chasm.parallel.coroutines.DefaultCoroutineParallelTaskExecutor
import io.github.charlietap.chasm.stream.SourceReader
import io.github.charlietap.chasm.validator.ParallelWasmModuleValidator
import io.github.charlietap.chasm.ast.module.Module as InternalModule

suspend fun module(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
    validate: Boolean = true,
): ChasmResult<Module, ModuleError> {

    if (!validate) {
        return ParallelWasmModuleDecoder(config, bytes, DefaultCoroutineParallelTaskExecutor)
            .mapError { error -> ModuleError.Malformed(error.toString()) }
            .map { internal -> module(config, internal, bytes, validated = false) }
            .fold(::Success, ::Error)
    }

    return WasmModuleFactory(config, bytes, DefaultCoroutineParallelTaskExecutor)
        .mapError { error ->
            when (error) {
                is ModuleFactoryError.Malformed -> ModuleError.Malformed(error.error.toString())
                is ModuleFactoryError.Invalid -> ModuleError.Invalid(error.error.toString())
            }
        }
        .map { internal -> module(config, internal, bytes, validated = true) }
        .fold(::Success, ::Error)
}

suspend fun module(
    sourceReader: SourceReader,
    config: ModuleConfig = ModuleConfig(),
    validate: Boolean = true,
): ChasmResult<Module, ModuleError> {

    val decoded = WasmModuleDecoder(config, sourceReader).getOrElse { error ->
        return Error(ModuleError.Malformed(error.toString()))
    }

    if (!validate) {
        return Success(module(config, decoded, null, validated = false))
    }

    return ParallelWasmModuleValidator(config, decoded, DefaultCoroutineParallelTaskExecutor)
        .mapError { error -> ModuleError.Invalid(error.toString()) }
        .map { internal -> module(config, internal, null, validated = true) }
        .fold(::Success, ::Error)
}

suspend fun validate(module: Module): ChasmResult<Module, ModuleError.Invalid> {

    val decoded = module._payload() as? DecodedModule
    if (decoded == null || decoded.validated) {
        return Success(module)
    }

    return ParallelWasmModuleValidator(decoded.config, decoded.module, DefaultCoroutineParallelTaskExecutor)
        .mapError { error -> ModuleError.Invalid(error.toString()) }
        .map { internal -> validated(module, decoded, internal) }
        .fold(::Success, ::Error)
}

private fun module(
    config: ModuleConfig,
    internal: InternalModule,
    binary: ByteArray?,
    validated: Boolean,
): Module {

    val decoded = _decodedModule(config, internal, binary, validated)
    val info = moduleInfo(decoded.declarations)

    return _module(info.imports, info.exports, decoded)
}

private fun validated(
    module: Module,
    decoded: DecodedModule,
    internal: InternalModule,
): Module {

    if (internal !== decoded.module) {
        return module(decoded.config, internal, decoded.binary, validated = true)
    }

    val payload = DecodedModule(decoded.config, internal, decoded.declarations, decoded.binary, validated = true)

    return _module(module.imports, module.exports, payload)
}
