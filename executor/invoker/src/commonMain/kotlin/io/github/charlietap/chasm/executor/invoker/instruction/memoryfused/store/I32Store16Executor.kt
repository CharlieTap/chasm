package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun I32Store16Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16,
) =
    I32Store16Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I32ToI16Writer,
    )

internal inline fun I32Store16Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I32ToI16Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = instruction.value(stack).toInt()
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Short.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
