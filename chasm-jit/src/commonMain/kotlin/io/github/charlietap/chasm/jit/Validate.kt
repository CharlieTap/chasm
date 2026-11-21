@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.compiled.Module
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._config
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes._validated
import io.github.charlietap.chasm.jit.internal._classicModule
import io.github.charlietap.chasm.jit.internal._compiledModule
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

fun validate(module: Module): ChasmResult<Module, ModuleError.Invalid> {

    val classic = _classicModule(module)
    if (classic._validated()) {
        return Success(module)
    }

    return WasmModuleValidator(classic._config(), classic._module())
        .mapError(ModuleValidatorError::toString)
        .mapError(ModuleError::Invalid)
        .map { internal -> _compiledModule(_module(classic._config(), internal, classic._binary(), validated = true)) }
        .fold(::Success, ::Error)
}
