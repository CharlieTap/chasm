package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.FrameStack
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.InstructionStack
import io.github.charlietap.chasm.executor.runtime.stack.LabelStack
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.stack.Stack as InternalStack

data class Stack(
    private val frames: FrameStack = FrameStack(),
    private val handlers: InternalStack<ExceptionHandler> = InternalStack(INITIAL_CAPACITY),
    private val instructions: InstructionStack = InstructionStack(INITIAL_CAPACITY),
    private val labels: LabelStack = LabelStack(INITIAL_CAPACITY),
    private val values: ValueStack = ValueStack(INITIAL_CAPACITY),
) {
    constructor(
        frames: List<ActivationFrame>,
        handlers: List<ExceptionHandler>,
        instructions: List<DispatchableInstruction>,
        labels: List<Entry.Label>,
        values: List<ExecutionValue>,
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

    fun push(value: ExecutionValue) = values.push(value)

    fun push(many: Array<DispatchableInstruction>) = instructions.pushAll(many)

    fun popFrame(): ActivationFrame = frames.pop()

    fun popHandlerOrNull(): ExceptionHandler? = handlers.popOrNull()

    fun popInstruction(): DispatchableInstruction = instructions.pop()

    fun popLabel(): Entry.Label? = labels.pop()

    fun popValue(): ExecutionValue = values.pop()

    fun peekFrame(): ActivationFrame = frames.peek()

    fun peekInstructionOrNull(): DispatchableInstruction? = instructions.peekOrNull()

    fun peekValue(): ExecutionValue = values.peek()

    fun peekNthFrameOrNull(n: Int): ActivationFrame? = frames.peekNth(n)

    fun peekNthLabel(n: Int): Entry.Label = labels.peekNth(n)

    fun peekNthValue(n: Int): ExecutionValue = values.peekNth(n)

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

    fun frames(): List<ActivationFrame> = frames.entries()

    fun handlers(): List<ExceptionHandler> = handlers.entries()

    fun instructions(): List<DispatchableInstruction> = instructions.entries()

    fun labels(): List<Entry.Label> = labels.entries()

    fun values(): List<ExecutionValue> = values

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
