package io.github.charlietap.chasm.fixture.runtime.component.info

import io.github.charlietap.chasm.fixture.type.component.componentTypes
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalCallPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentTypes

fun componentRuntimeInfo(
    types: ComponentTypes = componentTypes(),
    exports: List<PreparedComponentExport> = emptyList(),
    functions: List<PreparedComponentFunction> = emptyList(),
    linearMemoryLayouts: List<LinearMemoryLayout> = emptyList(),
    callPlans: List<CanonicalCallPlan> = emptyList(),
    resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeIndex> = emptyMap(),
) = ComponentRuntimeInfo(
    types = types,
    exports = exports,
    functions = functions,
    linearMemoryLayouts = linearMemoryLayouts,
    callPlans = callPlans,
    resourceTypes = resourceTypes,
)
