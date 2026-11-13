package io.github.charlietap.chasm.compiler.backend.dex.lower

internal fun LowerContext.lowerFunctionBody() {

    enterPrologue()
    lowerPrologue()

    function.instructions.forEachIndexed { position, instruction ->
        emit.place(label(position))
        enterInstruction(position)
        if (module.options.stacktraces) emit.line(line(position))
        lowerInstruction(instruction, this)
    }

    enterColdCode()
    emit.place(bodyEnd)
    returnUnreachable()
    lowerExceptionHandling()
    outOfLine.forEach { path ->
        enterColdCode()
        path()
    }
}
