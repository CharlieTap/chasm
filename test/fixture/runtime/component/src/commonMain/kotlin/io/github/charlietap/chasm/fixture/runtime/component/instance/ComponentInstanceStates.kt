package io.github.charlietap.chasm.fixture.runtime.component.instance

import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceStates
import io.github.charlietap.chasm.runtime.component.resource.CanonicalHandleTable

fun componentInstanceStates(
    parents: IntArray = intArrayOf(),
    mayLeave: BooleanArray = BooleanArray(parents.size) { true },
    mayEnter: BooleanArray = BooleanArray(parents.size) { true },
    poisoned: BooleanArray = BooleanArray(parents.size),
    handleTables: Array<CanonicalHandleTable?> = arrayOfNulls(parents.size),
) = ComponentInstanceStates(
    parents = parents,
    mayLeave = mayLeave,
    mayEnter = mayEnter,
    poisoned = poisoned,
    handleTables = handleTables,
)
