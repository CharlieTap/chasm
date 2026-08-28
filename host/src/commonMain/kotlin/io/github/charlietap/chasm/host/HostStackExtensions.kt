package io.github.charlietap.chasm.host

context(stack: HostStack)
inline fun HostParameters.readI32(index: Int): Int = stack[this + index].toInt()

context(stack: HostStack)
inline fun HostResults.writeI32(index: Int, value: Int) {
    stack[this + index] = value.toLong()
}

context(stack: HostStack)
inline fun HostParameters.readI64(index: Int): Long = stack[this + index]

context(stack: HostStack)
inline fun HostResults.writeI64(index: Int, value: Long) {
    stack[this + index] = value
}

context(stack: HostStack)
inline fun HostParameters.readF32(index: Int): Float = Float.fromBits(stack[this + index].toInt())

context(stack: HostStack)
inline fun HostResults.writeF32(index: Int, value: Float) {
    stack[this + index] = value.toRawBits().toLong()
}

context(stack: HostStack)
inline fun HostParameters.readF64(index: Int): Double = Double.fromBits(stack[this + index])

context(stack: HostStack)
inline fun HostResults.writeF64(index: Int, value: Double) {
    stack[this + index] = value.toRawBits()
}

context(stack: HostStack)
inline fun HostParameters.readRawReference(index: Int): Long = stack[this + index]

context(stack: HostStack)
inline fun HostResults.writeRawReference(index: Int, value: Long) {
    stack[this + index] = value
}
