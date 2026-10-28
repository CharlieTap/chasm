package io.github.charlietap.chasm.executor.instantiator.compat

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.SharedStatus

typealias CompatibilityChecker = (ModuleDeclarations) -> Result<Unit, ModuleTrapError>

internal fun CompatibilityChecker(
    module: ModuleDeclarations,
): Result<Unit, ModuleTrapError> = binding {

    val addresses = module.memories.map { it.addressType } + module.tables.map { it.type.addressType }
    if (addresses.any { it == AddressType.I64 }) {
        Err(InstantiationError.UnsupportedMemory64Module).bind()
    }

    if (module.memories.any { it.shared == SharedStatus.Shared }) {
        Err(InstantiationError.UnsupportedThreadsModule).bind()
    }
}
