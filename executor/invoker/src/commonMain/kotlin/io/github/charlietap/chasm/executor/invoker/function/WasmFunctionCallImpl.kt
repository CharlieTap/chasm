@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.flow.ReturnException
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popFrameOrError
import io.github.charlietap.chasm.executor.runtime.ext.popLabelOrError
import io.github.charlietap.chasm.executor.runtime.ext.popValueOrError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun WasmFunctionCallImpl(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    tailRecursion: Boolean,
): Result<Unit, InvocationError> =
    WasmFunctionCallImpl(
        store = store,
        stack = stack,
        instance = instance,
        tailRecursion = tailRecursion,
        instructionBlockExecutor = ::InstructionBlockExecutorImpl,
    )

internal inline fun WasmFunctionCallImpl(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    tailRecursion: Boolean,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    if (tailRecursion) {
        stack.popFrameOrError().bind()
    }

    val type = instance.type

    val params = List(type.params.types.size) {
        stack.popValueOrError().bind().value
    }.asReversed()

    val locals = params + instance.function.locals.map { local ->
        local.type.default().bind()
    }

    val frame = Stack.Entry.ActivationFrame(
        arity = Arity(type.results.types.size),
        state = Stack.Entry.ActivationFrame.State(
            locals = locals.toMutableList(),
            module = instance.module,
        ),
    )

    stack.push(frame)

    val label = Stack.Entry.Label(
        arity = Arity(type.params.types.size),
        continuation = emptyList(),
    )

    val labelsDepth = stack.labelsDepth()
    val valuesDepth = stack.valuesDepth()

    try {
        instructionBlockExecutor(store, stack, label, instance.function.body.instructions, emptyList()).bind()
    } catch (exception: ReturnException) {
        while (stack.labelsDepth() > labelsDepth) {
            stack.popLabelOrError().bind()
        }
        while (stack.valuesDepth() > valuesDepth) {
            stack.popValueOrError().bind()
        }

        exception.results.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }
    }

    if (!tailRecursion) {
        stack.popFrameOrError().bind()
    }
}
