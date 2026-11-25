package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.TARGET_ART
import io.github.charlietap.chasm.compiled.format.TARGET_HOTSPOT
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class CompiledModuleReaderTest {

    private val reader = CompiledModuleReader(
        abi = COMPILED_RUNTIME_ABI,
        target = ArtifactTarget.HOTSPOT,
        byteArrayLayout = FixedByteArrayLayout(BYTE_ARRAY_BASE),
        ownership = ClassOwnership(),
        settings = RuntimeSettings(),
        checks = ResourceChecks(),
        initializer = RefusingInitializer(),
    )

    private val resources = MemoryResources(emptyMap())

    private val otherAbi = "the module was compiled for runtime ABI ${COMPILED_RUNTIME_ABI + 1} " +
        "but this runtime supports $COMPILED_RUNTIME_ABI"

    @Test
    fun `a module compiled for another ABI is malformed`() {

        val result = reader.read(FakeCompiledModule(abi = COMPILED_RUNTIME_ABI + 1, target = TARGET_HOTSPOT), resources, artifact = null)

        assertEquals(malformed(otherAbi), result)
    }

    @Test
    fun `the class inventory of a module compiled for another ABI is never read`() {

        val compiled = FakeCompiledModule(
            abi = COMPILED_RUNTIME_ABI + 1,
            target = TARGET_HOTSPOT,
            classes = { fail("the inventory of a module for another runtime was read") },
        )

        val result = reader.read(compiled, resources, artifact = BrokenClasses())

        assertEquals(malformed(otherAbi), result)
    }

    @Test
    fun `the class inventory of a module loaded without an artifact is read only when it initialises eagerly`() {

        val compiled = FakeCompiledModule(
            target = TARGET_HOTSPOT,
            classes = { fail("the inventory of a module from the class path was read") },
        )

        val result = reader.read(compiled, resources, artifact = null)

        assertIs<ChasmResult.Success<*>>(result)
    }

    @Test
    fun `an inventory class that fails to link is malformed`() {

        val compiled = FakeCompiledModule(target = TARGET_HOTSPOT, classes = { arrayOf(BROKEN_CLASS) })

        val result = reader.read(compiled, resources, artifact = BrokenClasses())

        assertEquals(
            malformed("class $BROKEN_CLASS of the module is missing from the input, does not link, or is defined by another class loader"),
            result,
        )
    }

    @Test
    fun `a module compiled for another target is malformed`() {

        val result = reader.read(FakeCompiledModule(target = TARGET_ART), resources, artifact = null)

        assertEquals(malformed("the module was compiled for ART but this runtime runs on HotSpot"), result)
    }

    @Test
    fun `a pinned byte array offset that differs from the VM's is malformed`() {

        val compiled = FakeCompiledModule(target = TARGET_HOTSPOT, byteArrayBaseOffset = BYTE_ARRAY_BASE - 4)

        val result = reader.read(compiled, resources, artifact = null)

        val reason = "the module reads byte arrays at offset ${BYTE_ARRAY_BASE - 4} but this VM keeps them at $BYTE_ARRAY_BASE"
        assertEquals(malformed(reason), result)
    }

    @Test
    fun `an unpinned offset loads`() {

        val unpinned = FakeCompiledModule(target = TARGET_HOTSPOT, byteArrayBaseOffset = UNPINNED_OFFSET)
        val pinned = FakeCompiledModule(target = TARGET_HOTSPOT, byteArrayBaseOffset = BYTE_ARRAY_BASE)

        assertIs<ChasmResult.Success<*>>(reader.read(unpinned, resources, artifact = null))
        assertIs<ChasmResult.Success<*>>(reader.read(pinned, resources, artifact = null))
    }

    @Test
    fun `an unpinned module never asks the VM for its byte array offset`() {

        val unpinnedReader = CompiledModuleReader(
            abi = COMPILED_RUNTIME_ABI,
            target = ArtifactTarget.HOTSPOT,
            byteArrayLayout = RefusingByteArrayLayout(),
            ownership = ClassOwnership(),
            settings = RuntimeSettings(),
            checks = ResourceChecks(),
            initializer = RefusingInitializer(),
        )
        val unpinned = FakeCompiledModule(target = TARGET_HOTSPOT, byteArrayBaseOffset = UNPINNED_OFFSET)

        assertIs<ChasmResult.Success<*>>(unpinnedReader.read(unpinned, resources, artifact = null))
    }

    @Test
    fun `invalid settings are malformed`() {

        val result = reader.read(FakeCompiledModule(target = TARGET_HOTSPOT, fuel = -2), resources, artifact = null)

        assertEquals(malformed("the module's settings are invalid"), result)
    }

    @Test
    fun `declarations that throw are malformed`() {

        val compiled = FakeCompiledModule(target = TARGET_HOTSPOT, declarations = { throw IllegalStateException("broken declarations") })

        val result = reader.read(compiled, resources, artifact = null)

        assertEquals(malformed("broken declarations"), result)
    }

    @Test
    fun `declarations that fail to link are malformed`() {

        val failure = NoClassDefFoundError("io/github/charlietap/chasm/declarations/Missing")
        val compiled = FakeCompiledModule(target = TARGET_HOTSPOT, declarations = { throw failure })

        val result = reader.read(compiled, resources, artifact = null)

        assertEquals(malformed("the module's declarations do not link against this runtime: $failure"), result)
    }

    private class FixedByteArrayLayout(
        private val offset: Long,
    ) : ByteArrayLayout {

        override fun baseOffset(): Long {
            return offset
        }
    }

    private class RefusingByteArrayLayout : ByteArrayLayout {

        override fun baseOffset(): Long {
            fail("a module that pins no byte array offset never reads the VM's")
        }
    }

    private class RefusingInitializer : CodeClassInitializer {

        override suspend fun initialize(
            loader: ClassLoader,
            classes: List<String>,
        ) {
            fail("reading a module initialises no classes")
        }
    }

    private class BrokenClasses : ClassLoader(BrokenClasses::class.java.classLoader) {

        override fun findClass(name: String): Class<*> {

            if (name != BROKEN_CLASS) {
                throw ClassNotFoundException(name)
            }

            val writer = ClassWriter(0)
            writer.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, BROKEN_CLASS.replace('.', '/'), null, MISSING_SUPERCLASS, null)
            writer.visitEnd()
            val bytes = writer.toByteArray()

            return defineClass(name, bytes, 0, bytes.size)
        }
    }

    private companion object {
        const val BYTE_ARRAY_BASE = 16L
        const val BROKEN_CLASS = "chasm.compiled.reader.Broken"
        const val MISSING_SUPERCLASS = "chasm/compiled/reader/Missing"
    }
}
