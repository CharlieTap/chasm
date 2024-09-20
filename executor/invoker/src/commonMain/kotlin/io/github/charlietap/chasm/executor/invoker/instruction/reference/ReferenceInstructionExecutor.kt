package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
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
    refNullExecutor: Executor<ReferenceInstruction.RefNull>,
    refIsNullExecutor: Executor<ReferenceInstruction.RefIsNull>,
    refFuncExecutor: Executor<ReferenceInstruction.RefFunc>,
    refAsNonNullExecutor: Executor<ReferenceInstruction.RefAsNonNull>,
    refEqExecutor: Executor<ReferenceInstruction.RefEq>,
    refTestExecutor: Executor<ReferenceInstruction.RefTest>,
    refCastExecutor: Executor<ReferenceInstruction.RefCast>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is ReferenceInstruction.RefNull -> refNullExecutor(context, instruction).bind()
        is ReferenceInstruction.RefIsNull -> refIsNullExecutor(context, instruction).bind()
        is ReferenceInstruction.RefFunc -> refFuncExecutor(context, instruction).bind()
        is ReferenceInstruction.RefAsNonNull -> refAsNonNullExecutor(context, instruction).bind()
        is ReferenceInstruction.RefEq -> refEqExecutor(context, instruction).bind()
        is ReferenceInstruction.RefTest -> refTestExecutor(context, instruction).bind()
        is ReferenceInstruction.RefCast -> refCastExecutor(context, instruction).bind()
    }
}
