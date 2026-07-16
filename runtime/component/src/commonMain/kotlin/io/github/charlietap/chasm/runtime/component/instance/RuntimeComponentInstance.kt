package io.github.charlietap.chasm.runtime.component.instance

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo

class RuntimeComponentInstance(
    val config: RuntimeConfig,
    val runtimeInfo: ComponentRuntimeInfo,
    val state: ComponentRuntimeState,
    val allocation: ComponentAllocation,
)
