package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.ast.module.Module as InternalModule

fun publicModule(module: InternalModule = module()) = Module(module)
