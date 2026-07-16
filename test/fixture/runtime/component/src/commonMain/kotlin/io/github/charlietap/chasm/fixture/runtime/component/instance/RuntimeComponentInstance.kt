package io.github.charlietap.chasm.fixture.runtime.component.instance

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.instance.ComponentAllocation
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.instance.RuntimeComponentInstance

fun runtimeComponentInstance(
    config: RuntimeConfig = runtimeConfig(),
    runtimeInfo: ComponentRuntimeInfo = componentRuntimeInfo(),
    state: ComponentRuntimeState = componentRuntimeState(),
    allocation: ComponentAllocation = componentAllocation(),
) = RuntimeComponentInstance(
    config = config,
    runtimeInfo = runtimeInfo,
    state = state,
    allocation = allocation,
)
