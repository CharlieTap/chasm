package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler

data class ControlStack(
    private val frames: FrameStack = FrameStack(),
    private val handlers: HandlerStack = HandlerStack(INITIAL_CAPACITY),
) {
    constructor(
        frames: List<ActivationFrame>,
        handlers: List<ExceptionHandler>,
    ) : this() {
        frames.forEach { frame ->
            this.frames.push(frame)
        }
        handlers.forEach { handler ->
            this.handlers.push(handler)
        }
    }

    fun push(frame: ActivationFrame) = frames.push(frame)

    fun push(handler: ExceptionHandler) = handlers.push(handler)

    fun popFrame(): ActivationFrame = frames.pop()

    fun popHandler(): ExceptionHandler = handlers.pop()

    fun peekFrame(): ActivationFrame = frames.peek()

    fun peekNthFrameOrNull(n: Int): ActivationFrame? = frames.peekNth(n)

    fun shrinkFrames(
        depth: Int,
    ) = frames.shrink(depth)

    fun shrinkHandlers(
        depth: Int,
    ) = handlers.shrink(depth)

    fun size() = frames.depth()

    fun framesDepth() = frames.depth()

    fun handlersDepth() = handlers.depth()

    fun clear() {
        frames.clear()
        handlers.clear()
    }

    fun clearHandlers() = handlers.clear()

    fun clearFrames() = frames.clear()

    fun frames(): List<ActivationFrame> = frames.entries()

    fun handlers(): List<ExceptionHandler> = handlers.entries()

    fun fill(controlStack: ControlStack) {
        controlStack.frames.entries().forEach { entry ->
            push(entry)
        }
        controlStack.handlers.entries().forEach { entry ->
            push(entry)
        }
    }

    companion object {
        const val INITIAL_CAPACITY = 256
        const val MAX_DEPTH = 1028
    }
}
