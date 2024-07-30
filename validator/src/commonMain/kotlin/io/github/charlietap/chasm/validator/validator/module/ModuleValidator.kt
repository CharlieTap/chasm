package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.data.DataSegmentValidator
import io.github.charlietap.chasm.validator.validator.element.ElementSegmentValidator
import io.github.charlietap.chasm.validator.validator.export.ExportValidator
import io.github.charlietap.chasm.validator.validator.function.FunctionValidator
import io.github.charlietap.chasm.validator.validator.global.GlobalValidator
import io.github.charlietap.chasm.validator.validator.import.ImportValidator
import io.github.charlietap.chasm.validator.validator.memory.MemoryValidator
import io.github.charlietap.chasm.validator.validator.start.StartFunctionValidator
import io.github.charlietap.chasm.validator.validator.table.TableValidator

internal fun ModuleValidator(
    context: ValidationContext,
    module: Module,
): Result<Unit, ModuleValidatorError> =
    ModuleValidator(
        context = context,
        module = module,
        functionValidator = ::FunctionValidator,
        importValidator = ::ImportValidator,
        exportValidator = ::ExportValidator,
        globalValidator = ::GlobalValidator,
        dataSegmentValidator = ::DataSegmentValidator,
        elementSegmentValidator = ::ElementSegmentValidator,
        memoryValidator = ::MemoryValidator,
        startFunctionValidator = ::StartFunctionValidator,
        tableValidator = ::TableValidator,
    )

internal fun ModuleValidator(
    context: ValidationContext,
    module: Module,
    functionValidator: Validator<Function>,
    importValidator: Validator<Import>,
    exportValidator: Validator<Export>,
    globalValidator: Validator<Global>,
    dataSegmentValidator: Validator<DataSegment>,
    elementSegmentValidator: Validator<ElementSegment>,
    memoryValidator: Validator<Memory>,
    startFunctionValidator: Validator<StartFunction>,
    tableValidator: Validator<Table>,
): Result<Unit, ModuleValidatorError> = binding {

    module.apply {
        functions.forEach { function ->
            functionValidator(context, function).bind()
        }
        imports.forEach { import ->
            importValidator(context, import).bind()
        }
        exports.forEach { export ->
            exportValidator(context, export).bind()
        }
        globals.forEach { global ->
            globalValidator(context, global).bind()
        }
        dataSegments.forEach { dataSegment ->
            dataSegmentValidator(context, dataSegment).bind()
        }
        elementSegments.forEach { segment ->
            elementSegmentValidator(context, segment).bind()
        }
        memories.forEach { memory ->
            memoryValidator(context, memory).bind()
        }
        tables.forEach { table ->
            tableValidator(context, table).bind()
        }
        startFunction?.let { function ->
            startFunctionValidator(context, function).bind()
        }
    }
}
