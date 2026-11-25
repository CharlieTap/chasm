package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleNames
import java.io.IOException
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResourceChecksTest {

    private val checks = ResourceChecks()

    private val empty = MemoryResources(emptyMap())

    private val data = dataResource(ROOT)

    private val sites = trapSitesResource(ROOT)

    @Test
    fun `a module with data segments and no data resource is malformed`() {

        val failure = checks.failure(empty, ROOT, declarations(active(0, 4)), stacktraces = false)

        assertEquals("resource $data is missing", failure)
    }

    @Test
    fun `a module whose external segments are all empty still needs its data resource`() {

        val failure = checks.failure(empty, ROOT, declarations(active(0, 0), passive(0, 0)), stacktraces = false)

        assertEquals("resource $data is missing", failure)
    }

    @Test
    fun `a data resource shorter than an active segment needs is malformed`() {

        val resources = MemoryResources(mapOf(data to ByteArray(4)))

        val failure = checks.failure(resources, ROOT, declarations(active(0, 2), active(2, 4)), stacktraces = false)

        assertEquals("resource $data holds 4 bytes but a data segment needs 4 bytes at 2", failure)
    }

    @Test
    fun `a data resource shorter than a passive segment needs is malformed`() {

        val resources = MemoryResources(mapOf(data to ByteArray(4)))

        val failure = checks.failure(resources, ROOT, declarations(passive(0, 8)), stacktraces = false)

        assertEquals("resource $data holds 4 bytes but a data segment needs 8 bytes at 0", failure)
    }

    @Test
    fun `a module without data segments needs no data resource`() {

        val failure = checks.failure(empty, ROOT, declarations(), stacktraces = false)

        assertNull(failure)
    }

    @Test
    fun `a traced module without a sites resource is malformed`() {

        val failure = checks.failure(empty, ROOT, declarations(), stacktraces = true)

        assertEquals("resource $sites is missing", failure)
    }

    @Test
    fun `an untraced module needs no sites resource`() {

        val resources = MemoryResources(mapOf(data to ByteArray(8)))

        val failure = checks.failure(resources, ROOT, declarations(active(0, 4), passive(4, 4)), stacktraces = false)

        assertNull(failure)
    }

    @Test
    fun `a data resource that cannot be read is malformed`() {

        val failure = checks.failure(UnreadableResources(), ROOT, declarations(active(0, 4)), stacktraces = false)

        assertEquals("resource $data cannot be read", failure)
    }

    @Test
    fun `a sites resource that cannot be read is malformed`() {

        val failure = checks.failure(UnreadableResources(), ROOT, declarations(), stacktraces = true)

        assertEquals("resource $sites cannot be read", failure)
    }

    private class UnreadableResources : ModuleResources {

        override fun contains(path: String): Boolean {
            throw IOException("$path cannot be read")
        }

        override fun length(path: String): Long? {
            throw IOException("$path cannot be read")
        }

        override fun open(path: String): InputStream? {
            throw IOException("$path cannot be read")
        }

        override fun data(path: String): DataSource {
            return DataSource { throw IOException("$path cannot be read") }
        }
    }

    private fun declarations(vararg segments: DataSegmentDeclaration): ModuleDeclarations {
        return ModuleDeclarations(
            definedTypes = emptyList(),
            imports = emptyList(),
            functionTypeIndices = IntArray(0),
            tables = emptyList(),
            memories = emptyList(),
            tags = emptyList(),
            globals = emptyList(),
            exports = emptyList(),
            startFunction = null,
            elementSegments = emptyList(),
            dataSegments = segments.toList(),
            names = ModuleNames.NONE,
        )
    }

    private fun active(
        offset: Long,
        size: Int,
    ): DataSegmentDeclaration {
        val mode = DataSegmentDeclaration.Mode.Active(Index.MemoryIndex(0u), ConstantExpression(listOf(ConstantInstruction.I32Const(0))))
        return DataSegmentDeclaration(DataPayload.External(offset, size), mode)
    }

    private fun passive(
        offset: Long,
        size: Int,
    ): DataSegmentDeclaration {
        return DataSegmentDeclaration(DataPayload.External(offset, size), DataSegmentDeclaration.Mode.Passive)
    }

    private companion object {
        const val ROOT = "chasm/compiled/checked/Module"
    }
}
