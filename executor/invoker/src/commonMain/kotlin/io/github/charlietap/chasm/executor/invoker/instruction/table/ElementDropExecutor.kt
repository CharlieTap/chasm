@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame

internal inline fun ElementDropExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.ElemDrop,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context

    val frame = stack.peekFrame().bind()
    val elementAddress = frame.state.module.elementAddress(instruction.elemIdx).bind()
    val elementInstance = store.element(elementAddress).bind()

    store.elements[elementAddress.address] = elementInstance.copy(
        elements = arrayOf(),
        dropped = true,
    )
}
