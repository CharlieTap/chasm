package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.pushRef

internal fun RefFuncInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefFunc,
): Result<Unit, ModuleValidatorError> = binding {

    if (instruction.funcIdx !in context.refs) {
        Err(InstructionValidatorError.UnknownReference).bind<Unit>()
    }
    val functionType = context.functionType(instruction.funcIdx).bind()
    val concreteHeapType = ConcreteHeapType.Defined(functionType.definedType())

    context.pushRef(concreteHeapType)
}
