package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.FrameStack
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.stack.Stack as InternalStack

data class Stack(
    private val frames: FrameStack = FrameStack(),
    private val handlers: InternalStack<ExceptionHandler> = InternalStack(INITIAL_CAPACITY),
    private val instructions: InternalStack<DispatchableInstruction> = InternalStack(INITIAL_CAPACITY),
    private val labels: InternalStack<Entry.Label> = InternalStack(INITIAL_CAPACITY),
    private val values: InternalStack<Entry.Value> = InternalStack(INITIAL_CAPACITY),
) {
    constructor(
        frames: List<ActivationFrame>,
        handlers: List<ExceptionHandler>,
        instructions: List<DispatchableInstruction>,
        labels: List<Entry.Label>,
        values: List<Entry.Value>,
    ) : this() {
        frames.forEach { frame ->
            this.frames.push(frame)
        }
        handlers.forEach { handler ->
            this.handlers.push(handler)
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
                is Entry.Label -> push(entry)
                is Entry.Value -> push(entry)
            }
        }
    }

    fun depths(): StackDepths = FrameStackDepths(
        handlers = handlersDepth(),
        instructions = instructionsDepth(),
        labels = labelsDepth(),
        values = valuesDepth(),
    )

    fun push(frame: ActivationFrame) = frames.push(frame)

    fun push(handler: ExceptionHandler) = handlers.push(handler)

    fun push(instruction: DispatchableInstruction) = instructions.push(instruction)

    fun push(label: Entry.Label) = labels.push(label)

    fun push(value: Entry.Value) = values.push(value)

    fun push(many: Array<DispatchableInstruction>) = instructions.pushAll(many)

    fun popFrameOrNull(): ActivationFrame? = frames.popOrNull()

    fun popHandlerOrNull(): ExceptionHandler? = handlers.popOrNull()

    fun popInstructionOrNull(): DispatchableInstruction? = instructions.popOrNull()

    fun popLabelOrNull(): Entry.Label? = labels.popOrNull()

    fun popValueOrNull(): Entry.Value? = values.popOrNull()

    fun peekFrameOrNull(): ActivationFrame? = frames.peekOrNull()

    fun peekInstructionOrNull(): DispatchableInstruction? = instructions.peekOrNull()

    fun peekLabelOrNull(): Entry.Label? = labels.peekOrNull()

    fun peekValueOrNull(): Entry.Value? = values.peekOrNull()

    fun peekNthFrameOrNull(n: Int): ActivationFrame? = frames.peekNthOrNull(n)

    fun peekNthLabelOrNull(n: Int): Entry.Label? = labels.peekNthOrNull(n)

    fun peekNthValueOrNull(n: Int): Entry.Value? = values.peekNthOrNull(n)

    fun shrinkFrames(
        preserveTopN: Int,
        depth: Int,
    ) = frames.shrink(preserveTopN, depth)

    fun shrinkHandlers(
        preserveTopN: Int,
        depth: Int,
    ) = handlers.shrink(preserveTopN, depth)

    fun shrinkInstructions(
        preserveTopN: Int,
        depth: Int,
    ) = instructions.shrink(preserveTopN, depth)

    fun shrinkLabels(
        preserveTopN: Int,
        depth: Int,
    ) = labels.shrink(preserveTopN, depth)

    fun shrinkValues(
        preserveTopN: Int,
        depth: Int,
    ) = values.shrink(preserveTopN, depth)

    fun size() = frames.depth() + instructions.depth() + labels.depth() + values.depth()

    fun framesDepth() = frames.depth()

    fun handlersDepth() = handlers.depth()

    fun instructionsDepth() = instructions.depth()

    fun labelsDepth() = labels.depth()

    fun valuesDepth() = values.depth()

    fun clear() {
        frames.clear()
        handlers.clear()
        instructions.clear()
        labels.clear()
        values.clear()
    }

    fun clearHandlers() = handlers.clear()

    fun clearInstructions() = instructions.clear()

    fun clearLabels() = labels.clear()

    fun clearFrames() = frames.clear()

    fun clearValues() = values.clear()

    fun frames() = frames.entries()

    fun handlers() = handlers.entries()

    fun instructions() = instructions.entries()

    fun labels() = labels.entries()

    fun values() = values.entries()

    fun fill(stack: Stack) {
        stack.frames.entries().forEach { entry ->
            push(entry)
        }
        stack.handlers.entries().forEach { entry ->
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
            val arity: Int,
            val depths: StackDepths,
            val continuation: DispatchableInstruction?,
        ) : Entry
    }

    companion object {
        const val INITIAL_CAPACITY = 256
        const val MAX_DEPTH = 1028
    }
}
