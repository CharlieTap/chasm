package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

fun moduleInfo(
    module: Module,
): ModuleInfo = declarationsModuleInfo(module.declarations)
