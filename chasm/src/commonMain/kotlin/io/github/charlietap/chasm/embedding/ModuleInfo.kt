@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes._declarations
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

fun moduleInfo(
    module: Module,
): ModuleInfo = declarationsModuleInfo(module._declarations())
