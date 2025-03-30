package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64XorExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64XorDispatcher(
    instruction: FusedNumericInstruction.I64Xor,
) = I64XorDispatcher(
    instruction = instruction,
    executor = ::I64XorExecutor,
)

internal inline fun I64XorDispatcher(
    instruction: FusedNumericInstruction.I64Xor,
    crossinline executor: Executor<FusedNumericInstruction.I64Xor>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
