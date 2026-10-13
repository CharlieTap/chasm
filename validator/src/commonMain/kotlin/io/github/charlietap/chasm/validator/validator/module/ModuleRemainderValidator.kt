package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.data.DataSegmentValidator
import io.github.charlietap.chasm.validator.validator.element.ElementSegmentValidator
import io.github.charlietap.chasm.validator.validator.export.ExportValidator
import io.github.charlietap.chasm.validator.validator.global.GlobalValidator
import io.github.charlietap.chasm.validator.validator.memory.MemoryValidator
import io.github.charlietap.chasm.validator.validator.start.StartFunctionValidator
import io.github.charlietap.chasm.validator.validator.table.TableValidator
import io.github.charlietap.chasm.validator.validator.tag.TagValidator

/**
 * Validates everything that is neither a type, an import nor a function. [dataSegments] are
 * passed separately because [module] may not have them yet.
 */
internal fun ModuleRemainderValidator(
    context: ModuleValidationContext,
    module: Module,
    dataSegments: List<DataSegment>,
): Result<Unit, ModuleValidatorError> = binding {
    validateModuleSection(context, module.tables, ::TableValidator).bind()
    validateModuleSection(context, module.memories, ::MemoryValidator).bind()
    validateModuleSection(context, module.globals, ::GlobalValidator).bind()
    validateModuleSection(context, dataSegments, ::DataSegmentValidator).bind()
    validateModuleSection(context, module.elementSegments, ::ElementSegmentValidator).bind()
    validateModuleSection(context, module.exports, ::ExportValidator).bind()
    validateModuleSection(context, module.tags, ::TagValidator).bind()
    module.startFunction?.let { function ->
        StartFunctionValidator(context, function).bind()
    }
}
