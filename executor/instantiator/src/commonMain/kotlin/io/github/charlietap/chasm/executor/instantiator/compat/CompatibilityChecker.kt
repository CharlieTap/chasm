package io.github.charlietap.chasm.executor.instantiator.compat

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.type.AddressType

typealias CompatibilityChecker = (Module) -> Result<Unit, ModuleTrapError>

internal fun CompatibilityChecker(
    module: Module,
): Result<Unit, ModuleTrapError> = binding {

    val addresses = module.memories.map { it.type.addressType } + module.tables.map { it.type.addressType }
    if (addresses.any { it == AddressType.I64 }) {
        Err(InstantiationError.UnsupportedMemory64Module).bind()
    }
}
