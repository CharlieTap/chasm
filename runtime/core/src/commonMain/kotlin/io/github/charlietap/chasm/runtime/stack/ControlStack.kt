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
        frames.forEach(this::push)
        handlers.forEach(this::push)
    }

    fun push(frame: ActivationFrame) = frames.push(frame)

    fun push(handler: ExceptionHandler) = handlers.push(handler)

    fun popFrame(): ActivationFrame = frames.pop()

    fun popHandler(): ExceptionHandler = handlers.pop()

    fun peekFrame(): ActivationFrame = frames.peek()

    fun peekNthFrameOrNull(n: Int): ActivationFrame? = frames.peekNth(n)

    fun shrinkFrames(depth: Int) = frames.shrink(depth)

    fun shrinkHandlers(depth: Int) = handlers.shrink(depth)

    fun framesDepth(): Int = frames.depth()

    fun handlersDepth(): Int = handlers.depth()

    fun clear() {
        frames.clear()
        handlers.clear()
    }

    fun clearHandlers() = handlers.clear()

    fun clearFrames() = frames.clear()

    fun frames(): List<ActivationFrame> = frames.entries()

    fun handlers(): List<ExceptionHandler> = handlers.entries()

    fun fill(controlStack: ControlStack) {
        controlStack.frames.entries().forEach(this::push)
        controlStack.handlers.entries().forEach(this::push)
    }

    companion object {
        const val INITIAL_CAPACITY = 32
        const val MAX_DEPTH = 1028
    }
}
