package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError.ValidationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

fun validate(module: Module): ChasmResult<Module, ValidationError> {
    return validate(module, ::WasmModuleValidator)
}

internal fun validate(
    module: Module,
    validator: WasmModuleValidator,
): ChasmResult<Module, ValidationError> {
    return validator(module.config, module.module)
        .mapError(ModuleValidatorError::toString)
        .mapError(::ValidationError)
        .map { internal ->
            Module(
                config = module.config,
                module = internal,
            )
        }.fold(::Success, ::Error)
}
