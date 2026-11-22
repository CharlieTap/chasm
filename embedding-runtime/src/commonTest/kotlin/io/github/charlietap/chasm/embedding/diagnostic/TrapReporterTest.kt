package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.fixture.START_TRAP_MODULE
import io.github.charlietap.chasm.embedding.fixture.TRAPS_MODULE
import io.github.charlietap.chasm.embedding.fixture.decodedModule
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class TrapReporterTest {

    @Test
    fun `a trap without debug info carries its reason but no stack trace`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, TRAPS_MODULE)

        val trap = trap(invoke(instance, "nested"))

        assertEquals(TrapReason.UNREACHABLE, trap.reason)
        assertNull(trap.stackTrace)
    }

    @Test
    fun `a trap with debug info names frames by their export when the module has no trap source`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, TRAPS_MODULE, config = DEBUG_INFO)

        val trap = trap(invoke(instance, "nested"))

        val expected = listOf(
            WasmFrame(null, 0, null, null),
            WasmFrame(null, 1, null, null),
            WasmFrame(null, 2, "nested", null),
        )
        assertEquals(TrapReason.UNREACHABLE, trap.reason)
        assertEquals(expected, assertNotNull(trap.stackTrace).frames)
    }

    @Test
    fun `a trap with debug info names frames from the instance's trap source`() = runTest {

        val store = namedStore()
        val instance = instantiate(store, TRAPS_MODULE, config = DEBUG_INFO)

        val trap = trap(invoke(instance, "nested"))

        val expected = listOf(
            WasmFrame(MODULE_NAME, 0, "function0", null),
            WasmFrame(MODULE_NAME, 1, "function1", null),
            WasmFrame(MODULE_NAME, 2, "function2", null),
        )
        assertEquals(expected, assertNotNull(trap.stackTrace).frames)
    }

    @Test
    fun `an out of bounds access traps with the memory reason`() = runTest {

        val store = namedStore()
        val instance = instantiate(store, TRAPS_MODULE, config = DEBUG_INFO)

        val trap = trap(invoke(instance, "out_of_bounds"))

        assertEquals(TrapReason.MEMORY_OUT_OF_BOUNDS, trap.reason)
        assertEquals(WasmFrame(MODULE_NAME, 3, "function3", null), assertNotNull(trap.stackTrace).frames.single())
    }

    @Test
    fun `a start function trap names its frames from the instance being created`() = runTest {

        val store = namedStore()

        val result = instance(store, decodedModule(START_TRAP_MODULE), emptyList(), DEBUG_INFO)

        val trap = trap(result)
        assertEquals(TrapReason.UNREACHABLE, trap.reason)
        assertEquals(WasmFrame(MODULE_NAME, 0, "function0", null), assertNotNull(trap.stackTrace).frames.single())
    }

    private fun namedStore(): Store {
        return interpreterStore(trapSource = { instance -> NamedTrapSource(instance) })
    }

    private fun trap(result: ChasmResult<*, *>): Trap {

        val error = assertIs<ChasmResult.Error<*>>(result).error

        return assertIs<ExecutionError.Trapped>(error).trap
    }

    private class NamedTrapSource(
        override val instance: ModuleInstance,
    ) : TrapSource {

        override fun frames(
            runtime: RuntimeStore,
            snapshot: TrapSnapshot,
        ): ModuleFrames {
            return NamedFrames()
        }
    }

    private class NamedFrames : ModuleFrames {

        override val moduleName: String = MODULE_NAME

        override fun functionName(functionIndex: Int): String {
            return "function$functionIndex"
        }
    }

    private companion object {
        const val MODULE_NAME = "diagnostics"
        val DEBUG_INFO = RuntimeConfig(debugInfo = true)
    }
}
