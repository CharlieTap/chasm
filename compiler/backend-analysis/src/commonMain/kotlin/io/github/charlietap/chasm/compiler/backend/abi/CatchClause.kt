package io.github.charlietap.chasm.compiler.backend.abi

data class CatchClause(
    val tag: Int?,
    val target: Int,
    val payloadSlots: IntArray,
    val exceptionReference: Boolean,
    val stackSlots: Int,
) {
    override fun equals(
        other: Any?,
    ): Boolean = this === other ||
        other is CatchClause &&
        tag == other.tag &&
        target == other.target &&
        payloadSlots.contentEquals(other.payloadSlots) &&
        exceptionReference == other.exceptionReference &&
        stackSlots == other.stackSlots

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + target
        result = 31 * result + payloadSlots.contentHashCode()
        result = 31 * result + exceptionReference.hashCode()
        result = 31 * result + stackSlots
        return result
    }

    override fun toString(): String =
        "CatchClause(tag=$tag, target=$target, payloadSlots=${payloadSlots.contentToString()}, " +
            "exceptionReference=$exceptionReference, stackSlots=$stackSlots)"
}
