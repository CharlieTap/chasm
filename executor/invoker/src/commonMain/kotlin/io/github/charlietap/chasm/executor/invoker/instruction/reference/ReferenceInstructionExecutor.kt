package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun ReferenceInstructionExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction,
): Result<Unit, InvocationError> =
    ReferenceInstructionExecutor(
        context = context,
        instruction = instruction,
        refNullExecutor = ::RefNullExecutor,
        refIsNullExecutor = ::RefIsNullExecutor,
        refFuncExecutor = ::RefFuncExecutor,
        refAsNonNullExecutor = ::RefAsNonNullExecutor,
        refEqExecutor = ::RefEqExecutor,
        refTestExecutor = ::RefTestExecutor,
        refCastExecutor = ::RefCastExecutor,
    )

internal fun ReferenceInstructionExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction,
    refNullExecutor: RefNullExecutor,
    refIsNullExecutor: RefIsNullExecutor,
    refFuncExecutor: RefFuncExecutor,
    refAsNonNullExecutor: RefAsNonNullExecutor,
    refEqExecutor: RefEqExecutor,
    refTestExecutor: RefTestExecutor,
    refCastExecutor: RefCastExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    when (instruction) {
        is ReferenceInstruction.RefNull -> refNullExecutor(stack, instruction).bind()
        is ReferenceInstruction.RefIsNull -> refIsNullExecutor(stack).bind()
        is ReferenceInstruction.RefFunc -> refFuncExecutor(stack, instruction).bind()
        is ReferenceInstruction.RefAsNonNull -> refAsNonNullExecutor(stack).bind()
        is ReferenceInstruction.RefEq -> refEqExecutor(stack).bind()
        is ReferenceInstruction.RefTest -> refTestExecutor(store, stack, instruction.referenceType).bind()
        is ReferenceInstruction.RefCast -> refCastExecutor(store, stack, instruction.referenceType).bind()
    }
}
