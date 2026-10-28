package io.github.charlietap.chasm.compiler.frontend.context

internal class InstructionCursor(
    private val instructions: Array<Any?>,
    private val count: Int = instructions.size,
) {

    var index: Int = -1
        private set

    var nextIndex: Int = 0
        private set

    val current: Any?
        get() = instructions[index]

    fun next(): Any? = peek(nextIndex - index)

    fun peek(
        distance: Int,
    ): Any? {
        val position = index + distance
        return if (position < count) instructions[position] else null
    }

    fun consume(
        count: Int = 1,
    ) {
        nextIndex += count
    }

    fun advance(): Boolean {
        index = nextIndex
        nextIndex = index + 1
        return index < count
    }
}
