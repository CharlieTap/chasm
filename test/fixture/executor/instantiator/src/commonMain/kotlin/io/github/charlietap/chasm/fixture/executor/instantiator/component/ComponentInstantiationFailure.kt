package io.github.charlietap.chasm.fixture.executor.instantiator.component

import io.github.charlietap.chasm.executor.instantiator.component.ComponentInstantiationFailure
import io.github.charlietap.chasm.fixture.runtime.component.error.unsupportedFeatureComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.error.moduleRuntimeError
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

fun componentInstantiationFailure(
    error: ComponentInstantiationError = unsupportedFeatureComponentInstantiationError(),
) = ComponentInstantiationFailure.Component(error)

fun coreModuleComponentInstantiationFailure(
    error: ModuleTrapError = moduleRuntimeError(),
) = ComponentInstantiationFailure.CoreModule(error)

fun componentInvocationComponentInstantiationFailure(
    error: ComponentInvocationError,
) = ComponentInstantiationFailure.ComponentInvocation(error)

fun invalidPreparedComponentInstantiationFailure(
    reason: String = "reason",
) = ComponentInstantiationFailure.InvalidPreparedComponent(reason)
