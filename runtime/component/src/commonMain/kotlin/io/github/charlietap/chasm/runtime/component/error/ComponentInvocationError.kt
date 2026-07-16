package io.github.charlietap.chasm.runtime.component.error

import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.error.InvocationError

sealed interface ComponentInvocationError : ComponentError {

    data object StoreMismatch : ComponentInvocationError

    data class RootNotFound(
        val address: ComponentRootAddress,
    ) : ComponentInvocationError

    data class RootInitializing(
        val address: ComponentRootAddress,
    ) : ComponentInvocationError

    data class RootDead(
        val address: ComponentRootAddress,
    ) : ComponentInvocationError

    data class FunctionNotFound(
        val function: PreparedComponentFunctionIndex,
    ) : ComponentInvocationError

    data object InstanceDeallocated : ComponentInvocationError

    data object InstanceHasDependants : ComponentInvocationError

    data object InstanceActive : ComponentInvocationError

    data class InstanceTeardownFailure(
        val reason: String,
    ) : ComponentInvocationError

    data object CannotEnterComponentInstance : ComponentInvocationError

    data class CoreTrap(
        val error: InvocationError,
    ) : ComponentInvocationError

    data class InvalidCanonicalValue(
        val reason: String,
    ) : ComponentInvocationError

    data class MissingCanonicalDependency(
        val dependency: String,
    ) : ComponentInvocationError

    data class HostFunctionFailure(
        val reason: String,
    ) : ComponentInvocationError
}
