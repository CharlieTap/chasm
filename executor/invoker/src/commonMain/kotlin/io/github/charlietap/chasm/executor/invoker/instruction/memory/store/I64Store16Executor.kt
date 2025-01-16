package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I64ToI16Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I64Store16Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16,
) =
    I64Store16Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI16Writer,
    )

internal inline fun I64Store16Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI16Writer,
) {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popI64()

    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset.toInt()

    boundsChecker(effectiveAddress, 2, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
