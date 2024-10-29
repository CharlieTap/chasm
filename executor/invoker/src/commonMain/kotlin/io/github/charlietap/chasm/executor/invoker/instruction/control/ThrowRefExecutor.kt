@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.exception
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushInstruction
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.tagAddress
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun ThrowRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRef,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val ref = stack.popReference().bind()

    val exceptionRef = if (ref is ReferenceValue.Null) {
        Err(InvocationError.UnexpectedReferenceValue).bind()
    } else {
        ref as ReferenceValue.Exception
    }

    val instance = store.exception(exceptionRef.address).bind()
    val address = instance.tagAddress

    val handler = jumpToHandlerInstruction(stack).bind()

    if (handler.handlers.isEmpty()) {
        stack.pushValue(ReferenceValue.Exception(exceptionRef.address))
        stack.pushInstruction(ModuleInstruction(ControlInstruction.ThrowRef))
    } else {

        val frame = stack.peekFrame().bind()

        val catchHandler = handler.handlers.first()
        val otherHandlers = handler.handlers.drop(1)

        val tagMatches = when (catchHandler) {
            is ControlInstruction.CatchHandler.Catch -> {
                address == frame.state.module.tagAddress(catchHandler.tagIndex).bind()
            }
            is ControlInstruction.CatchHandler.CatchRef -> {
                address == frame.state.module.tagAddress(catchHandler.tagIndex).bind()
            }
            else -> false
        }

        when {
            catchHandler is ControlInstruction.CatchHandler.Catch && tagMatches -> {
                instance.fields.asReversed().forEach { field ->
                    stack.pushValue(field)
                }
                stack.pushInstruction(ModuleInstruction(ControlInstruction.Br(catchHandler.labelIndex)))
            }
            catchHandler is ControlInstruction.CatchHandler.CatchRef && tagMatches -> {
                instance.fields.asReversed().forEach { field ->
                    stack.pushValue(field)
                }
                stack.pushValue(exceptionRef)
                stack.pushInstruction(ModuleInstruction(ControlInstruction.Br(catchHandler.labelIndex)))
            }
            catchHandler is ControlInstruction.CatchHandler.CatchAll -> {
                stack.pushInstruction(ModuleInstruction(ControlInstruction.Br(catchHandler.labelIndex)))
            }
            catchHandler is ControlInstruction.CatchHandler.CatchAllRef -> {
                stack.pushValue(exceptionRef)
                stack.pushInstruction(ModuleInstruction(ControlInstruction.Br(catchHandler.labelIndex)))
            }
            else -> {
                stack.push(Stack.Entry.ExceptionHandler(otherHandlers))
                stack.pushValue(exceptionRef)
                stack.pushInstruction(ModuleInstruction(ControlInstruction.ThrowRef))
            }
        }
    }
}

private fun jumpToHandlerInstruction(stack: Stack): Result<Stack.Entry.ExceptionHandler, InvocationError> = binding {

    var instruction: ExecutionInstruction?
    do {
        instruction = stack.popInstructionOrNull()?.instruction
        when (instruction) {
            is AdminInstruction.Label -> stack.popLabel().bind()
            is AdminInstruction.Frame -> stack.popFrame().bind()
            else -> Unit
        }
    } while (instruction !is AdminInstruction.Handler && instruction != null)

    if (instruction is AdminInstruction.Handler) {
        instruction.handler
    } else {
        Err(InvocationError.UncaughtException).bind()
    }
}
