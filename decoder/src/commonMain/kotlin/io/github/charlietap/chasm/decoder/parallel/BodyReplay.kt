package io.github.charlietap.chasm.decoder.parallel

import io.github.charlietap.chasm.decoder.FunctionBodyObserver

/** Shows [observer] the bodies of a batch that was decoded before it could follow them. */
internal fun replayBatch(
    context: ParallelDecoderContext,
    batch: Int,
    observer: FunctionBodyObserver,
) {
    for (bodyIndex in context.batches.start(batch) until context.batches.end(batch)) {
        // null when the body failed to decode or was skipped
        val body = context.decodedBodies[bodyIndex] ?: continue
        val instructions = body.body.instructions

        observer.begin(bodyIndex, body.locals)
        for (index in instructions.indices) {
            observer.instruction(FunctionBodyObserver.UNKNOWN_OPCODE, instructions[index])
        }
        observer.end()
    }
}
