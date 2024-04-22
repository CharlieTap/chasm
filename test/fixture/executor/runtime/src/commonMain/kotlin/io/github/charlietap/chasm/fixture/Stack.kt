package io.github.charlietap.chasm.fixture

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.value.executionValue

fun stack(
    entries: Sequence<Stack.Entry> = sequenceOf(),
) = Stack(
    entries = entries,
)

fun frameState(
    locals: MutableList<ExecutionValue> = mutableListOf(),
    moduleInstance: ModuleInstance = moduleInstance(),
) = Stack.Entry.ActivationFrame.State(
    locals = locals,
    module = moduleInstance,
)

fun frame(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    state: Stack.Entry.ActivationFrame.State = frameState(),
) = Stack.Entry.ActivationFrame(
    arity = arity,
    state = state,
)

fun label(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    continuation: List<Instruction> = emptyList(),
) = Stack.Entry.Label(
    arity = arity,
    continuation = continuation,
)

fun value(
    value: ExecutionValue = executionValue(),
) = Stack.Entry.Value(
    value = value,
)
