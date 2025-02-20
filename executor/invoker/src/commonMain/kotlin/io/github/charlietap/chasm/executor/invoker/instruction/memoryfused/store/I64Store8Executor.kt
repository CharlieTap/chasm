package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction

fun I64Store8Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8,
) =
    I64Store8Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI8Writer,
    )

internal inline fun I64Store8Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI8Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = instruction.value(stack)
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Byte.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
