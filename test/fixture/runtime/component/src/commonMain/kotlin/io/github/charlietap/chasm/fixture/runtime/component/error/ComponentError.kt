package io.github.charlietap.chasm.fixture.runtime.component.error

import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.store.ComponentRootState

fun unsupportedFeatureComponentPreparationError(
    feature: UnsupportedComponentFeature = UnsupportedComponentFeature.CanonicalExecution,
) = ComponentPreparationError.UnsupportedFeature(feature)

fun canonicalLayoutUnavailableComponentPreparationError(
    reason: String = "reason",
) = ComponentPreparationError.CanonicalLayoutUnavailable(reason)

fun invalidPreparedComponentPreparationError(
    reason: String = "reason",
) = ComponentPreparationError.InvalidPreparedComponent(reason)

fun unsupportedFeatureComponentInstantiationError(
    feature: UnsupportedComponentFeature = UnsupportedComponentFeature.CanonicalExecution,
) = ComponentInstantiationError.UnsupportedFeature(feature)

fun missingImportComponentInstantiationError(
    path: List<String> = emptyList(),
) = ComponentInstantiationError.MissingImport(path)

fun importTypeMismatchComponentInstantiationError(
    path: List<String> = emptyList(),
) = ComponentInstantiationError.ImportTypeMismatch(path)

fun invalidRootTransitionComponentInstantiationError(
    address: ComponentRootAddress = componentRootAddress(),
    current: ComponentRootState = ComponentRootState.Initializing,
    target: ComponentRootState = ComponentRootState.Live,
) = ComponentInstantiationError.InvalidRootTransition(
    address = address,
    current = current,
    target = target,
)

fun rootRuntimeStateMismatchComponentInstantiationError(
    address: ComponentRootAddress = componentRootAddress(),
) = ComponentInstantiationError.RootRuntimeStateMismatch(address)

fun rootProviderUnavailableComponentInstantiationError(
    address: ComponentRootAddress = componentRootAddress(),
) = ComponentInstantiationError.RootProviderUnavailable(address)

fun resourceTypeUnavailableComponentInstantiationError(
    address: RuntimeResourceTypeAddress,
) = ComponentInstantiationError.ResourceTypeUnavailable(address)

fun rootInitializingComponentInvocationError(
    address: ComponentRootAddress = componentRootAddress(),
) = ComponentInvocationError.RootInitializing(address)

fun rootDeadComponentInvocationError(
    address: ComponentRootAddress = componentRootAddress(),
) = ComponentInvocationError.RootDead(address)

fun instanceHasDependantsComponentInvocationError() = ComponentInvocationError.InstanceHasDependants

fun instanceActiveComponentInvocationError() = ComponentInvocationError.InstanceActive
