package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference

context(references: HostReferenceStack)
inline fun HostParameters.readReference(index: Int): Any? {
    return references[this + index]
}

context(references: HostReferenceStack)
inline fun HostResults.writeReference(
    index: Int,
    value: Any?,
) {
    references[this + index] = value
}

context(references: HostReferenceStack)
inline fun HostParameters.readStructReference(index: Int): StructReference {
    return references[this + index] as StructReference
}

context(references: HostReferenceStack)
inline fun HostParameters.readArrayReference(index: Int): ArrayReference {
    return references[this + index] as ArrayReference
}
