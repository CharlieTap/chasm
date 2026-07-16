package io.github.charlietap.chasm.executor.instantiator.component

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImport
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceCounts

class PreparedComponent internal constructor(
    internal val runtimeInfo: ComponentRuntimeInfo,
    internal val modules: List<CompiledModule>,
    internal val initializers: List<ComponentInitializer>,
    internal val imports: List<PreparedComponentImport>,
    internal val counts: ComponentInstanceCounts,
    internal val componentInstanceParents: IntArray,
)
