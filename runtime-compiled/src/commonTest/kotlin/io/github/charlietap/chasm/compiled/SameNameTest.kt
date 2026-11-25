package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.GeneratedClassLoader
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiled.code.generate
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.internal.ClassOwnership
import io.github.charlietap.chasm.compiled.internal.CodeClassInitializer
import io.github.charlietap.chasm.compiled.internal.CompiledModuleReader
import io.github.charlietap.chasm.compiled.internal.FakeCompiledModule
import io.github.charlietap.chasm.compiled.internal.MemoryResources
import io.github.charlietap.chasm.compiled.internal.ResourceChecks
import io.github.charlietap.chasm.compiled.internal.RuntimeSettings
import io.github.charlietap.chasm.compiled.internal.VmByteArrayLayout
import io.github.charlietap.chasm.compiled.internal.platformTarget
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import io.github.charlietap.chasm.embedding.module as decodedModule
import io.github.charlietap.chasm.embedding.store as interpreterStore

class SameNameTest {

    @Test
    fun `a second module compiled under the same class name is unlinkable in one store`() = runTest {

        val first = compiled(ADD, SHARED_NAME)
        val second = compiled(DECLARATIONS, SHARED_NAME)
        val store = store(first)

        instance(store, first).expect("first instantiates")
        val result = instance(store, second)

        assertEquals(ChasmResult.Error(ExecutionError.Unlinkable(sameName(SHARED_NAME))), result)
    }

    @Test
    fun `a failed instantiation gives the name back`() = runTest {

        val failing = compiled(CALL_ADD, SHARED_NAME)
        val other = compiled(ADD, SHARED_NAME)
        val store = store(failing)

        val failed = instance(store, failing)
        val result = instance(store, other)

        assertIs<ChasmResult.Error<*>>(failed)
        assertIs<ChasmResult.Success<*>>(result)
    }

    @Test
    fun `a cancelled instantiation gives the name back`() = runTest {

        val suspended = suspendedModule(AwaitingInitializer())
        val other = compiled(ADD, FakeCompiledModule.RESOURCE_ROOT.replace('/', '.'))
        val store = store(other)

        val instantiating = launch { instance(store, suspended) }
        runCurrent()
        instantiating.cancelAndJoin()
        val result = instance(store, other)

        assertIs<ChasmResult.Success<*>>(result)
    }

    @Test
    fun `a second module is refused while a same-named one is still instantiating`() = runTest {

        val suspended = suspendedModule(AwaitingInitializer())
        val name = FakeCompiledModule.RESOURCE_ROOT.replace('/', '.')
        val other = compiled(ADD, name)
        val store = store(other)

        val instantiating = launch { instance(store, suspended) }
        runCurrent()
        val result = instance(store, other)
        instantiating.cancelAndJoin()

        assertEquals(ChasmResult.Error(ExecutionError.Unlinkable(sameName(name))), result)
    }

    @Test
    fun `the same module instantiates twice in one store`() = runTest {

        val module = compiled(ADD, SHARED_NAME)
        val store = store(module)

        val first = instance(store, module).expect("first instantiates")
        val second = instance(store, module).expect("second instantiates")

        assertEquals(listOf(I32(5)), invoke(first, "add", listOf(I32(2), I32(3))).expect("first adds"))
        assertEquals(listOf(I32(7)), invoke(second, "add", listOf(I32(3), I32(4))).expect("second adds"))
    }

    @Test
    fun `two modules loaded from the same compiled class share its name in one store`() = runTest {

        val generated = generate(fixture(ADD), JvmOptions(entryClass = SHARED_NAME))
        val compiled = GeneratedClassLoader(generated).loadClass(SHARED_NAME).getField("COMPILED").get(null) as CompiledModule
        val first = module(compiled).expect("first loads")
        val second = module(compiled).expect("second loads")
        val store = store(first)

        instance(store, first).expect("first instantiates")
        val result = instance(store, second)

        assertIs<ChasmResult.Success<*>>(result)
    }

