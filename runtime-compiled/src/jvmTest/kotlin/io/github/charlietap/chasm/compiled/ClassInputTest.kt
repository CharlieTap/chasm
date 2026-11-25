package io.github.charlietap.chasm.compiled

import com.sun.management.UnixOperatingSystemMXBean
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiled.internal.CompiledModulePayload
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.jvm.ClassOutput
import io.github.charlietap.chasm.jvm.CompileConfig
import io.github.charlietap.chasm.jvm.MemoryClassOutput
import io.github.charlietap.chasm.jvm.compile
import kotlinx.coroutines.test.runTest
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ClassInputTest {

    private val directory: Path = Files.createTempDirectory("class-input")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun `a jar loads`() = runTest {

        val jar = directory.resolve("module.jar")
        compile(fixture(DECLARATIONS), ClassOutput.jar(jar), CompileConfig(ENTRY_CLASS)).expect("compiles")

        val module = module(ClassInput.jar(jar), ENTRY_CLASS).expect("loads")

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
    }

    @Test
    fun `a directory loads`() = runTest {

        val classes = directory.resolve("classes")
        compile(fixture(DECLARATIONS), ClassOutput.directory(classes), CompileConfig(ENTRY_CLASS)).expect("compiles")

        val module = module(ClassInput.directory(classes), ENTRY_CLASS).expect("loads")

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
    }

    @Test
    fun `memory class files and resources load`() = runTest {

        val output = compiled(DECLARATIONS, ENTRY_CLASS)

        val module = module(ClassInput.memory(output.classFiles, output.resources), ENTRY_CLASS).expect("loads")

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
    }

    @Test
    fun `a missing path is malformed`() {

        val missing = directory.resolve("missing")

        assertEquals(malformed("$missing cannot be read"), module(ClassInput.jar(missing), ENTRY_CLASS))
        assertEquals(malformed("$missing cannot be read"), module(ClassInput.directory(missing), ENTRY_CLASS))
    }

    @Test
    fun `a class that is not a compiled module is malformed`() {

        val classFiles = mapOf(PLAIN_CLASS to plainClass(PLAIN_CLASS))

        val result = module(ClassInput.memory(classFiles), PLAIN_CLASS)

        assertEquals(malformed("class $PLAIN_CLASS is not a compiled module"), result)
    }

    @Test
    fun `a class missing from the input is malformed`() {

        val result = module(ClassInput.memory(emptyMap()), ENTRY_CLASS)

        assertEquals(malformed("class $ENTRY_CLASS was not found"), result)
    }

    @Test
    fun `an entry class the parent already defines is malformed`() = runTest {

        val shadowing = ClassInputTest::class.java.name
        val output = compiled(ADD, shadowing)

        val result = module(ClassInput.memory(output.classFiles, output.resources), shadowing)

        assertEquals(malformed("$shadowing is already defined by the runtime's class loader"), result)
    }

    @Test
    fun `a nested class the parent also defines is never taken from the parent`() = runTest {

        val entry = NESTED_SHADOW_ENTRY
        val nested = `NestedShadow$Code0`::class.java.name
        val output = compiled(ADD, entry)
        assertTrue(nested in output.classFiles)

        val module = module(ClassInput.memory(output.classFiles, output.resources), entry).expect("loads")
        val instance = instance(store(module), module).expect("instantiates")

        assertEquals(listOf(I32(5)), invoke(instance, "add", listOf(I32(2), I32(3))).expect("add"))
        val loader = artifactLoader(module)
        assertSame(loader, Class.forName(nested, false, loader).classLoader)
        assertNotSame(`NestedShadow$Code0`::class.java, Class.forName(nested, false, loader))
    }

    @Test
    fun `a jar missing one generated class is malformed`() = runTest {

        val output = compiled(DECLARATIONS, ENTRY_CLASS)
        val dropped = output.classFiles.keys.first { name -> name != ENTRY_CLASS }
        val jar = directory.resolve("incomplete.jar")
        writeJar(jar, output.classFiles - dropped, output.resources)

        val result = module(ClassInput.jar(jar), ENTRY_CLASS)

        val reason = "class $dropped of the module is missing from the input, does not link, or is defined by another class loader"
        assertEquals(malformed(reason), result)
    }

    @Test
    fun `a jar replaced by rename after module() still serves the old data`() = runTest {

        val output = compiled(DECLARATIONS, ENTRY_CLASS)
        val jar = directory.resolve("module.jar")
        val replacement = directory.resolve("replacement.jar")
        writeJar(jar, output.classFiles, output.resources)
        writeJar(replacement, output.classFiles, output.resources.mapValues { entry -> ByteArray(entry.value.size) })

        val module = module(ClassInput.jar(jar), ENTRY_CLASS).expect("loads")
        Files.move(replacement, jar, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
        val replacedData = JarFile(jar.toFile()).use { replaced -> replaced.getInputStream(replaced.getJarEntry(DATA_PATH)).readBytes() }
        assertTrue(replacedData.all { byte -> byte == 0.toByte() })
    }

    @Test
    fun `a rejected jar is closed`() = runTest {

        val jar = directory.resolve("module.jar")
        compile(fixture(DECLARATIONS), ClassOutput.jar(jar), CompileConfig(ENTRY_CLASS)).expect("compiles")
        val system = ManagementFactory.getOperatingSystemMXBean() as UnixOperatingSystemMXBean
        val before = system.openFileDescriptorCount

        repeat(REJECTIONS) {
            assertIs<ChasmResult.Error<ModuleError>>(module(ClassInput.jar(jar), PLAIN_CLASS))
        }

        assertTrue(system.openFileDescriptorCount - before < REJECTIONS, "rejected jars were left open")
        Files.delete(jar)
    }

    @Test
    fun `a data resource the parent also serves is read from the input`() = runTest {

        val output = compiled(DECLARATIONS, SHADOWED_DATA_ENTRY)
        val parentData = checkNotNull(ClassInputTest::class.java.classLoader.getResource(SHADOWED_DATA_PATH)) {
            "the parent serves no $SHADOWED_DATA_PATH"
        }
        assertTrue(parentData.readBytes().size < output.resources.getValue(SHADOWED_DATA_PATH).size)

        val module = module(ClassInput.memory(output.classFiles, output.resources), SHADOWED_DATA_ENTRY).expect("loads")

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
    }

    @Test
    fun `two inputs with the same class name load side by side`() = runTest {

        val adder = compiled(ADD, ENTRY_CLASS)
        val declarations = compiled(DECLARATIONS, ENTRY_CLASS)

        val adderInput = ClassInput.memory(adder.classFiles, adder.resources)
        val declarationsInput = ClassInput.memory(declarations.classFiles, declarations.resources)

        val adderModule = module(adderInput, ENTRY_CLASS).expect("adder loads")
        val declarationsModule = module(declarationsInput, ENTRY_CLASS).expect("declarations loads")
        val adderInstance = instance(store(adderModule), adderModule).expect("adder instantiates")

        assertEquals(listOf(I32(5)), invoke(adderInstance, "add", listOf(I32(2), I32(3))).expect("add"))
        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(declarationsModule))
        assertNotSame(artifactLoader(adderModule), artifactLoader(declarationsModule))
    }

    @Test
    fun `mutating the arrays given to memory input changes nothing`() = runTest {

        val output = compiled(DECLARATIONS, ENTRY_CLASS)
        val classFiles = output.classFiles.mapValues { entry -> entry.value.copyOf() }
        val resources = output.resources.mapValues { entry -> entry.value.copyOf() }

        val input = ClassInput.memory(classFiles, resources)
        classFiles.values.forEach { bytes -> bytes.fill(0) }
        resources.values.forEach { bytes -> bytes.fill(0) }
        val module = module(input, ENTRY_CLASS).expect("loads")

        assertEquals(listOf(I32(ACTIVE_DATA)), activeData(module))
    }

    private suspend fun compiled(
        fixture: String,
        className: String,
    ): MemoryClassOutput {

        val output = ClassOutput.memory()
        compile(fixture(fixture), output, CompileConfig(className)).expect("compiles")

        return output
    }

    private suspend fun activeData(module: Module): List<*> {

        val instance = instance(store(module), module).expect("instantiates")

        return invoke(instance, "active_data").expect("active_data")
    }

    private fun artifactLoader(module: Module): ClassLoader {
        return checkNotNull((module._payload() as CompiledModulePayload).compiled.javaClass.classLoader)
    }

    private fun writeJar(
        jar: Path,
        classFiles: Map<String, ByteArray>,
        resources: Map<String, ByteArray>,
    ) {
        JarOutputStream(Files.newOutputStream(jar)).use { output ->
            classFiles.forEach { (name, bytes) -> output.entry(name.replace('.', '/') + ".class", bytes) }
            resources.forEach { (path, bytes) -> output.entry(path, bytes) }
        }
    }

    private fun JarOutputStream.entry(
        path: String,
        bytes: ByteArray,
    ) {
        putNextEntry(JarEntry(path))
        write(bytes)
        closeEntry()
    }

    private fun plainClass(name: String): ByteArray {

        val writer = ClassWriter(0)
        writer.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, name.replace('.', '/'), null, "java/lang/Object", null)
        writer.visitEnd()

        return writer.toByteArray()
    }

    private fun malformed(reason: String): ChasmResult.Error<ModuleError.Malformed> {
        return ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: $reason"))
    }

    private companion object {
        const val ADD = "add"
        const val DECLARATIONS = "declarations"
        const val ACTIVE_DATA = 42
        const val REJECTIONS = 32
        const val ENTRY_CLASS = "chasm.compiled.input.WasmModule"
        const val DATA_PATH = "chasm/compiled/input/WasmModule.chasmdata"
        const val PLAIN_CLASS = "chasm.compiled.input.Plain"
        const val NESTED_SHADOW_ENTRY = "io.github.charlietap.chasm.compiled.NestedShadow"
        const val SHADOWED_DATA_ENTRY = "chasm.compiled.shadowed.WasmModule"
        const val SHADOWED_DATA_PATH = "chasm/compiled/shadowed/WasmModule.chasmdata"
    }
}
