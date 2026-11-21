package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE

internal fun LowerContext.lowerUnreached() {
    if (kinds.atLeader()) {
        trap(UNREACHABLE)
    }
}

internal fun LowerContext.lowerFunctionBody() {

    enterPrologue()
    lowerPrologue()

    lowerPositions(0, function.instructions.size)
    emit.place(bodyEnd)
    returnUnreachable()

    lowerOutOfLine()
}

internal fun LowerContext.lowerPositions(
    start: Int,
    end: Int,
) {

    for (position in start until end) {

        emit.place(label(position))
        val reached = enterInstruction(position)
        if (module.options.stacktraces) emit.line(line(position))

        if (reached) {
            lowerInstruction(function.instructions[position], this)
        } else {
            lowerUnreached()
        }

        kinds.commit()
    }
}

internal fun LowerContext.lowerOutOfLine() {

    lowerExceptionHandling()

    outOfLine.forEach { path -> path() }
}
