package io.github.charlietap.chasm.compiler.backend.dex.lower

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
    outOfLine.forEach { path -> path() }
}
