package io.github.charlietap.chasm.runtime.component.store

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.instance.RuntimeComponentInstance

sealed interface ComponentRootSlot {
    val rootState: ComponentRootState

    data class Initializing(
        val config: RuntimeConfig,
        val state: ComponentRuntimeState,
        val rootProviders: Set<ComponentRootAddress>,
    ) : ComponentRootSlot {
        override val rootState: ComponentRootState = ComponentRootState.Initializing
    }

    data class Live(
        val instance: RuntimeComponentInstance,
    ) : ComponentRootSlot {
        override val rootState: ComponentRootState = ComponentRootState.Live
    }

    data class Retained(
        val instance: RuntimeComponentInstance,
    ) : ComponentRootSlot {
        override val rootState: ComponentRootState = ComponentRootState.Retained
    }

    data object Dead : ComponentRootSlot {
        override val rootState: ComponentRootState = ComponentRootState.Dead
    }
}

enum class ComponentRootState {
    Initializing,
    Live,
    Retained,
    Dead,
}
