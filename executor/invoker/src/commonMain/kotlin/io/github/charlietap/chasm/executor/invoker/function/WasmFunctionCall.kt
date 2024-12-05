package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.functionType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias WasmFunctionCall = (Store, Stack, FunctionInstance.WasmFunction, Boolean) -> Result<Unit, InvocationError>

internal inline fun WasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    tailRecursion: Boolean,
): Result<Unit, InvocationError> =
    WasmFunctionCall(
        store = store,
        stack = stack,
        instance = instance,
        tailRecursion = tailRecursion,
        instructionBlockExecutor = ::InstructionBlockExecutor,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun WasmFunctionCall(
    store: Store,
    stack: Stack,
    instance: FunctionInstance.WasmFunction,
    tailRecursion: Boolean,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val type = instance.functionType().bind()

    if (tailRecursion) {
        val frame = stack.popFrame().bind()

        val results = List(type.params.types.size) {
            stack.popValue().bind()
        }

        do {
            val instruction = stack.popInstruction().bind()
        } while (instruction.instruction !is AdminInstruction.Frame)

        while (stack.labelsDepth() > frame.stackLabelsDepth) {
            stack.popLabel().bind()
        }

        while (stack.valuesDepth() > frame.stackValuesDepth) {
            stack.popValue().bind()
        }

        results.asReversed().forEach { value ->
            stack.push(value)
        }
    }

    val params = List(type.params.types.size) {
        stack.popValue().bind().value
    }.asReversed()

    val locals = params + instance.function.locals.map { local ->
        local.type.default().bind()
    }

    val frame = Stack.Entry.ActivationFrame(
        arity = Arity.Return(type.results.types.size),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth(),
        state = Stack.Entry.ActivationFrame.State(
            locals = locals.toMutableList(),
            module = instance.module,
        ),
    )

    stack.pushFrame(frame).bind()

    val label = Stack.Entry.Label(
        arity = Arity.Return(type.results.types.size),
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    instructionBlockExecutor(stack, label, instance.function.body.instructions.map(::ModuleInstruction), emptyList(), null).bind()
}
