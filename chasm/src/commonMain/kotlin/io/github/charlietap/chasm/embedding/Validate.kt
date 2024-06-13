package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.error.ChasmError.ValidationError
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.WasmModuleValidatorImpl

fun validate(module: Module): ChasmResult<Module, ValidationError> {
    return validate(module, ::WasmModuleValidatorImpl)
}

internal fun validate(
    module: Module,
    validator: WasmModuleValidator,
): ChasmResult<Module, ValidationError> {
    return validator(module)
        .mapError(::ValidationError)
        .fold(::Success, ::Error)
}
