package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Index

data class DataSegmentDeclaration(
    val payload: DataPayload,
    val mode: Mode,
) {

    sealed interface Mode {

        data object Passive : Mode

        data class Active(
            val memoryIndex: Index.MemoryIndex,
            val offset: ConstantExpression,
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

    data class External(
        val offset: Long,
        override val size: Int,
    ) : DataPayload
}

fun interface DataSource {

    fun open(): DataReader
}

interface DataReader : AutoCloseable {

    val length: Long?
        get() = null

    fun read(
        offset: Long,
        size: Int,
    ): ByteArray

    fun read(
        offset: Long,
        size: Int,
        destination: ByteArray,
        destinationOffset: Int,
    ) {
        read(offset, size).copyInto(destination, destinationOffset)
    }
}
