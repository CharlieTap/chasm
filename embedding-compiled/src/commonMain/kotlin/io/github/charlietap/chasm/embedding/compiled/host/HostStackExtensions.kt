package io.github.charlietap.chasm.embedding.compiled.host

import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference

context(stack: HostStack)
inline fun HostParameters.readI32(index: Int): Int {
    return stack[this + index].toInt()
}

context(stack: HostStack)
inline fun HostResults.writeI32(
    index: Int,
    value: Int,
) {
    stack[this + index] = value.toLong()
}

context(stack: HostStack)
inline fun HostParameters.readI64(index: Int): Long {
    return stack[this + index]
}

context(stack: HostStack)
inline fun HostResults.writeI64(
    index: Int,
    value: Long,
) {
    stack[this + index] = value
}

context(stack: HostStack)
inline fun HostParameters.readF32(index: Int): Float {
    return Float.fromBits(stack[this + index].toInt())
}

context(stack: HostStack)
inline fun HostResults.writeF32(
    index: Int,
    value: Float,
) {
    stack[this + index] = value.toRawBits().toLong()
}

context(stack: HostStack)
inline fun HostParameters.readF64(index: Int): Double {
    return Double.fromBits(stack[this + index])
}

context(stack: HostStack)
inline fun HostResults.writeF64(
    index: Int,
    value: Double,
) {
    stack[this + index] = value.toRawBits()
}

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
