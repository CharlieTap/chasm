package io.github.charlietap.chasm.validator.validator.component.core.module

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.component.CoreModule
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.type.component.CoreModuleType
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.context.component.ComponentValidationContext
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import io.github.charlietap.chasm.validator.validator.module.ModuleValidator

internal fun CoreModuleValidator(
    context: ComponentValidationContext,
    module: CoreModule,
): Result<Unit, ComponentValidatorError> = CoreModuleValidator(
    context = context,
    module = module,
    moduleValidator = ::ModuleValidator,
    moduleTypeDeriver = ::CoreModuleTypeDeriver,
)

internal inline fun CoreModuleValidator(
    context: ComponentValidationContext,
    module: CoreModule,
    crossinline moduleValidator: ModuleValidator<Module>,
    crossinline moduleTypeDeriver: (ModuleValidationContext, Module) -> Result<CoreModuleType, ComponentValidatorError>,
): Result<Unit, ComponentValidatorError> {
    val astModule = module.module
    val moduleContext = context.moduleContext(astModule)
    val result: Result<Unit, ComponentValidatorError> = binding {
        moduleValidator(moduleContext, astModule)
            .mapError(ComponentValidatorError::EmbeddedModule)
            .bind()
        val moduleType = moduleTypeDeriver(moduleContext, astModule).bind()
        context.frame.coreModules += moduleType
    }
    moduleContext.clearLocalState()
    return result
}
