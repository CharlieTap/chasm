@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmTrap
import io.github.charlietap.chasm.embedding.error.WasmTrapException
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.TestScope
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrapDiagnosticsTest {

    @Test
    fun `a trap without diagnostics keeps the existing error and exception`() = integrationTest(FIXTURE) {
        val runner = fixture(RuntimeConfig())

        val error = fail(runner, "load_out_of_bounds", i32(65534))

        assertEquals(InvocationError.MemoryOperationOutOfBounds.toString(), error.error, engine.name())
        assertNull(error.trap, engine.name())
        val exception = assertFailsWith<IllegalStateException>(engine.name()) {
            runner.invoke("load_out_of_bounds", listOf(i32(65534))).expect("load")
        }
        assertFalse(exception is WasmTrapException, engine.name())
        assertEquals("load: ExecutionError(error=MemoryOperationOutOfBounds)", exception.message, engine.name())
    }

    @Test
    fun `a trap with diagnostics keeps the same error text`() = integrationTest(FIXTURE) {
        val error = fail(fixture(DIAGNOSTICS), "load_out_of_bounds", i32(65534))

        assertEquals(InvocationError.MemoryOperationOutOfBounds.toString(), error.error, engine.name())
        assertEquals(ChasmError.ExecutionError(InvocationError.MemoryOperationOutOfBounds.toString()), error, engine.name())
    }

    @Test
    fun `nested calls report every frame with names and offsets`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "nested_unreachable")

        assertEquals(TrapReason.UNREACHABLE, trap.reason, engine.name())
        assertEquals(
            listOf(
                frame(1, "leaf", 0x14f),
                frame(2, "middle", 0x153),
                frame(3, "nested_unreachable", 0x158),
            ),
            trap.frames,
            engine.name(),
        )
        assertTrue(trap.traceComplete, engine.name())
        assertEquals(0, trap.omittedFrames, engine.name())
    }

    @Test
    fun `a single result call site is attributed to its call`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "divide_by_zero")

        assertEquals(TrapReason.INTEGER_DIVIDE_BY_ZERO, trap.reason, engine.name())
        assertEquals(listOf(frame(4, "divide", 0x161), frame(5, "divide_by_zero", 0x16b)), trap.frames, engine.name())
    }

    @Test
    fun `an out of bounds load reports its address width and memory size`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "load_out_of_bounds", i32(65534))

        assertEquals(TrapReason.MEMORY_OUT_OF_BOUNDS, trap.reason, engine.name())
        assertEquals(listOf(frame(6, "load", 0x176), frame(7, "load_out_of_bounds", 0x17e)), trap.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.READ, address = 65538, width = 4),
            trap.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `an out of bounds store reports its address and width`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "store_out_of_bounds", i32(65535))

        assertEquals(listOf(frame(8, "store_out_of_bounds", 0x187)), trap.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.WRITE, address = 65537, width = 2),
            trap.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `a negative address is reported as an unsigned address`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "load_out_of_bounds", i32(-8))

        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.READ, address = 0xFFFF_FFF8L + 4, width = 4),
            trap.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `an out of bounds load from a constant address reports that address`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "load_immediate")

        assertEquals(listOf(frame(9, "load_immediate", 0x191)), trap.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.READ, address = 70000, width = 8),
            trap.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `bulk memory failures report the failing range`() = integrationTest(FIXTURE) {
        val runner = fixture(DIAGNOSTICS)

        val fill = trap(runner, "fill_out_of_bounds", i32(65000), i32(1000))
        val copy = trap(runner, "copy_out_of_bounds", i32(0), i32(65530), i32(100))

        assertEquals(listOf(frame(10, "fill_out_of_bounds", 0x19d)), fill.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.FILL, address = 65000, width = 1000),
            fill.memoryAccess.describe(),
            engine.name(),
        )
        assertEquals(listOf(frame(11, "copy_out_of_bounds", 0x1a9)), copy.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.COPY, address = 65530, width = 100),
            copy.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `a constant address with a folded offset reports the effective address`() = integrationTest(MEMORY_FIXTURE) {
        val runner = fixture(DIAGNOSTICS, imports = false)

        val load = trap(runner, "load_constant_offset")
        val store = trap(runner, "store_constant_offset")

        assertEquals(listOf(WasmFrame("memory_fixture", 0, "load_constant_offset", 0x99)), load.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.READ, address = 65538, width = 4),
            load.memoryAccess.describe(),
            engine.name(),
        )
        assertEquals(listOf(WasmFrame("memory_fixture", 1, "store_constant_offset", 0xa5)), store.frames, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.WRITE, address = 65538, width = 4),
            store.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `memory init reports its destination only when the destination is out of bounds`() = integrationTest(MEMORY_FIXTURE) {
        val runner = fixture(DIAGNOSTICS, imports = false)

        val source = trap(runner, "init_source_out_of_bounds")
        val destination = trap(runner, "init_destination_out_of_bounds")

        assertEquals(0xb1, source.frames.single().wasmOffset, engine.name())
        assertNull(source.memoryAccess, engine.name())
        assertEquals(0xc0, destination.frames.single().wasmOffset, engine.name())
        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.INIT, address = 65536, width = 1),
            destination.memoryAccess.describe(),
            engine.name(),
        )
    }

    @Test
    fun `memory init reads the segment length at the time of the trap`() = integrationTest(DROPPED_SEGMENT_FIXTURE) {
        val beforeDrop = trap(fixture(DIAGNOSTICS, imports = false), "init_before_drop")
        val afterDrop = trap(fixture(DIAGNOSTICS, imports = false), "init_after_drop")

        assertEquals(
            MemoryAccessOf(MemoryAccess.Kind.INIT, address = 65536, width = 1),
            beforeDrop.memoryAccess.describe(),
            engine.name(),
        )
        assertNull(afterDrop.memoryAccess, engine.name())
    }

    @Test
    fun `a failing host function is reported at its call site`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "call_host")

        assertEquals(TrapReason.HOST_FUNCTION_FAILURE, trap.reason, engine.name())
        assertEquals(listOf(frame(12, "call_host", 0x1b0)), trap.frames, engine.name())
    }

    @Test
    fun `a trap in a call a host function makes back into the module reports the frames of that call`() = integrationTest(FIXTURE) {
        val store = store()
        lateinit var runner: TestRunner
        var inner: ChasmResult<List<ExecutionValue>, ChasmError>? = null
        val hostFunction = HostFunction { _, _ ->
            inner = runner.invoke("nested_unreachable")
            throw HostFunctionException("host failed")
        }
        runner = runner(store, listOf(Import("env", "host_fail", function(store, functionType(), hostFunction))), DIAGNOSTICS)

        val outer = trap(runner, "call_host")

        assertEquals(listOf(frame(12, "call_host", 0x1b0)), outer.frames, engine.name())
        val innerError = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(inner, engine.name()).error
        assertEquals(
            listOf(
                frame(1, "leaf", 0x14f),
                frame(2, "middle", 0x153),
                frame(3, "nested_unreachable", 0x158),
            ),
            assertNotNull(innerError.trap, engine.name()).frames,
            engine.name(),
        )
    }

    @Test
    fun `an indirect call type mismatch is reported at the indirect call`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "indirect_mismatch")

        assertEquals(TrapReason.INDIRECT_CALL_TYPE_MISMATCH, trap.reason, engine.name())
        assertEquals(listOf(frame(13, "indirect_mismatch", 0x1b7)), trap.frames, engine.name())
    }

    @Test
    fun `a deep trace keeps both ends and counts the omitted frames`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "deep", i32(1000))

        // 1,001 recursive activations plus the exported entry point.
        assertEquals(128, trap.frames.size, engine.name())
        assertEquals(1002 - 128, trap.omittedFrames, engine.name())
        assertEquals(frame(14, "recurse", 0x1c2), trap.frames.first(), engine.name())
        assertEquals(frame(14, "recurse", 0x1c9), trap.frames[1], engine.name())
        assertEquals(frame(15, "deep", 0x1d0), trap.frames.last(), engine.name())
        assertTrue(trap.traceComplete, engine.name())
        assertTrue(trap.toString().contains("... 874 frames omitted ..."), engine.name())
    }

    @Test
    fun `a function that tail called away is absent from the trace`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "tail")

        assertEquals(listOf(frame(16, "tail_target", 0x1d5), frame(18, "tail", 0x1de)), trap.frames, engine.name())
    }

    @Test
    fun `a fused instruction with several trap sites reports its first site`() = integrationTest(FIXTURE) {
        val trap = trap(fixture(DIAGNOSTICS), "chained_struct_get")

        assertEquals(listOf(WasmFrame("trap_fixture", 19, "chained_struct_get", 0x1e8)), trap.frames, engine.name())
    }

    @Test
    fun `expect throws a trap exception carrying the report`() = integrationTest(FIXTURE) {
        val runner = fixture(DIAGNOSTICS)

        val exception = assertFailsWith<WasmTrapException>(engine.name()) {
            runner.invoke("nested_unreachable").expect("call failed")
        }

        assertIs<IllegalStateException>(exception, engine.name())
        assertEquals(
            """
            call failed: ExecutionError(error=Unreachable)
            unreachable executed
              at leaf (func[1]) wasm offset 0x0014f
              at middle (func[2]) wasm offset 0x00153
              at nested_unreachable (func[3]) wasm offset 0x00158
            """.trimIndent(),
            exception.message,
            engine.name(),
        )
        assertEquals(TrapReason.UNREACHABLE, exception.trap.reason, engine.name())
    }

    @Test
    fun `a trap in a start function is reported by instantiation`() = integrationTest(START_FIXTURE) {
        val result = runner(store(), config = DIAGNOSTICS).invoke("start")

        val error = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result, engine.name()).error
        assertEquals(InvocationError.Unreachable.toString(), error.error, engine.name())
        assertEquals(
            listOf(
                WasmFrame("start_fixture", 0, "boom", 0x1b),
                WasmFrame("start_fixture", 1, "start", 0x1f),
            ),
            assertNotNull(error.trap, engine.name()).frames,
            engine.name(),
        )
    }

    @Test
    fun `an imported start function is not attributed to the module being instantiated`() = integrationTest(START_IMPORT_FIXTURE) {
        val store = store()
        val owner = runner(store, script = START_OWNER_FIXTURE)
        val boom = owner.exports().expect("owner exports").single { export -> export.name == "boom" }.value

        val result = runner(store, listOf(Import("owner", "boom", boom)), DIAGNOSTICS).invoke("not_executed")

        val trap = assertNotNull(assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result, engine.name()).error.trap, engine.name())
        assertEquals(listOf(WasmFrame(null, 0, "boom", null)), trap.frames, engine.name())
    }

    @Test
    fun `an instance keeps running after a reported trap`() = integrationTest(FIXTURE) {
        val runner = fixture(DIAGNOSTICS)
        trap(runner, "load_out_of_bounds", i32(65534))

        val result = runner.invoke("load_out_of_bounds", listOf(i32(0)))

        assertEquals(ChasmResult.Success(listOf<ExecutionValue>(NumberValue.I32(0))), result, engine.name())
    }

    private fun TestScope.fixture(
        config: RuntimeConfig,
        imports: Boolean = true,
    ): TestRunner {
        val store = store()
        val hostFunction = HostFunction { _, _ -> throw HostFunctionException("host failed") }
        val import = Import("env", "host_fail", function(store, functionType(), hostFunction))
        return runner(store, if (imports) listOf(import) else emptyList(), config)
    }

    private fun TestScope.fail(
        runner: TestRunner,
        name: String,
        vararg args: ExecutionValue,
    ): ChasmError.ExecutionError =
        assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(runner.invoke(name, args.toList()), engine.name()).error

    private fun TestScope.trap(
        runner: TestRunner,
        name: String,
        vararg args: ExecutionValue,
    ): WasmTrap = assertNotNull(fail(runner, name, *args).trap, engine.name())

    private data class MemoryAccessOf(
        val kind: MemoryAccess.Kind,
        val memoryIndex: Int = 0,
        val address: Long,
        val width: Long,
        val memorySize: Long = PAGE_SIZE,
    )

    private fun MemoryAccess?.describe(): MemoryAccessOf? = this?.let { access ->
        MemoryAccessOf(access.kind, access.memoryIndex, access.address, access.width, access.memorySize)
    }

    private fun frame(functionIndex: Int, name: String, offset: Int) =
        WasmFrame("trap_fixture", functionIndex, name, offset)

    private fun i32(value: Int) = NumberValue.I32(value)

    private companion object {
        const val FIXTURE = "trap_diagnostics"
        const val MEMORY_FIXTURE = "trap_diagnostics_memory"
        const val DROPPED_SEGMENT_FIXTURE = "trap_diagnostics_dropped_segment"
        const val START_FIXTURE = "trap_diagnostics_start"
        const val START_OWNER_FIXTURE = "trap_diagnostics_start_owner"
        const val START_IMPORT_FIXTURE = "trap_diagnostics_start_import"
        const val PAGE_SIZE = 65536L
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
