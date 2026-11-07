@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

@ExperimentalChasmApi
fun moduleInfo(
    module: CompiledModule,
): ChasmResult<ModuleInfo, ChasmError.ExecutionError> {
    val declarations = module.declarations.getOrElse { failure ->
        return Error(ChasmError.ExecutionError("Compiled module rejected: ${failure.message ?: failure.toString()}"))
    }
    return Success(declarationsModuleInfo(declarations))
}
