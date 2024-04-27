package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmInline

data class Stack(
    private val frames: ArrayDeque<Entry.ActivationFrame> = ArrayDeque(INITIAL_CAPACITY),
    private val instructions: ArrayDeque<Entry.Instruction> = ArrayDeque(INITIAL_CAPACITY),
    private val labels: ArrayDeque<Entry.Label> = ArrayDeque(INITIAL_CAPACITY),
    private val values: ArrayDeque<Entry.Value> = ArrayDeque(INITIAL_CAPACITY),
) {

    constructor(
        entries: Sequence<Entry>,
    ) : this() {
        entries.forEach { entry ->
            when (entry) {
                is Entry.ActivationFrame -> push(entry)
                is Entry.Instruction -> push(entry)
                is Entry.Label -> push(entry)
                is Entry.Value -> push(entry)
            }
        }
    }

    fun push(frame: Entry.ActivationFrame) {
        frames.addLast(frame)
        instructions.addLast(Entry.Instruction(AdminInstruction.Frame(frame)))
    }

    fun push(frame: Entry.Instruction) = instructions.addLast(frame)

    fun push(label: Entry.Label) {
        labels.addLast(label)
        instructions.addLast(Entry.Instruction(AdminInstruction.Label(label)))
    }

    fun push(value: Entry.Value) = values.addLast(value)

    fun popFrameOrNull(): Entry.ActivationFrame? = frames.removeLastOrNull()

    fun popInstructionOrNull(): Entry.Instruction? = instructions.removeLastOrNull()

    fun popLabelOrNull(): Entry.Label? = labels.removeLastOrNull()

    fun popValueOrNull(): Entry.Value? = values.removeLastOrNull()

    fun peekFrameOrNull(): Entry.ActivationFrame? = frames.lastOrNull()

    fun peekInstructionOrNull(): Entry.Instruction? = instructions.lastOrNull()

    fun peekLabelOrNull(): Entry.Label? = labels.lastOrNull()

    fun peekValueOrNull(): Entry.Value? = values.lastOrNull()

    fun peekNthFrameOrNull(n: Int): Entry.ActivationFrame? = frames.getOrNull(frames.lastIndex - n)

    fun peekNthLabelOrNull(n: Int): Entry.Label? = labels.getOrNull(labels.lastIndex - n)

    fun peekNthValueOrNull(n: Int): Entry.Value? = values.getOrNull(values.lastIndex - n)

    fun size() = frames.size + instructions.size + labels.size + values.size

    fun framesDepth() = frames.size

    fun instructionsDepth() = instructions.size

    fun labelsDepth() = labels.size

    fun valuesDepth() = values.size

    fun empty() {
        frames.removeAll { true }
        instructions.removeAll { true }
        labels.removeAll { true }
        values.removeAll { true }
    }

    fun frames() = frames

    fun instructions() = instructions

    fun labels() = labels

    fun values() = values

    fun fill(stack: Stack) {
        stack.frames.forEach { entry ->
            push(entry)
        }
        stack.instructions.forEach { entry ->
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
            val stackValuesDepth: Int,
            val continuation: List<ExecutionInstruction>,
        ) : Entry

        data class ActivationFrame(
            val arity: Arity.Return,
            val stackLabelsDepth: Int,
            val stackValuesDepth: Int,
            val state: State,
        ) : Entry {
            data class State(
                val locals: MutableList<ExecutionValue>,
                val module: ModuleInstance,
            )
        }

        @JvmInline
        value class Instruction(val instruction: ExecutionInstruction) : Entry
    }

    companion object {
        private const val INITIAL_CAPACITY = 256
    }
}
