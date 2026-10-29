package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

fun inspectModule(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): ChasmResult<ModuleInfo, DecodeError> =
    WasmModuleDecoder(config, bytes)
        .mapError(ModuleDecoderError::toString)
        .andThen(::describe)
        .mapError(::DecodeError)
        .fold(::Success, ::Error)

private fun describe(
    module: Module,
): Result<ModuleInfo, String> = try {
    Ok(declarationsModuleInfo(ModuleDeclarationsFactory(module)))
} catch (missing: NoSuchElementException) {
    Err(undeclared(missing))
} catch (missing: IndexOutOfBoundsException) {
    Err(undeclared(missing))
}

private fun undeclared(
    failure: RuntimeException,
) = "the module refers to something it does not declare: ${failure.message}"
