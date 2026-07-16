package io.github.charlietap.chasm.executor.instantiator.component.fixture

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.PreparedComponent
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImport
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceCounts
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceCounts

internal fun preparedComponent(
    runtimeInfo: ComponentRuntimeInfo = componentRuntimeInfo(),
    modules: List<CompiledModule> = emptyList(),
    initializers: List<ComponentInitializer> = emptyList(),
    imports: List<PreparedComponentImport> = emptyList(),
    counts: ComponentInstanceCounts = componentInstanceCounts(),
    componentInstanceParents: IntArray = IntArray(counts.componentInstances) { -1 },
) = PreparedComponent(
    runtimeInfo = runtimeInfo,
    modules = modules,
    initializers = initializers,
    imports = imports,
    counts = counts,
    componentInstanceParents = componentInstanceParents,
)
