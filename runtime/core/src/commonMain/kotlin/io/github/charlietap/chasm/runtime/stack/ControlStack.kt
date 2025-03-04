package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler

data class ControlStack(
    private val frames: FrameStack = FrameStack(),
    private val handlers: HandlerStack = HandlerStack(INITIAL_CAPACITY),
    private val instructions: InstructionStack = InstructionStack(INITIAL_CAPACITY),
    private val labels: LabelStack = LabelStack(INITIAL_CAPACITY),
) {
    constructor(
        frames: List<ActivationFrame>,
        handlers: List<ExceptionHandler>,
        instructions: List<DispatchableInstruction>,
        labels: List<Entry.Label>,
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

    fun push(frame: ActivationFrame) = frames.push(frame)

    fun push(handler: ExceptionHandler) = handlers.push(handler)

    fun push(instruction: DispatchableInstruction) = instructions.push(instruction)

    fun push(label: Entry.Label) = labels.push(label)

    fun push(many: Array<DispatchableInstruction>) = instructions.pushAll(many)

    fun popFrame(): ActivationFrame = frames.pop()

    fun popHandler(): ExceptionHandler = handlers.pop()

    fun popInstruction(): DispatchableInstruction = instructions.pop()

    fun popLabel(): Entry.Label? = labels.pop()

    fun peekFrame(): ActivationFrame = frames.peek()

    fun peekInstructionOrNull(): DispatchableInstruction? = instructions.peekOrNull()

    fun peekNthFrameOrNull(n: Int): ActivationFrame? = frames.peekNth(n)

    fun peekNthLabel(n: Int): Entry.Label = labels.peekNth(n)

    fun shrinkFrames(
        depth: Int,
    ) = frames.shrink(depth)

    fun shrinkHandlers(
        depth: Int,
    ) = handlers.shrink(depth)

    fun shrinkInstructions(
        depth: Int,
    ) = instructions.shrink(depth)

    fun shrinkLabels(
        depth: Int,
    ) = labels.shrink(depth)

    fun size() = frames.depth() + instructions.depth() + labels.depth()

    fun framesDepth() = frames.depth()

    fun handlersDepth() = handlers.depth()

    fun instructionsDepth() = instructions.depth()

    fun labelsDepth() = labels.depth()

    fun clear() {
        frames.clear()
        handlers.clear()
        instructions.clear()
        labels.clear()
    }

    fun clearHandlers() = handlers.clear()

    fun clearInstructions() = instructions.clear()

    fun clearLabels() = labels.clear()

    fun clearFrames() = frames.clear()

    fun frames(): List<ActivationFrame> = frames.entries()

    fun handlers(): List<ExceptionHandler> = handlers.entries()

    fun instructions(): List<DispatchableInstruction> = instructions.entries()

    fun labels(): List<Entry.Label> = labels.entries()

    fun fill(controlStack: ControlStack) {
        controlStack.frames.entries().forEach { entry ->
            push(entry)
        }
        controlStack.handlers.entries().forEach { entry ->
            push(entry)
        }
        controlStack.instructions.entries().forEach { entry ->
            push(entry)
        }
        controlStack.labels.entries().forEach { entry ->
            push(entry)
        }
    }

    fun instructionStack() = instructions

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
