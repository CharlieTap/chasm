package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.module.Module

internal fun passContext(
    config: RuntimeConfig = runtimeConfig(),
    module: Module = module(),
) = PassContext(
    config = config,
    module = module,
)
