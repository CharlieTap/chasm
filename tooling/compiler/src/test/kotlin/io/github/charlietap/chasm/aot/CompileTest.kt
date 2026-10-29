package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.aot.CompileError.Stage
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

private val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)

class CompileTest {

    @Test
    fun `a compiled module names its class and the data resource it wrote`() {
        val output = MemoryOutput()

        val artifact = compile(module("declarations"), output, CompileConfig("test.compiled.declarations")).expect("compiles")

        val data = checkNotNull(artifact.dataResourcePath)
        assertEquals("test.compiled.declarations.WasmModule", artifact.moduleClassName)
        assertEquals("test/compiled/declarations/data.chasmdata", data)
        assertTrue(data.endsWith(CompiledModuleArtifact.DATA_RESOURCE_EXTENSION))
        assertTrue("test/compiled/declarations/WasmModule.class" in output.entries)
        assertTrue(output.entries.getValue(data).isNotEmpty())
    }

    @Test
    fun `a module without data segments writes no data resource`() {
        val output = MemoryOutput()

        val artifact = compile(EMPTY_MODULE, output, CompileConfig("test.empty")).expect("compiles")

        assertEquals(null, artifact.dataResourcePath)
        assertTrue(output.entries.keys.none { name -> name.endsWith(CompiledModuleArtifact.DATA_RESOURCE_EXTENSION) })
    }

    @Test
    fun `a module compiles to the same output from a file as from its bytes`() {
        val bytes = module("declarations")
        val file = Files.createTempFile("module", ".wasm").also { Files.write(it, bytes) }
        val fromFile = MemoryOutput()
        val fromBytes = MemoryOutput()

        compile(file, fromFile, CompileConfig("test.same")).expect("compiles from the file")
        compile(bytes, fromBytes, CompileConfig("test.same")).expect("compiles from the bytes")
        Files.delete(file)

        assertEquals(fromBytes.entries.keys, fromFile.entries.keys)
        fromBytes.entries.forEach { (name, contents) -> assertContentEquals(contents, fromFile.entries.getValue(name), name) }
    }

    @Test
    fun `an Android module reaches memory as Android's memories allow`() {
        val output = MemoryOutput()
        val config = CompileConfig("test.android") { target(CompileTarget.ANDROID) }

        compile(module("declarations"), output, config).expect("compiles")

        assertTrue(output.entries.values.any { bytes -> "sun/misc/Unsafe" in String(bytes, Charsets.ISO_8859_1) })
    }

    @Test
    fun `methods may be as long as the target compiles to machine code unless set`() {
        assertEquals(8_000, CompileConfig("test.jvm").maxMethodBytes)
        assertEquals(12_000, CompileConfig("test.android") { target(CompileTarget.ANDROID) }.maxMethodBytes)
        val set = CompileConfig("test.set") { target(CompileTarget.ANDROID).maxMethodBytes(20_000).maxMethodBytes(function = 3, 9_000) }
        assertEquals(20_000, set.maxMethodBytes)
        assertEquals(mapOf(3 to 9_000), set.functionMaxMethodBytes)
    }

    @Test
    fun `failures say where compilation failed`() {
        val missing = Files.createTempDirectory("compile").resolve("missing.wasm")
        val cases = listOf(
            compile(module("declarations"), MemoryOutput(), CompileConfig("test.class.name")) to Stage.CONFIGURATION,
            compile(module("declarations"), MemoryOutput(), CompileConfig("1test")) to Stage.CONFIGURATION,
            compile(module("declarations"), MemoryOutput(), CompileConfig("test.small") { maxMethodBytes(999) }) to Stage.CONFIGURATION,
            compile(module("declarations"), MemoryOutput(), CompileConfig("test.large") { maxMethodBytes(32_768) }) to Stage.CONFIGURATION,
            compile(missing, MemoryOutput(), CompileConfig("test.missing")) to Stage.INPUT,
            compile(byteArrayOf(1, 2, 3), MemoryOutput(), CompileConfig("test.garbage")) to Stage.DECODE,
            compile(module("invalid"), MemoryOutput(), CompileConfig("test.invalid")) to Stage.VALIDATION,
            compile(module("simd"), MemoryOutput(), CompileConfig("test.simd")) to Stage.GENERATION,
            compile(module("declarations"), FailingOutput(), CompileConfig("test.failing")) to Stage.OUTPUT,
        )

        cases.forEach { (result, stage) ->
            assertIs<CompileResult.Error>(result)
            assertEquals(stage, result.error.stage, result.error.toString())
        }
        val simd = cases.single { (_, stage) -> stage == Stage.GENERATION }.first as CompileResult.Error
        assertEquals(listOf(2), simd.error.functions, simd.error.toString())
        assertTrue("Simd" in simd.error.error, simd.error.toString())
    }

