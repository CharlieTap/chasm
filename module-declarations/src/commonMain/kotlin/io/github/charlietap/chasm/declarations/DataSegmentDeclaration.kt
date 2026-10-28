package io.github.charlietap.chasm.declarations

data class DataSegmentDeclaration(
    val payload: DataPayload,
    val mode: Mode,
) {

    sealed interface Mode {

        data object Passive : Mode

        data class Active(
            val memoryIndex: Int,
            val offset: InitializerExpression,
        ) : Mode
    }
}

sealed interface DataPayload {

    val size: Int

    class Inline(
        val bytes: ByteArray,
    ) : DataPayload {

        override val size: Int
            get() = bytes.size

        override fun equals(
            other: Any?,
        ): Boolean = this === other || other is Inline && bytes.contentEquals(other.bytes)

        override fun hashCode(): Int = bytes.contentHashCode()
    }
}
