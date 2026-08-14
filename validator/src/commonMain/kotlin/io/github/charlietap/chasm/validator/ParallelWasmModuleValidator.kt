package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.validator.context.ImmutableModuleValidationContext
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.validator.data.DataSegmentValidator
import io.github.charlietap.chasm.validator.validator.element.ElementSegmentValidator
import io.github.charlietap.chasm.validator.validator.export.ExportValidator
import io.github.charlietap.chasm.validator.validator.function.FunctionValidator
import io.github.charlietap.chasm.validator.validator.global.GlobalValidator
import io.github.charlietap.chasm.validator.validator.import.ImportValidator
import io.github.charlietap.chasm.validator.validator.memory.MemoryValidator
import io.github.charlietap.chasm.validator.validator.module.validateModuleSection
import io.github.charlietap.chasm.validator.validator.start.StartFunctionValidator
import io.github.charlietap.chasm.validator.validator.table.TableValidator
import io.github.charlietap.chasm.validator.validator.tag.TagValidator
import io.github.charlietap.chasm.validator.validator.type.TypeValidator

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
): Result<Module, ModuleValidatorError> {
    val strategy = selectValidationStrategy(module.functions, mode, availableProcessors)
    val assignments = when (strategy) {
        ValidationStrategy.Serial -> return WasmModuleValidator(config, module)
        is ValidationStrategy.Parallel -> strategy.assignments
    }

    val context = ModuleValidationContext(config, module)
    return try {
        validateModuleSection(context, module.types, ::TypeValidator).getOrThrowValidation()
        validateModuleSection(context, module.imports, ::ImportValidator).getOrThrowValidation()

        val failure = taskExecutor.validateFunctions(
            immutableContext = context.immutableContext,
            definedTypesValidated = context.definedTypesValidated,
            assignments = assignments,
        ).earliestFailure()
        if (failure != null) return Err(failure.error)

        validateModuleSection(context, module.tables, ::TableValidator).getOrThrowValidation()
        validateModuleSection(context, module.memories, ::MemoryValidator).getOrThrowValidation()
        validateModuleSection(context, module.globals, ::GlobalValidator).getOrThrowValidation()
        validateModuleSection(context, module.dataSegments, ::DataSegmentValidator).getOrThrowValidation()
        validateModuleSection(context, module.elementSegments, ::ElementSegmentValidator).getOrThrowValidation()
        validateModuleSection(context, module.exports, ::ExportValidator).getOrThrowValidation()
        validateModuleSection(context, module.tags, ::TagValidator).getOrThrowValidation()
        module.startFunction?.let { function ->
            StartFunctionValidator(context, function).getOrThrowValidation()
        }
        Ok(module)
    } catch (exception: ModuleValidationException) {
        Err(exception.error)
    }
}

private suspend fun ParallelTaskExecutor.validateFunctions(
    immutableContext: ImmutableModuleValidationContext,
    definedTypesValidated: Int,
    assignments: Array<IntArray>,
): List<ValidationTaskResult> {
    val tasks = List<ParallelTaskScope.() -> ValidationTaskResult>(assignments.size) { assignmentIndex ->
        {
            validateAssignedFunctions(
                immutableContext = immutableContext,
                definedTypesValidated = definedTypesValidated,
                assignment = assignments[assignmentIndex],
            )
        }
    }
    return execute(tasks)
}

private fun List<ValidationTaskResult>.earliestFailure(): ValidationTaskResult.Failure? {
    var earliest: ValidationTaskResult.Failure? = null
    for (result in this) {
        if (result is ValidationTaskResult.Failure &&
            (earliest == null || result.functionIndex < earliest.functionIndex)
        ) {
            earliest = result
        }
    }
    return earliest
}

private fun ParallelTaskScope.validateAssignedFunctions(
    immutableContext: ImmutableModuleValidationContext,
    definedTypesValidated: Int,
    assignment: IntArray,
): ValidationTaskResult {
    val context = ModuleValidationContext(immutableContext, definedTypesValidated)
    val functions = immutableContext.module.functions
    for (functionIndex in assignment) {
        ensureActive()
        val result = try {
            FunctionValidator(context, functions[functionIndex])
        } catch (exception: ModuleValidationException) {
            return ValidationTaskResult.Failure(functionIndex, exception.error)
        }
        result.getOrElse { error ->
            return ValidationTaskResult.Failure(functionIndex, error)
        }
    }
    return ValidationTaskResult.Success
}

private sealed interface ValidationTaskResult {
    data object Success : ValidationTaskResult

    class Failure(
        val functionIndex: Int,
        val error: ModuleValidatorError,
    ) : ValidationTaskResult
}
