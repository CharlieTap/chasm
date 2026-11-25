package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiled.internal.CompiledModulePayload
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.jvm.ClassOutput
import io.github.charlietap.chasm.jvm.CompileConfig
import io.github.charlietap.chasm.jvm.CompileTarget
import io.github.charlietap.chasm.jvm.compile
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

            val config = CompileConfig(ENTRY_CLASS, CompileTarget.HOTSPOT, RuntimeConfig(stacktraces = true))
            runBlocking { compile(fixture("declarations"), ClassOutput.jar(jar), config) }.expect("compiles")

            assertCollected(loadInvokeAndDrop(jar))
        } finally {
            directory.toFile().deleteRecursively()
        }
    }

    @Test
    fun `a compiled module with GC types is collected once its store and loader are dropped`() {

        val directory = Files.createTempDirectory("unloading")
        val jar = directory.resolve("managed_objects.jar")

        try {

            val config = CompileConfig(GC_ENTRY_CLASS, CompileTarget.HOTSPOT, RuntimeConfig())
            runBlocking { compile(fixture("managed_objects"), ClassOutput.jar(jar), config) }.expect("compiles")

            assertCollected(loadAllocateAndDrop(jar))
        } finally {
            directory.toFile().deleteRecursively()
        }
    }

    @Test
    fun `a module loaded from a jar is collected with its loader`() {

        val directory = Files.createTempDirectory("unloading")
        val jar = directory.resolve("declarations.jar")

        try {

            val config = CompileConfig(ENTRY_CLASS, CompileTarget.HOTSPOT, RuntimeConfig(stacktraces = true))
            runBlocking { compile(fixture("declarations"), ClassOutput.jar(jar), config) }.expect("compiles")

            assertCollected(loadJarInvokeAndDrop(jar))
        } finally {
            directory.toFile().deleteRecursively()
        }
    }

    private fun assertCollected(loaderReference: WeakReference<ClassLoader>) {

        var attempts = 0
        while (loaderReference.get() != null && attempts < GC_ATTEMPTS) {
            System.gc()
            Thread.sleep(GC_PAUSE_MILLIS)
            attempts++
        }

        assertNull(loaderReference.get(), "the module's class loader is still reachable after $attempts collections")
    }

    private fun loadAllocateAndDrop(jar: Path): WeakReference<ClassLoader> {

        val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), CompiledModule::class.java.classLoader)
        val compiled = loader.loadClass(GC_ENTRY_CLASS).getField("COMPILED").get(null) as CompiledModule
        val module = module(compiled).expect("loads")
        val store = store(module)
        val instance = runBlocking { instance(store, module) }.expect("instantiates")

        val point = invoke(instance, "make_point", listOf(I32(4), I32(5))).expect("make_point").single()
        invoke(instance, "keep", listOf(point)).expect("keep")
        val kept = invoke(instance, "kept").expect("kept").single()

        assertEquals(listOf(I32(4)), invoke(instance, "x", listOf(kept)).expect("x"))

        dropStore(store)
        loader.close()

        return WeakReference(loader)
    }

    private fun loadInvokeAndDrop(jar: Path): WeakReference<ClassLoader> {

        val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), CompiledModule::class.java.classLoader)
        val compiled = loader.loadClass(ENTRY_CLASS).getField("COMPILED").get(null) as CompiledModule
        val module = module(compiled).expect("loads")

        assertSame(loader, compiled.javaClass.classLoader)
        invokeAndDrop(module)
        loader.close()

        return WeakReference(loader)
    }

    private fun loadJarInvokeAndDrop(jar: Path): WeakReference<ClassLoader> {

        val module = module(ClassInput.jar(jar), ENTRY_CLASS).expect("loads")
        val loader = checkNotNull((module._payload() as CompiledModulePayload).compiled.javaClass.classLoader)

        invokeAndDrop(module)

        return WeakReference(loader)
    }

    private fun invokeAndDrop(module: Module) {

        val store = store(module)
        val instance = runBlocking { instance(store, module) }.expect("instantiates")

        assertEquals(listOf(I32(42)), invoke(instance, "active_data").expect("active_data"))

        val result = invoke(instance, "call_direct", listOf(I32(5), I32(1)))
        val trapped = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(result).error)
        val frame = checkNotNull(trapped.trap.stackTrace).frames.first()

        assertEquals(TrapReason.OTHER, trapped.trap.reason)
        assertEquals("declarations", frame.moduleName)
        assertEquals("call_direct", frame.functionName)
        assertEquals(0x123, frame.wasmOffset)

        dropStore(store)
    }

    private companion object {
        const val ENTRY_CLASS = "chasm.aot.unloading.WasmModule"
        const val GC_ENTRY_CLASS = "chasm.aot.unloading.gc.WasmModule"
        const val GC_ATTEMPTS = 20
        const val GC_PAUSE_MILLIS = 50L
    }
}
