package io.github.charlietap.chasm.runtime.component.info

import io.github.charlietap.chasm.runtime.component.canonical.CanonicalCallPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentTypes

class ComponentRuntimeInfo(
    val types: ComponentTypes,
    val exports: List<PreparedComponentExport> = emptyList(),
    val functions: List<PreparedComponentFunction> = emptyList(),
    val linearMemoryLayouts: List<LinearMemoryLayout> = emptyList(),
    val callPlans: List<CanonicalCallPlan> = emptyList(),
    val resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeIndex> = emptyMap(),
)
