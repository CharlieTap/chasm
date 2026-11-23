package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class UnloadingTest {

    @Test
    fun `a compiled module's classes are collected once its loader is dropped`() {

        val directory = Files.createTempDirectory("unloading")
        val jar = directory.resolve("declarations.jar")

        try {

            val config = CompileConfig(ENTRY_CLASS, CompileTarget.JVM, RuntimeConfig(stacktraces = true))
            compile(fixture("declarations"), ClassOutput.jar(jar), config).expect("compiles")

            val loaderReference = loadInvokeAndDrop(jar)

            var attempts = 0
            while (loaderReference.get() != null && attempts < GC_ATTEMPTS) {
                System.gc()
                Thread.sleep(GC_PAUSE_MILLIS)
                attempts++
            }

            assertNull(loaderReference.get(), "the module's class loader is still reachable after $attempts collections")
        } finally {
            directory.toFile().deleteRecursively()
        }
    }

    private fun loadInvokeAndDrop(jar: Path): WeakReference<ClassLoader> {

        val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), CompiledModule::class.java.classLoader)
        val compiled = loader.loadClass(ENTRY_CLASS).getField("COMPILED").get(null) as CompiledModule
        val module = module(compiled).expect("loads")
        val store = store(module)
        val instance = runBlocking { instance(store, module) }.expect("instantiates")

        assertSame(loader, compiled.javaClass.classLoader)
        assertEquals(listOf(I32(42)), invoke(instance, "active_data").expect("active_data"))

        val trapped = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(instance, "call_direct", listOf(I32(5), I32(1)))).error)
        val frame = checkNotNull(trapped.trap.stackTrace).frames.first()

        assertEquals(TrapReason.OTHER, trapped.trap.reason)
        assertEquals("declarations", frame.moduleName)
        assertEquals("call_direct", frame.functionName)
        assertEquals(0x123, frame.wasmOffset)

        dropStore(store)
        loader.close()

        return WeakReference(loader)
    }

    private companion object {
        const val ENTRY_CLASS = "chasm.aot.unloading.WasmModule"
        const val GC_ATTEMPTS = 20
        const val GC_PAUSE_MILLIS = 50L
    }
}
