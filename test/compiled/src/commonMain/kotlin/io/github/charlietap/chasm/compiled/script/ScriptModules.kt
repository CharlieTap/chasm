package io.github.charlietap.chasm.compiled.script

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.validator.WasmModuleValidator

object ScriptModules {

    fun decode(
        bytes: ByteArray,
        config: ModuleConfig,
    ): ChasmResult<Module, ChasmError> = WasmModuleDecoder(config, bytes).fold(
        { module -> ChasmResult.Success(module) },
        { error -> ChasmResult.Error(ChasmError.DecodeError(error.toString())) },
    )

    fun validate(
        bytes: ByteArray,
        config: ModuleConfig,
    ): ChasmResult<Module, ChasmError> = when (val decoded = decode(bytes, config)) {
        is ChasmResult.Error -> decoded
        is ChasmResult.Success -> WasmModuleValidator(config, decoded.result).fold(
            { module -> ChasmResult.Success(module) },
            { error -> ChasmResult.Error(ChasmError.ValidationError(error.toString())) },
        )
    }
}
