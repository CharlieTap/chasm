package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.F32Writer
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popF32
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
): Result<Unit, InvocationError> =
    F32StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F32Writer,
    )

internal inline fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F32Writer,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popF32().bind()
    val baseAddress = stack.popI32().bind()
    val effectiveAddress = baseAddress + instruction.memArg.offset.toInt()

    boundsChecker(effectiveAddress, Float.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
