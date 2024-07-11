package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

typealias WasmModuleValidator = (Module) -> Result<Module, ModuleValidatorError>
