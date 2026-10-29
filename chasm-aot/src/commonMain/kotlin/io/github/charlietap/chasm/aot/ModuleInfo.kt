package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

fun moduleInfo(
    module: CompiledModule,
): ChasmResult<ModuleInfo, ChasmError.ExecutionError> =
    module.compatibleDeclarations().map(::declarationsModuleInfo)
