package io.github.charlietap.chasm.runtime.component.instance

import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.resource.CanonicalHandleTable

class ComponentInstanceStates(
    val parents: IntArray,
    val mayLeave: BooleanArray,
    val mayEnter: BooleanArray,
    val poisoned: BooleanArray,
    val handleTables: Array<CanonicalHandleTable?>,
) {
    init {
        require(mayLeave.size == parents.size)
        require(mayEnter.size == parents.size)
        require(poisoned.size == parents.size)
        require(handleTables.size == parents.size)
    }

    fun handleTable(instance: RuntimeComponentInstanceIndex): CanonicalHandleTable =
        handleTables[instance.index] ?: CanonicalHandleTable().also { table ->
            handleTables[instance.index] = table
        }

    companion object {
        fun allocate(parents: IntArray): ComponentInstanceStates = ComponentInstanceStates(
            parents = parents.copyOf(),
            mayLeave = BooleanArray(parents.size) { true },
            mayEnter = BooleanArray(parents.size) { true },
            poisoned = BooleanArray(parents.size),
            handleTables = arrayOfNulls(parents.size),
        )
    }
}
