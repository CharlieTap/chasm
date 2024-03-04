package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmInline

data class Stack(
    private val frames: ArrayDeque<Entry.ActivationFrame> = ArrayDeque(INITIAL_CAPACITY),
    private val labels: ArrayDeque<Entry.Label> = ArrayDeque(INITIAL_CAPACITY),
    private val values: ArrayDeque<Entry.Value> = ArrayDeque(INITIAL_CAPACITY),
) {

    constructor(
        entries: Sequence<Entry>,
    ) : this() {
        entries.forEach { entry ->
            when (entry) {
                is Entry.ActivationFrame -> push(entry)
                is Entry.Label -> push(entry)
                is Entry.Value -> push(entry)
            }
        }
    }

    fun push(frame: Entry.ActivationFrame) = frames.addLast(frame)

    fun push(label: Entry.Label) = labels.addLast(label)

    fun push(value: Entry.Value) = values.addLast(value)

    fun popFrame(): Entry.ActivationFrame? = frames.removeLastOrNull()

    fun popLabel(): Entry.Label? = labels.removeLastOrNull()

    fun popValue(): Entry.Value? = values.removeLastOrNull()

    fun peekFrame(): Entry.ActivationFrame? = frames.lastOrNull()

    fun peekLabel(): Entry.Label? = labels.lastOrNull()

    fun peekValue(): Entry.Value? = values.lastOrNull()

    fun size() = frames.size + labels.size + values.size

    fun empty() {
        frames.removeAll { true }
        labels.removeAll { true }
        values.removeAll { true }
    }

    fun fill(stack: Stack) {
        stack.frames.forEach { entry ->
            push(entry)
        }
        stack.labels.forEach { entry ->
            push(entry)
        }
        stack.values.forEach { entry ->
            push(entry)
        }
    }

    sealed interface Entry {
        @JvmInline
        value class Value(val value: ExecutionValue) : Entry

        data class Label(
            val arity: Arity,
            val continuation: List<Instruction>,
        ) : Entry

        data class ActivationFrame(
            val arity: Arity,
            val state: State,
        ) : Entry {
            data class State(
                val locals: MutableList<ExecutionValue>,
                val module: ModuleInstance,
            )
        }
    }

    companion object {
        private const val INITIAL_CAPACITY = 256
    }
}
