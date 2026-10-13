package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.validator.context.ImmutableModuleValidationContext
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.context.scope.EnterFunctionScope
import io.github.charlietap.chasm.validator.context.scope.ExitFunctionScope
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.function.FinishFunction
import io.github.charlietap.chasm.validator.validator.instruction.FinishInstructionBlock
import io.github.charlietap.chasm.validator.validator.instruction.OpcodeInstructionValidator
import io.github.charlietap.chasm.validator.validator.module.ModulePreludeValidator
import io.github.charlietap.chasm.validator.validator.module.ModuleRemainderValidator
import io.github.charlietap.chasm.validator.validator.type.ValueTypeValidator

/**
 * Validates a module while it is still being decoded: [validatePrelude] once the sections
 * ahead of the code section are known, function bodies through [functionValidator] as they
 * are decoded, and [validateRemainder] when the rest has arrived.
 *
 * [prelude] is the module without function bodies or data segments; [dataCount] is the value
 * of its data count section, which stands in for the data segments.
 */
class IncrementalWasmModuleValidator(
    config: ModuleConfig,
    private val prelude: Module,
    dataCount: UInt?,
) {

    private val context = ModuleValidationContext(
        ImmutableModuleValidationContext(config, prelude, dataCount),
        0,
    )

    fun validatePrelude(): Result<Unit, ModuleValidatorError> = validating {
        ModulePreludeValidator(context, prelude)
    }

    /** Use one per thread, after [validatePrelude] has passed. */
    fun functionValidator(): IncrementalFunctionValidator = IncrementalFunctionValidator(
        ModuleValidationContext(context.immutableContext, context.definedTypesValidated),
    )

    fun validateRemainder(dataSegments: List<DataSegment>): Result<Unit, ModuleValidatorError> = validating {
        ModuleRemainderValidator(context, prelude, dataSegments)
    }
}

/** Validates a function one instruction at a time as it is decoded. Not thread safe. */
class IncrementalFunctionValidator internal constructor(
    private val context: ModuleValidationContext,
) {

    private var result: Result<Unit, ModuleValidatorError> = Ok(Unit)
    private var labelsDepth = 0

    fun begin(
        typeIndex: Index.TypeIndex,
        locals: List<Local>,
    ) {
        // abandons a function that was begun but never ended
        ExitFunctionScope(context, null)

        result = validating {
            binding {
                EnterFunctionScope(context, typeIndex, locals).bind()
                locals.forEach { local ->
                    ValueTypeValidator(context, local.type).bind()
                }
                labelsDepth = context.labels.depth()
            }
        }
    }

    fun instruction(
        opcode: Int,
        instruction: Instruction,
    ) {
        if (result.isErr) return

        result = validating {
            OpcodeInstructionValidator(context, opcode, instruction)
        }
    }

    fun end(): Result<Unit, ModuleValidatorError> {
        val result = result.andThen {
            validating {
                FinishInstructionBlock(context, labelsDepth).andThen {
                    FinishFunction(context)
                }
            }
        }
        ExitFunctionScope(context, null)

        return result
    }
}

private inline fun validating(
    block: () -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> = try {
    block()
} catch (exception: ModuleValidationException) {
    Err(exception.error)
}
