package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.reference.ExceptionReference

fun createException(
    store: Store,
    tag: Tag,
    words: LongArray,
    references: Array<Any?>,
): ExceptionReference {
    return store.engine.createException(tag, words, references)
}

fun exceptionTag(exception: ExceptionReference): Tag {
    return engineOf(exception).exceptionTag(exception)
}

fun payloadSize(exception: ExceptionReference): Int {
    return engineOf(exception).payloadSize(exception)
}

fun readPayload(
    exception: ExceptionReference,
    index: Int,
): Long {
    return engineOf(exception).readPayload(exception, index)
}

fun readPayloadReference(
    exception: ExceptionReference,
    index: Int,
): Any? {
    return engineOf(exception).readPayloadReference(exception, index)
}
