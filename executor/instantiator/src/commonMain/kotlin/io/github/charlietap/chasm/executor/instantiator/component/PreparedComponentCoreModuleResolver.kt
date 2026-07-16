package io.github.charlietap.chasm.executor.instantiator.component

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.linking.ResolvedComponentImports
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentCoreModule

typealias PreparedComponentCoreModuleResolver = (
    PreparedComponent,
    ResolvedComponentImports,
    PreparedComponentCoreModule,
) -> CompiledModule

fun PreparedComponentCoreModuleResolver(
    component: PreparedComponent,
    imports: ResolvedComponentImports,
    module: PreparedComponentCoreModule,
): CompiledModule = when (module) {
    is PreparedComponentCoreModule.Embedded -> component.modules[module.moduleIndex]
    is PreparedComponentCoreModule.Import -> imports.coreModules[module.importIndex].module
}
