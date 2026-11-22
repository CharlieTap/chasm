package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.host.UnsafeHostApi

fun readGlobal(global: Global): Long {
    return global.engine.readGlobal(global)
}

fun readGlobalReference(global: Global): Any? {
    return global.engine.readGlobalReference(global)
}

fun writeGlobal(
    global: Global,
    value: Long,
) {
    global.engine.writeGlobal(global, value)
}

fun writeGlobalReference(
    global: Global,
    value: Any?,
) {
    global.engine.writeGlobalReference(global, value)
}

@UnsafeHostApi
fun unsafeWriteGlobalReference(
    global: Global,
    value: Any?,
) {
    global.engine.unsafeWriteGlobalReference(global, value)
}
