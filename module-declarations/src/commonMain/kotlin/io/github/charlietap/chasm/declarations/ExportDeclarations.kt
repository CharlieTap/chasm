package io.github.charlietap.chasm.declarations

class ExportDeclarations(
    val names: Array<String>,
    val kinds: ByteArray,
    val indices: IntArray,
) {

    init {
        require(kinds.size == names.size && indices.size == names.size) {
            "export columns differ in length: ${names.size} names, ${kinds.size} kinds, ${indices.size} indices"
        }
        kinds.forEach { kind -> require(kind in FUNCTION..TAG) { "unknown export kind $kind" } }
    }

    val size: Int
        get() = names.size

    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (other !is ExportDeclarations) return false
        return names.contentEquals(other.names) &&
            kinds.contentEquals(other.kinds) &&
            indices.contentEquals(other.indices)
    }

    override fun hashCode(): Int {
        var result = names.contentHashCode()
        result = 31 * result + kinds.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }

    companion object {
        const val FUNCTION: Byte = 0
        const val TABLE: Byte = 1
        const val MEMORY: Byte = 2
        const val GLOBAL: Byte = 3
        const val TAG: Byte = 4
    }
}
