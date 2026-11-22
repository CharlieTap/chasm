package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.reference.ExceptionReference

fun createException(
    tag: Tag,
    words: LongArray,
    references: Array<Any?>,
): ExceptionReference {
    return tag.engine.createException(tag, words, references)
}

fun exceptionTag(exception: ExceptionReference): Tag {
    return exception.engine().exceptionTag(exception)
}

fun payloadSize(exception: ExceptionReference): Int {
    return exception.engine().payloadSize(exception)
}

fun readPayload(
    exception: ExceptionReference,
    index: Int,
): Long {
    return exception.engine().readPayload(exception, index)
}

fun readPayloadReference(
    exception: ExceptionReference,
    index: Int,
): Any? {
    return exception.engine().readPayloadReference(exception, index)
}
