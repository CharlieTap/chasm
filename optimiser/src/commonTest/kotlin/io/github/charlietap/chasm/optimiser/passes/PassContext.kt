package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.module.Module

internal fun passContext(
    module: Module = module(),
) = PassContext(
    module = module,
)