    @Test
    fun `a resource stream that fails part way fails at the output`() {
        val output = object : MemoryOutput() {
            override fun resource(
                path: String,
                write: (OutputStream) -> Unit,
            ) {
                write(
                    object : OutputStream() {
                        override fun write(
                            b: Int,
                        ) = throw IOException("disk full")
                    },
                )
            }
        }

        val result = compile(module("declarations"), output, CompileConfig("test.partial"))

        assertIs<CompileResult.Error>(result)
        assertEquals(Stage.OUTPUT, result.error.stage, result.error.toString())
    }

    @Test
    fun `an output that writes a resource twice fails compilation`() {
        val output = object : MemoryOutput() {
            override fun resource(
                path: String,
                write: (OutputStream) -> Unit,
            ) {
                write(ByteArrayOutputStream())
                write(ByteArrayOutputStream())
            }
        }

        val result = compile(module("declarations"), output, CompileConfig("test.twice"))

        assertIs<CompileResult.Error>(result)
        assertEquals(Stage.OUTPUT, result.error.stage)
    }

    @Test
    fun `an output that does not write a resource fails compilation`() {
        val output = object : MemoryOutput() {
            override fun resource(
                path: String,
                write: (OutputStream) -> Unit,
            ) = Unit
        }

        val result = compile(module("declarations"), output, CompileConfig("test.unwritten"))

        assertIs<CompileResult.Error>(result)
        assertEquals(Stage.OUTPUT, result.error.stage)
    }

    private fun module(
        name: String,
    ): ByteArray =
        checkNotNull(CompileTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }.use { it.readBytes() }

    private open class MemoryOutput : CompileOutput {
        val entries = LinkedHashMap<String, ByteArray>()

        override fun classFile(
            internalName: String,
            bytes: ByteArray,
        ) {
            entries["$internalName.class"] = bytes
        }

        override fun resource(
            path: String,
            write: (OutputStream) -> Unit,
        ) {
            entries[path] = ByteArrayOutputStream().also(write).toByteArray()
        }
    }

    @Test
    fun `a module whose names do not decode is decoded again without them`() {
        val module = WasmModuleDecoder(ModuleConfig(), EMPTY_MODULE).getOrThrow { IllegalStateException(it.toString()) }
        val names = ArrayList<Boolean>()

        val decoded = decodeForCompilation(ModuleConfig()) { config ->
            names += config.decodeNameSection
            if (config.decodeNameSection) Err(CompileError("bad names", Stage.DECODE)) else Ok(module)
        }

        assertEquals(listOf(true, false), names)
        assertSame(module, decoded.get())
    }

    @Test
    fun `a binary that cannot be read is not read again`() {
        var reads = 0

        val decoded = decodeForCompilation(ModuleConfig()) {
            reads++
            Err(CompileError("cannot read", Stage.INPUT))
        }

        assertEquals(1, reads)
        assertEquals(Stage.INPUT, decoded.getError()?.stage)
    }

    private class FailingOutput : MemoryOutput() {
        override fun classFile(
            internalName: String,
            bytes: ByteArray,
        ) = throw IOException("disk full")
    }
}
