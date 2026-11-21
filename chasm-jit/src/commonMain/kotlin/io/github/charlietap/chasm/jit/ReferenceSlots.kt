package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.host.HostParameters
import io.github.charlietap.chasm.host.HostResults
import kotlin.jvm.JvmInline

@ExperimentalChasmApi
@JvmInline
value class ReferenceSlots internal constructor(
    internal val objects: Array<Any?>,
) {

    constructor(size: Int) : this(arrayOfNulls<Any?>(size))

    val size: Int
        get() = objects.size

    operator fun get(index: Int): WasmReference {
        return WasmReference(objects[index])
    }

    operator fun set(
        index: Int,
        reference: WasmReference,
    ) {
        objects[index] = reference.value
    }
}

@ExperimentalChasmApi
context(references: ReferenceSlots)
fun HostParameters.readReference(index: Int): WasmReference {
    return references[this + index]
}

@ExperimentalChasmApi
context(references: ReferenceSlots)
fun HostResults.writeReference(
    index: Int,
    reference: WasmReference,
) {
    references[this + index] = reference
}