    @Test
    fun `same-named modules run in separate stores`() = runTest {

        val first = compiled(ADD, SHARED_NAME)
        val second = compiled(DECLARATIONS, SHARED_NAME)

        val adder = instance(store(first), first).expect("first instantiates")
        val declarations = instance(store(second), second).expect("second instantiates")

        assertEquals(listOf(I32(5)), invoke(adder, "add", listOf(I32(2), I32(3))).expect("add"))
        assertEquals(listOf(I32(40)), invoke(declarations, "started").expect("started"))
    }

    @Test
    fun `a trap crossing two modules reports each module's own frames`() = runTest {

        val decodedBoom = decodedModule(fixture(BOOM)).expect("boom decodes")
        val decodedCaller = decodedModule(fixture(CALLER)).expect("caller decodes")
        val expected = crossingTrap(interpreterStore(DIAGNOSTICS), decodedBoom, decodedCaller)
        val boom = generate(fixture(BOOM), JvmOptions(entryClass = "chasm.compiled.same.Boom", runtime = DIAGNOSTICS)).compiledModule()
        val caller = generate(fixture(CALLER), JvmOptions(entryClass = "chasm.compiled.same.Caller", runtime = DIAGNOSTICS))
            .compiledModule()

        val actual = crossingTrap(store(boom), boom, caller)

        assertEquals(listOf("explode", "call_boom", "call_owner"), expected.map { frame -> frame.functionName })
        assertEquals(expected.map(::named), actual.map(::named))
    }

    private suspend fun crossingTrap(
        store: Store,
        boom: Module,
        caller: Module,
    ): List<WasmFrame> {

        val boomInstance = instance(store, boom).expect("boom instantiates")
        val explode = assertNotNull(exports(boomInstance).function("boom"))
        val callerInstance = instance(store, caller, listOf(Import("owner", "boom", explode))).expect("caller instantiates")

        val trapped = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(invoke(callerInstance, "call_owner")).error)

        return checkNotNull(trapped.trap.stackTrace).frames
    }

    private fun named(frame: WasmFrame): Triple<String?, String?, Int> {
        return Triple(frame.moduleName, frame.functionName, frame.functionIndex)
    }

    private fun compiled(
        fixture: String,
        className: String,
    ): Module {
        return generate(fixture(fixture), JvmOptions(entryClass = className)).compiledModule()
    }

    private fun suspendedModule(initializer: CodeClassInitializer): Module {

        val reader = CompiledModuleReader(
            abi = COMPILED_RUNTIME_ABI,
            target = platformTarget(),
            byteArrayLayout = VmByteArrayLayout(),
            ownership = ClassOwnership(),
            settings = RuntimeSettings(),
            checks = ResourceChecks(),
            initializer = initializer,
        )
        val eagerClasses = arrayOf(FakeCompiledModule.RESOURCE_ROOT.replace('/', '.'))
        val compiled = FakeCompiledModule(classes = { eagerClasses }, initializesEagerly = true)

        return reader.read(compiled, MemoryResources(emptyMap()), artifact = null).expect("loads")
    }

    private fun sameName(className: String): String {
        return "the store already holds another module compiled as $className; give each module of a store its own className"
    }

    private class AwaitingInitializer : CodeClassInitializer {

        override suspend fun initialize(
            loader: ClassLoader,
            classes: List<String>,
        ) {
            awaitCancellation()
        }
    }

    private companion object {
        const val ADD = "add"
        const val CALL_ADD = "call_add"
        const val DECLARATIONS = "declarations"
        const val BOOM = "boom"
        const val CALLER = "trap_diagnostics_mixed"
        const val SHARED_NAME = "chasm.compiled.same.WasmModule"
        val DIAGNOSTICS = RuntimeConfig(stacktraces = true)
    }
}
