@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.ext.pushFrame
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias WasmFunctionCall = (Store, Stack, FunctionInstance.WasmFunction) -> Result<Unit, InvocationError>

internal inline fun WasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
): Result<Unit, InvocationError> =
    WasmFunctionCall(
        store = store,
        stack = stack,
        instance = instance,
        instructionBlockExecutor = ::InstructionBlockExecutor,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun WasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val type = instance.functionType().bind()
    val params = type.params.types.size
    val results = type.results.types.size

    val locals = MutableList<ExecutionValue>(params + instance.function.locals.size) { ExecutionValue.Uninitialised }
    for (i in (params - 1) downTo 0) {
        locals[i] = stack.popValue().bind().value
    }
    var idx = params
    for (local in instance.function.locals) {
        locals[idx++] = local.type.default().bind()
    }

    val frame = Stack.Entry.ActivationFrame(
        arity = Arity.Return(results),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth(),
        state = Stack.Entry.ActivationFrame.State(
            locals = locals.toMutableList(),
            module = instance.module,
        ),
    )

    stack.pushFrame(frame).bind()

    val label = Stack.Entry.Label(
        arity = Arity.Return(results),
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions.map(::ModuleInstruction), emptyList(), null).bind()
}
