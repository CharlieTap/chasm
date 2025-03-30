package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ShrUDispatcher(
    instruction: FusedNumericInstruction.I64ShrU,
) = I64ShrUDispatcher(
    instruction = instruction,
    executor = ::I64ShrUExecutor,
)

internal inline fun I64ShrUDispatcher(
    instruction: FusedNumericInstruction.I64ShrU,
    crossinline executor: Executor<FusedNumericInstruction.I64ShrU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
