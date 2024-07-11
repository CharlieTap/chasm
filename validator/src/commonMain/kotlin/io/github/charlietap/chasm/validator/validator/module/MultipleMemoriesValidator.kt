@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.MemoryValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun MultipleMemoriesValidator(
    context: ValidationContext,
    module: Module,
): Result<Unit, ModuleValidatorError> = binding {

    var memories = context.module.memories.size

    module.imports.forEach { import ->
        when (import.descriptor) {
            is Import.Descriptor.Function -> Unit
            is Import.Descriptor.Global -> Unit
            is Import.Descriptor.Memory -> memories++
            is Import.Descriptor.Table -> Unit
        }
    }

    if (memories > 1) {
        Err(MemoryValidatorError.MultipleMemories).bind<Unit>()
    }
}
