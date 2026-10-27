package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.WorkCursor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.claimEach
import io.github.charlietap.chasm.parallel.executeWorkers
import io.github.charlietap.chasm.parallel.workersForBatches
import io.github.charlietap.chasm.validator.context.ImmutableModuleValidationContext
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.validator.function.FunctionValidator
import io.github.charlietap.chasm.validator.validator.module.ModulePreludeValidator
import io.github.charlietap.chasm.validator.validator.module.ModuleRemainderValidator

suspend fun ParallelWasmModuleValidator(
    config: ModuleConfig,
    module: Module,
    taskExecutor: ParallelTaskExecutor,
): Result<Module, ModuleValidatorError> =
    ParallelWasmModuleValidator(config, module, taskExecutor, ValidationMode.AUTO)

internal suspend fun ParallelWasmModuleValidator(
    config: ModuleConfig,
    module: Module,
    taskExecutor: ParallelTaskExecutor,
    mode: ValidationMode,
    availableProcessors: Int = availableParallelProcessors(),
    policy: ParallelismPolicy = validationParallelism(),
): Result<Module, ModuleValidatorError> {
    val selectedWorkers = selectValidationWorkers(module.functions, mode, policy, availableProcessors)
    if (selectedWorkers == 1) return WasmModuleValidator(config, module)

    val batches = batchFunctions(module.functions)
    val workerCount = if (mode == ValidationMode.AUTO) {
        workersForBatches(selectedWorkers, batches.count, policy)
    } else {
        selectedWorkers
    }
    if (workerCount == 1) return WasmModuleValidator(config, module)

    val context = ModuleValidationContext(config, module)
    return try {
        ModulePreludeValidator(context, module).getOrThrowValidation()

        val parallelContext = ParallelValidationContext(module.functions, batches, context.immutableContext, context.definedTypesValidated)
        taskExecutor.executeWorkers(workerCount) { validateFunctions(parallelContext) }
        parallelContext.failures.error?.let { error -> return Err(error) }

        ModuleRemainderValidator(context, module, module.dataSegments).getOrThrowValidation()
        Ok(module)
    } catch (exception: ModuleValidationException) {
        Err(exception.error)
    }
}

private class ParallelValidationContext(
    val functions: List<Function>,
    val batches: WorkBatches,
    val immutableContext: ImmutableModuleValidationContext,
    val definedTypesValidated: Int,
) {
    val cursor = WorkCursor(batches.count)
    val failures = EarliestFailure<ModuleValidatorError>()
}

private fun ParallelTaskScope.validateFunctions(parallelContext: ParallelValidationContext) {
    val context = ModuleValidationContext(parallelContext.immutableContext, parallelContext.definedTypesValidated)

    claimEach(parallelContext.cursor) { batch ->
        for (functionIndex in parallelContext.batches.start(batch) until parallelContext.batches.end(batch)) {
            if (parallelContext.failures.isBeyond(functionIndex)) continue

            validateFunction(context, parallelContext.functions[functionIndex]).getOrElse { error ->
                parallelContext.failures.report(functionIndex, error)
            }
        }
    }
}

private fun validateFunction(
    context: ModuleValidationContext,
    function: Function,
): Result<Unit, ModuleValidatorError> = try {
    FunctionValidator(context, function)
} catch (exception: ModuleValidationException) {
    Err(exception.error)
}
