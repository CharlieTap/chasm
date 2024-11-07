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
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.module.Type
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
import io.github.charlietap.chasm.validator.validator.tag.TagValidator
import io.github.charlietap.chasm.validator.validator.type.TypeValidator

internal fun ModuleValidator(
    context: ValidationContext,
    module: Module,
): Result<Unit, ModuleValidatorError> =
    ModuleValidator(
        context = context,
        module = module,
        typeValidator = ::TypeValidator,
        functionValidator = ::FunctionValidator,
        importValidator = ::ImportValidator,
        exportValidator = ::ExportValidator,
        globalValidator = ::GlobalValidator,
        dataSegmentValidator = ::DataSegmentValidator,
        elementSegmentValidator = ::ElementSegmentValidator,
        memoryValidator = ::MemoryValidator,
        startFunctionValidator = ::StartFunctionValidator,
        tableValidator = ::TableValidator,
        tagValidator = ::TagValidator,
    )

internal inline fun ModuleValidator(
    context: ValidationContext,
    module: Module,
    crossinline typeValidator: Validator<Type>,
    crossinline functionValidator: Validator<Function>,
    crossinline importValidator: Validator<Import>,
    crossinline exportValidator: Validator<Export>,
    crossinline globalValidator: Validator<Global>,
    crossinline dataSegmentValidator: Validator<DataSegment>,
    crossinline elementSegmentValidator: Validator<ElementSegment>,
    crossinline memoryValidator: Validator<Memory>,
    crossinline startFunctionValidator: Validator<StartFunction>,
    crossinline tableValidator: Validator<Table>,
    crossinline tagValidator: Validator<Tag>,
): Result<Unit, ModuleValidatorError> = binding {
    module.apply {
        types.forEach { type ->
            typeValidator(context, type).bind()
        }
        imports.forEach { import ->
            importValidator(context, import).bind()
        }
        functions.forEach { function ->
            functionValidator(context, function).bind()
        }
        tables.forEach { table ->
            tableValidator(context, table).bind()
        }
        memories.forEach { memory ->
            memoryValidator(context, memory).bind()
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
        exports.forEach { export ->
            exportValidator(context, export).bind()
        }
        tags.forEach { tag ->
            tagValidator(context, tag).bind()
        }
        startFunction?.let { function ->
            startFunctionValidator(context, function).bind()
        }
    }
}
