package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ChasmError.ValidationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.validator.ParallelWasmModuleValidator

@InternalChasmApi
suspend fun _validate(
    module: Module,
    taskExecutor: ParallelTaskExecutor,
): ChasmResult<Module, ValidationError> {
    val result = ParallelWasmModuleValidator(
        config = module.config,
        module = module.module,
        taskExecutor = taskExecutor,
    )
    return validate(module, result)
}
