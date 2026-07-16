package io.github.charlietap.chasm.executor.instantiator.component.fixture

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.fixture.ir.module.module as irModule

internal fun compiledModule(
    module: Module = irModule(),
) = CompiledModule(module)
