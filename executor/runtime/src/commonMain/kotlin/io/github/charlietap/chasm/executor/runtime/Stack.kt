package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.jvm.JvmInline
import io.github.charlietap.chasm.stack.Stack as InternalStack

data class Stack(
    private val frames: InternalStack<Entry.ActivationFrame> = InternalStack(INITIAL_CAPACITY),
    private val instructions: InternalStack<Entry.Instruction> = InternalStack(INITIAL_CAPACITY),
    private val labels: InternalStack<Entry.Label> = InternalStack(INITIAL_CAPACITY),
    private val values: InternalStack<Entry.Value> = InternalStack(INITIAL_CAPACITY),
) {
    constructor(
        frames: List<Entry.ActivationFrame>,
        instructions: List<Entry.Instruction>,
        labels: List<Entry.Label>,
        values: List<Entry.Value>,
    ) : this() {
        frames.forEach { frame ->
            this.frames.push(frame)
        }
        instructions.forEach { instruction ->
            this.instructions.push(instruction)
        }
        labels.forEach { label ->
            this.labels.push(label)
        }
        values.forEach { value ->
            this.values.push(value)
        }
    }

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
        frames.push(frame)
        instructions.push(Entry.Instruction(AdminInstruction.Frame(frame)))
    }

    fun push(handler: Entry.ExceptionHandler) {
        instructions.push(Entry.Instruction(AdminInstruction.Handler(handler)))
    }

    fun push(instruction: Entry.Instruction) = instructions.push(instruction)

    fun push(label: Entry.Label) {
        labels.push(label)
        instructions.push(Entry.Instruction(AdminInstruction.Label(label)))
    }

    fun push(value: Entry.Value) = values.push(value)

    fun popFrameOrNull(): Entry.ActivationFrame? = frames.popOrNull()

    fun popInstructionOrNull(): Entry.Instruction? = instructions.popOrNull()

    fun popLabelOrNull(): Entry.Label? = labels.popOrNull()

    fun popValueOrNull(): Entry.Value? = values.popOrNull()

    fun peekFrameOrNull(): Entry.ActivationFrame? = frames.peekOrNull()

    fun peekInstructionOrNull(): Entry.Instruction? = instructions.peekOrNull()

    fun peekLabelOrNull(): Entry.Label? = labels.peekOrNull()

    fun peekValueOrNull(): Entry.Value? = values.peekOrNull()

    fun peekNthFrameOrNull(n: Int): Entry.ActivationFrame? = frames.peekNthOrNull(n)

    fun peekNthLabelOrNull(n: Int): Entry.Label? = labels.peekNthOrNull(n)

    fun peekNthValueOrNull(n: Int): Entry.Value? = values.peekNthOrNull(n)

    fun size() = frames.depth() + instructions.depth() + labels.depth() + values.depth()

    fun framesDepth() = frames.depth()

    fun instructionsDepth() = instructions.depth()

    fun labelsDepth() = labels.depth()

    fun valuesDepth() = values.depth()

    fun clear() {
        frames.clear()
        instructions.clear()
        labels.clear()
        values.clear()
    }

    fun clearInstructions() = instructions.clear()

    fun clearLabels() = labels.clear()

    fun clearFrames() = frames.clear()

    fun clearValues() = values.clear()

    fun frames() = frames.entries()

    fun instructions() = instructions.entries()

    fun labels() = labels.entries()

    fun values() = values.entries()

    fun fill(stack: Stack) {
        stack.frames.entries().forEach { entry ->
            push(entry)
        }
        stack.instructions.entries().forEach { entry ->
            push(entry)
        }
        stack.labels.entries().forEach { entry ->
            push(entry)
        }
        stack.values.entries().forEach { entry ->
            push(entry)
        }
    }

    sealed interface Entry {

        data class Value(val value: ExecutionValue) : Entry

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
                var locals: MutableList<ExecutionValue>,
                val module: ModuleInstance,
            )
        }

        @JvmInline
        value class Instruction(val instruction: ExecutionInstruction) : Entry

        @JvmInline
        value class ExceptionHandler(val handlers: List<ControlInstruction.CatchHandler>)
    }

    companion object {
        const val INITIAL_CAPACITY = 256
        const val MAX_DEPTH = 1028
    }
}
