package io.github.charlietap.chasm.runtime.component.error

import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.store.ComponentRootState

sealed interface ComponentInstantiationError : ComponentError {

    data class UnsupportedFeature(
        val feature: UnsupportedComponentFeature,
    ) : ComponentInstantiationError

    data class MissingImport(
        val path: List<String>,
    ) : ComponentInstantiationError

    data class UnexpectedImport(
        val path: List<String>,
    ) : ComponentInstantiationError

    data class ImportTypeMismatch(
        val path: List<String>,
    ) : ComponentInstantiationError

    data class RootNotFound(
        val address: ComponentRootAddress,
    ) : ComponentInstantiationError

    data class InvalidRootTransition(
        val address: ComponentRootAddress,
        val current: ComponentRootState,
        val target: ComponentRootState,
    ) : ComponentInstantiationError

    data class RootRuntimeStateMismatch(
        val address: ComponentRootAddress,
    ) : ComponentInstantiationError

    data class RootProviderUnavailable(
        val address: ComponentRootAddress,
    ) : ComponentInstantiationError

    data class ResourceTypeUnavailable(
        val address: RuntimeResourceTypeAddress,
    ) : ComponentInstantiationError
}
