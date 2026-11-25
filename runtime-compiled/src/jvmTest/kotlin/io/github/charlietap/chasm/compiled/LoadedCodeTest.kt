package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.jvm.ClassOutput
import io.github.charlietap.chasm.jvm.CompileConfig
import io.github.charlietap.chasm.jvm.MemoryClassOutput
import io.github.charlietap.chasm.jvm.compile
import kotlinx.coroutines.test.runTest
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadedCodeTest {

    private val directory: Path = Files.createTempDirectory("loaded-code")

    @AfterTest
    fun cleanUp() {
        directory.toFile().deleteRecursively()
    }

    @Test
    fun `a module loads from a caller's class loader`() = runTest {

        val classes = directory.resolve("classes")
        compile(fixture(ADD), ClassOutput.directory(classes), CompileConfig(ENTRY_CLASS)).expect("compiles")
        val loader = URLClassLoader(arrayOf(classes.toUri().toURL()), CompiledModule::class.java.classLoader)

        val module = module(loader, ENTRY_CLASS).expect("loads")
        val instance = instance(store(module), module).expect("instantiates")

        assertEquals(listOf(I32(5)), invoke(instance, "add", listOf(I32(2), I32(3))).expect("add"))
    }

    @Test
    fun `a module with data segments loads its data through its own loader`() = runTest {

        val classes = directory.resolve("classes")
        compile(fixture(DECLARATIONS), ClassOutput.directory(classes), CompileConfig(ENTRY_CLASS)).expect("compiles")
        val loader = URLClassLoader(arrayOf(classes.toUri().toURL()), CompiledModule::class.java.classLoader)

        val module = module(loader, ENTRY_CLASS).expect("loads")
        val instance = instance(store(module), module).expect("instantiates")

        assertEquals(listOf(I32(ACTIVE_DATA)), invoke(instance, "active_data").expect("active_data"))
    }

    @Test
    fun `a caller's loader that serves no resources cannot load a module with data segments`() = runTest {

        val output = ClassOutput.memory()
        compile(fixture(DECLARATIONS), output, CompileConfig(ENTRY_CLASS)).expect("compiles")

        val result = module(ClassesOnly(output), ENTRY_CLASS)

        assertEquals(ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: resource $DATA_PATH is missing")), result)
    }

    private class ClassesOnly(
        private val output: MemoryClassOutput,
    ) : ClassLoader(CompiledModule::class.java.classLoader) {

        override fun findClass(name: String): Class<*> {

            val bytes = output.classFiles[name] ?: throw ClassNotFoundException(name)

            return defineClass(name, bytes, 0, bytes.size)
        }
    }

    private companion object {
        const val ADD = "add"
        const val DECLARATIONS = "declarations"
        const val ACTIVE_DATA = 42
        const val ENTRY_CLASS = "chasm.compiled.loaded.WasmModule"
        const val DATA_PATH = "chasm/compiled/loaded/WasmModule.chasmdata"
    }
}
