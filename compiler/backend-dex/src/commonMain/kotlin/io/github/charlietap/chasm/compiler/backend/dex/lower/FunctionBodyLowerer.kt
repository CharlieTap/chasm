package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_OUT_OF_BOUNDS

internal fun LowerContext.lowerFunctionBody() {
    val publication = references.publication
    publishesWrites = publication.afterEntry
    lowerPrologue()
    function.instructions.forEachIndexed { position, instruction ->
        emit.place(label(position))
        this.position = position
        publishesWrites = publication.requiresPublicationAfter(position)
        if (module.options.stacktraces) emit.line(line(position))
        lowerInstruction(instruction, this)
    }
    publishesWrites = true
    emit.place(bodyEnd)
    returnUnreachable()
    lowerExceptionHandling()
    if (outOfBoundsUsed) {
        emit.place(OUT_OF_BOUNDS)
        emit.invoke(MEMORY_OUT_OF_BOUNDS)
        returnUnreachable()
    }
    outOfLine.forEach { path -> path() }
}
