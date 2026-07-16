package io.github.charlietap.chasm.runtime.component.instance

import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class ComponentRuntimeState(
    val coreInstances: Array<ModuleInstance?>,
    val coreFunctions: IntArray,
    val memories: IntArray,
    val reallocs: IntArray,
    val postReturns: IntArray,
    val resourceTypes: IntArray,
    val hostFunctions: Array<RuntimeComponentHostFunction?>,
    val states: ComponentInstanceStates,
    val adapterInstance: ModuleInstance = ModuleInstance(emptyList()),
    var deallocated: Boolean = false,
) {
    companion object {
        fun allocate(
            counts: ComponentInstanceCounts,
            componentInstanceParents: IntArray = IntArray(counts.componentInstances) { UNINITIALIZED_RUNTIME_SLOT },
        ): ComponentRuntimeState {
            require(componentInstanceParents.size == counts.componentInstances)
            return ComponentRuntimeState(
                coreInstances = arrayOfNulls(counts.coreInstances),
                coreFunctions = runtimeSlots(counts.coreFunctions),
                memories = runtimeSlots(counts.memories),
                reallocs = runtimeSlots(counts.reallocs),
                postReturns = runtimeSlots(counts.postReturns),
                resourceTypes = runtimeSlots(counts.resourceTypes),
                hostFunctions = arrayOfNulls(counts.hostFunctions),
                states = ComponentInstanceStates.allocate(componentInstanceParents),
            )
        }
    }
}

private fun runtimeSlots(size: Int): IntArray = IntArray(size) { UNINITIALIZED_RUNTIME_SLOT }

private const val UNINITIALIZED_RUNTIME_SLOT = -1
