package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmTrap
import io.github.charlietap.chasm.embedding.error.WasmTrapException
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
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
    fun `a trap without diagnostics keeps today's error and exception`() {
        val fixture = Fixture(RuntimeConfig())

        val error = fixture.fail("load_out_of_bounds", i32(65534))

        assertEquals(InvocationError.MemoryOperationOutOfBounds.toString(), error.error)
        assertNull(error.trap)
        val exception = assertFailsWith<IllegalStateException> {
            fixture.invoke("load_out_of_bounds", i32(65534)).expect("load")
        }
        assertFalse(exception is WasmTrapException)
        assertEquals("load: ExecutionError(error=MemoryOperationOutOfBounds)", exception.message)
    }

    @Test
    fun `a trap with diagnostics keeps the same error text`() {
        val fixture = Fixture(DIAGNOSTICS)

        val error = fixture.fail("load_out_of_bounds", i32(65534))

        assertEquals(InvocationError.MemoryOperationOutOfBounds.toString(), error.error)
        assertEquals(ChasmError.ExecutionError(InvocationError.MemoryOperationOutOfBounds.toString()), error)
    }

    @Test
    fun `nested calls report every frame with names and offsets`() {
        val trap = Fixture(DIAGNOSTICS).trap("nested_unreachable")

        assertEquals(TrapReason.UNREACHABLE, trap.reason)
        assertEquals(
            listOf(
                frame(1, "leaf", 0x14f),
                frame(2, "middle", 0x153),
                frame(3, "nested_unreachable", 0x158),
            ),
            trap.frames,
        )
        assertTrue(trap.traceComplete)
        assertEquals(0, trap.omittedFrames)
    }

    @Test
    fun `a single result call site is attributed to its call`() {
        val trap = Fixture(DIAGNOSTICS).trap("divide_by_zero")

        assertEquals(TrapReason.INTEGER_DIVIDE_BY_ZERO, trap.reason)
        assertEquals(listOf(frame(4, "divide", 0x161), frame(5, "divide_by_zero", 0x16b)), trap.frames)
    }

    @Test
    fun `an out of bounds load reports its address width and memory size`() {
        val trap = Fixture(DIAGNOSTICS).trap("load_out_of_bounds", i32(65534))

        assertEquals(TrapReason.MEMORY_OUT_OF_BOUNDS, trap.reason)
        assertEquals(listOf(frame(6, "load", 0x176), frame(7, "load_out_of_bounds", 0x17e)), trap.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.READ, address = 65538, width = 4), trap.memoryAccess.describe())
    }

    @Test
    fun `an out of bounds store reports its address and width`() {
        val trap = Fixture(DIAGNOSTICS).trap("store_out_of_bounds", i32(65535))

        assertEquals(listOf(frame(8, "store_out_of_bounds", 0x187)), trap.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.WRITE, address = 65537, width = 2), trap.memoryAccess.describe())
    }

    @Test
    fun `a negative address is reported as an unsigned address`() {
        val trap = Fixture(DIAGNOSTICS).trap("load_out_of_bounds", i32(-8))

        assertEquals(MemoryAccessOf(MemoryAccess.Kind.READ, address = 0xFFFF_FFF8L + 4, width = 4), trap.memoryAccess.describe())
    }

    @Test
    fun `an out of bounds load from a constant address reports that address`() {
        val trap = Fixture(DIAGNOSTICS).trap("load_immediate")

        assertEquals(listOf(frame(9, "load_immediate", 0x191)), trap.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.READ, address = 70000, width = 8), trap.memoryAccess.describe())
    }

    @Test
    fun `bulk memory failures report the failing range`() {
        val fixture = Fixture(DIAGNOSTICS)

        val fill = fixture.trap("fill_out_of_bounds", i32(65000), i32(1000))
        val copy = fixture.trap("copy_out_of_bounds", i32(0), i32(65530), i32(100))

        assertEquals(listOf(frame(10, "fill_out_of_bounds", 0x19d)), fill.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.FILL, address = 65000, width = 1000), fill.memoryAccess.describe())
        assertEquals(listOf(frame(11, "copy_out_of_bounds", 0x1a9)), copy.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.COPY, address = 65530, width = 100), copy.memoryAccess.describe())
    }

    @Test
    fun `a failing host function is reported at its call site`() {
        val trap = Fixture(DIAGNOSTICS).trap("call_host")

        assertEquals(TrapReason.HOST_FUNCTION_FAILURE, trap.reason)
        assertEquals(listOf(frame(12, "call_host", 0x1b0)), trap.frames)
    }

    @Test
    fun `an indirect call type mismatch is reported at the indirect call`() {
        val trap = Fixture(DIAGNOSTICS).trap("indirect_mismatch")

        assertEquals(TrapReason.INDIRECT_CALL_TYPE_MISMATCH, trap.reason)
        assertEquals(listOf(frame(13, "indirect_mismatch", 0x1b7)), trap.frames)
    }

    @Test
    fun `a deep trace keeps both ends and counts the omitted frames`() {
        val trap = Fixture(DIAGNOSTICS).trap("deep", i32(1000))

        // 1,001 recursive activations plus the exported entry point.
        assertEquals(128, trap.frames.size)
        assertEquals(1002 - 128, trap.omittedFrames)
        assertEquals(frame(14, "recurse", 0x1c2), trap.frames.first())
        assertEquals(frame(14, "recurse", 0x1c9), trap.frames[1])
        assertEquals(frame(15, "deep", 0x1d0), trap.frames.last())
        assertTrue(trap.traceComplete)
        assertTrue(trap.toString().contains("... 874 frames omitted ..."))
    }

    @Test
    fun `a function that tail called away is absent from the trace`() {
        val trap = Fixture(DIAGNOSTICS).trap("tail")

        assertEquals(listOf(frame(16, "tail_target", 0x1d5), frame(18, "tail", 0x1de)), trap.frames)
    }

    @Test
    fun `a fused instruction with several trap sites reports its first site`() {
        val trap = Fixture(DIAGNOSTICS).trap("chained_struct_get")

        assertEquals(listOf(WasmFrame("trap_fixture", 19, "chained_struct_get", 0x1e8)), trap.frames)
    }

    @Test
    fun `expect throws a trap exception carrying the report`() {
        val fixture = Fixture(DIAGNOSTICS)

        val exception = assertFailsWith<WasmTrapException> {
            fixture.invoke("nested_unreachable").expect("call failed")
        }

        assertIs<IllegalStateException>(exception)
        assertEquals(
            """
            call failed: ExecutionError(error=Unreachable)
            unreachable executed
              at leaf (func[1]) wasm offset 0x0014f
              at middle (func[2]) wasm offset 0x00153
              at nested_unreachable (func[3]) wasm offset 0x00158
            """.trimIndent(),
            exception.message,
        )
        assertEquals(TrapReason.UNREACHABLE, exception.trap.reason)
    }

    @Test
    fun `prepared functions report traps too`() {
        val fixture = Fixture(DIAGNOSTICS)
        val prepared = prepareFunction(fixture.store, fixture.instance, "nested_unreachable").expect("prepare")

        val result = prepared.invoke(emptyList())

        val trap = assertNotNull(assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result).error.trap)
        assertEquals(3, trap.frames.size)
    }

    @Test
    fun `a module decoded from a stream reports frames without offsets`() {
        val fixture = Fixture(DIAGNOSTICS, streamed = true)

        val trap = fixture.trap("nested_unreachable")

        assertEquals(
            listOf(
                WasmFrame(null, 1, null, null),
                WasmFrame(null, 2, null, null),
                WasmFrame(null, 3, "nested_unreachable", null),
            ),
            trap.frames,
        )
    }

    @Test
    fun `a binary that no longer matches the installed code yields no offsets`() {
        val bytes = Resource(FIXTURE).readBytes()
        val fixture = Fixture(DIAGNOSTICS, bytes = bytes)
        // Replace the leaf's unreachable with nop; the shared binary now disagrees with the installed code.
        bytes[0x14f] = 0x01

        val trap = fixture.trap("nested_unreachable")

        assertNull(trap.frames.first().wasmOffset)
        assertEquals(0x153, trap.frames[1].wasmOffset)
    }

    @Test
    fun `a constant address with a folded offset reports the effective address`() {
        val fixture = Fixture(DIAGNOSTICS, bytes = Resource(MEMORY_FIXTURE).readBytes(), imports = false)

        val load = fixture.trap("load_constant_offset")
        val store = fixture.trap("store_constant_offset")

        assertEquals(listOf(WasmFrame("memory_fixture", 0, "load_constant_offset", 0x99)), load.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.READ, address = 65538, width = 4), load.memoryAccess.describe())
        assertEquals(listOf(WasmFrame("memory_fixture", 1, "store_constant_offset", 0xa5)), store.frames)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.WRITE, address = 65538, width = 4), store.memoryAccess.describe())
    }

    @Test
    fun `memory init reports its destination only when the destination is out of bounds`() {
        val fixture = Fixture(DIAGNOSTICS, bytes = Resource(MEMORY_FIXTURE).readBytes(), imports = false)

        val source = fixture.trap("init_source_out_of_bounds")
        val destination = fixture.trap("init_destination_out_of_bounds")

        assertEquals(0xb1, source.frames.single().wasmOffset)
        assertNull(source.memoryAccess)
        assertEquals(0xc0, destination.frames.single().wasmOffset)
        assertEquals(MemoryAccessOf(MemoryAccess.Kind.INIT, address = 65536, width = 1), destination.memoryAccess.describe())
    }

    @Test
    fun `an imported start function is not attributed to the module being instantiated`() {
        val store = store()
        val owner = module(Resource(START_OWNER_FIXTURE).readBytes()).expect("decode owner")
        val ownerInstance = instance(store, owner, emptyList()).expect("instantiate owner")
        val boom = ownerInstance.exports.single { export -> export.name == "boom" }.value
        val importer = module(Resource(START_IMPORT_FIXTURE).readBytes()).expect("decode importer")

        val result = instance(store, importer, listOf(Import("owner", "boom", boom)), DIAGNOSTICS)

        val trap = assertNotNull(assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result).error.trap)
        assertEquals(listOf(WasmFrame(null, 0, "boom", null)), trap.frames)
    }

    @Test
    fun `a trap in a start function is reported by instantiation`() {
        val store = store()
        val module = module(Resource(START_FIXTURE).readBytes()).expect("decode")

        val result = instance(store, module, emptyList(), DIAGNOSTICS)

        val error = assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(result).error
        assertEquals(InvocationError.Unreachable.toString(), error.error)
        assertEquals(
            listOf(
                WasmFrame("start_fixture", 0, "boom", 0x1b),
                WasmFrame("start_fixture", 1, "start", 0x1f),
            ),
            assertNotNull(error.trap).frames,
        )
    }

    @Test
    fun `an instance keeps running after a reported trap`() {
        val fixture = Fixture(DIAGNOSTICS)
        fixture.trap("load_out_of_bounds", i32(65534))

        val result = fixture.invoke("load_out_of_bounds", i32(0))

        assertEquals(ChasmResult.Success(listOf<ExecutionValue>(NumberValue.I32(0))), result)
    }

    private class Fixture(
        config: RuntimeConfig,
        streamed: Boolean = false,
        bytes: ByteArray = Resource(FIXTURE).readBytes(),
        imports: Boolean = true,
    ) {
        val store: Store = store()
        val instance: Instance

        init {
            val hostFunction = HostFunction { _, _ -> throw HostFunctionException("host failed") }
            val import = publicImport("env", "host_fail", function(store, functionType(), hostFunction))
            val module = if (streamed) {
                module(FakeSourceReader(bytes)).expect("decode")
            } else {
                module(bytes).expect("decode")
            }
            instance = instance(store, module, if (imports) listOf(import) else emptyList(), config).expect("instantiate")
        }

        fun invoke(name: String, vararg args: ExecutionValue) = invoke(store, instance, name, args.toList())

        fun fail(name: String, vararg args: ExecutionValue): ChasmError.ExecutionError =
            assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(invoke(name, *args)).error

        fun trap(name: String, vararg args: ExecutionValue): WasmTrap = assertNotNull(fail(name, *args).trap)
    }

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
        const val FIXTURE = "integration/trap_diagnostics.wasm"
        const val MEMORY_FIXTURE = "integration/trap_diagnostics_memory.wasm"
        const val START_FIXTURE = "integration/trap_diagnostics_start.wasm"
        const val START_OWNER_FIXTURE = "integration/trap_diagnostics_start_owner.wasm"
        const val START_IMPORT_FIXTURE = "integration/trap_diagnostics_start_import.wasm"
        const val PAGE_SIZE = 65536L
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
