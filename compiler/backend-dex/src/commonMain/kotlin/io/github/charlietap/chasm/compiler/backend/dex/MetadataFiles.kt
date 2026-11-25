package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter

data class PoolLimits(
    val strings: Int,
    val types: Int,
    val fields: Int,
    val methods: Int,
)

internal data class PoolUse(
    val strings: Int,
    val types: Int,
    val fields: Int,
    val methods: Int,
) {

    operator fun plus(other: PoolUse): PoolUse {
        return PoolUse(strings + other.strings, types + other.types, fields + other.fields, methods + other.methods)
    }

    fun within(limits: PoolLimits): Boolean {
        return strings <= limits.strings && types <= limits.types && fields <= limits.fields && methods <= limits.methods
    }
}

internal class MetadataFiles(
    private val dexter: Dexter,
    private val limits: PoolLimits,
    private val first: DexFile?,
) {

    private val scratch by lazy(LazyThreadSafetyMode.NONE) { Dexter() }
    private val finished = ArrayList<ByteArray>()
    private var current: DexFile? = first
    private var used = EMPTY

    fun <T> write(writeClass: (DexFile) -> T): T {

        if (first != null) {
            return writeClass(first)
        }

        return writeClass(file(measure(writeClass)))
    }

    fun abort() {

        val open = current
        if (open != null && open !== first) {
            open.abort()
        }

        current = null
    }

    fun finish(): List<ByteArray> {

        current?.let { file -> finished += file.finish() }
        current = null

        return finished
    }

    private fun file(use: PoolUse): DexFile {

        val open = current
        if (open != null && (used + use).within(limits)) {
            used += use
            return open
        }

        open?.let { file -> finished += file.finish() }
        used = use

        return dexter.beginFile().also { file -> current = file }
    }

    private fun measure(writeClass: (DexFile) -> Any?): PoolUse {

        val file = scratch.beginFile()
        val bytes = try {
            writeClass(file)
            file.finish()
        } catch (failure: Throwable) {
            file.abort()
            throw failure
        }

        return PoolUse(
            strings = headerInt(bytes, STRING_IDS_SIZE),
            types = headerInt(bytes, TYPE_IDS_SIZE),
            fields = headerInt(bytes, FIELD_IDS_SIZE),
            methods = headerInt(bytes, METHOD_IDS_SIZE),
        )
    }

    private fun headerInt(
        bytes: ByteArray,
        offset: Int,
    ): Int {
        return (bytes[offset].toInt() and BYTE) or
            ((bytes[offset + 1].toInt() and BYTE) shl 8) or
            ((bytes[offset + 2].toInt() and BYTE) shl 16) or
            ((bytes[offset + 3].toInt() and BYTE) shl 24)
    }

    companion object {

        val DEX_LIMITS = PoolLimits(strings = 65_536, types = 65_535, fields = 65_536, methods = 65_536)

        private val EMPTY = PoolUse(0, 0, 0, 0)
        private const val STRING_IDS_SIZE = 0x38
        private const val TYPE_IDS_SIZE = 0x40
        private const val FIELD_IDS_SIZE = 0x50
        private const val METHOD_IDS_SIZE = 0x58
        private const val BYTE = 0xFF
    }
}
