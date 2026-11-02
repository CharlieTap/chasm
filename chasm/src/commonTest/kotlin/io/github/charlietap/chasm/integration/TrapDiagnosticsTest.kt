@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmTrap
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
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TrapDiagnosticsTest {

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

    private class Fixture(
        config: RuntimeConfig,
        streamed: Boolean = false,
        bytes: ByteArray = Resource(FIXTURE).readBytes(),
    ) {
        val store: Store = store()
        val instance: Instance

        init {
            val hostFunction = HostFunction { _, _ -> throw HostFunctionException("host failed") }
            val import = Import("env", "host_fail", function(store, functionType(), hostFunction))
            val module = if (streamed) {
                module(FakeSourceReader(bytes)).expect("decode")
            } else {
                module(bytes).expect("decode")
            }
            instance = instance(store, module, listOf(import), config).expect("instantiate")
        }

        fun invoke(name: String, vararg args: ExecutionValue) = invoke(store, instance, name, args.toList())

        fun fail(name: String, vararg args: ExecutionValue): ChasmError.ExecutionError =
            assertIs<ChasmResult.Error<ChasmError.ExecutionError>>(invoke(name, *args)).error

        fun trap(name: String, vararg args: ExecutionValue): WasmTrap = assertNotNull(fail(name, *args).trap)
    }

    private companion object {
        const val FIXTURE = "integration/trap_diagnostics.wasm"
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
