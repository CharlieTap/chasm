package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.globalType

internal fun ConstInstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
): Result<Unit, ModuleValidatorError> =
    ConstInstructionValidator(
        context = context,
        instruction = instruction,
        instructionValidator = ::InstructionValidator,
    )

internal inline fun ConstInstructionValidator(
    context: ValidationContext,
    instruction: Instruction,
    crossinline instructionValidator: Validator<Instruction>,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        is AggregateInstruction.AnyConvertExtern,
        is AggregateInstruction.ArrayNew,
        is AggregateInstruction.ArrayNewDefault,
        is AggregateInstruction.ArrayNewFixed,
        is AggregateInstruction.ExternConvertAny,
        is AggregateInstruction.RefI31,
        is AggregateInstruction.StructNew,
        is AggregateInstruction.StructNewDefault,
        is NumericInstruction.I32Const,
        is NumericInstruction.I64Const,
        is NumericInstruction.F32Const,
        is NumericInstruction.F64Const,
        is NumericInstruction.I32Add,
        is NumericInstruction.I32Sub,
        is NumericInstruction.I32Mul,
        is NumericInstruction.I64Add,
        is NumericInstruction.I64Sub,
        is NumericInstruction.I64Mul,
        is ReferenceInstruction.RefNull,
        is ReferenceInstruction.RefFunc,
        is VectorInstruction.V128Const,
        -> instructionValidator(context, instruction).bind()
        is VariableInstruction.GlobalGet -> {
            val globalType = context.globalType(instruction.globalIdx).bind()
            if (globalType.mutability == Mutability.Var) {
                Err(InstructionValidatorError.ConstInstructionExpected).bind<Unit>()
            }
            instructionValidator(context, instruction).bind()
        }
        else -> Err(InstructionValidatorError.ConstInstructionExpected).bind<Unit>()
    }
}
