package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.ast.module.Module as InternalModule

fun publicModule(
    config: ModuleConfig = moduleConfig(),
    module: InternalModule = module(),
) = Module(
    config = config,
    module = module,
)
