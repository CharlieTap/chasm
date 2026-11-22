@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmStackTrace
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TrapDiagnosticsTest {

    @Test
    fun `prepared functions report traps too`() = runTest {
        val fixture = fixture(DIAGNOSTICS)
        val prepared = prepareFunction(fixture.instance, "nested_unreachable").expect("prepare")

        val result = prepared.invoke(emptyList())

        val error = assertIs<ChasmResult.Error<*>>(result).error
        val stackTrace = assertNotNull(assertIs<ExecutionError.Trapped>(error).trap.stackTrace)
        assertEquals(3, stackTrace.frames.size)
    }

    @Test
    fun `a module decoded from a stream reports frames without offsets`() = runTest {
        val fixture = fixture(DIAGNOSTICS, streamed = true)

        val stackTrace = fixture.stackTrace("nested_unreachable")

        assertEquals(
            listOf(
                WasmFrame(null, 1, null, null),
                WasmFrame(null, 2, null, null),
                WasmFrame(null, 3, "nested_unreachable", null),
            ),
            stackTrace.frames,
        )
    }

    @Test
    fun `a binary that no longer matches the installed code yields no offsets`() = runTest {
        val bytes = Resource(FIXTURE).readBytes()
        val fixture = fixture(DIAGNOSTICS, bytes = bytes)
        // Replace the leaf's unreachable with nop; the shared binary now disagrees with the installed code.
        bytes[0x14f] = 0x01

        val stackTrace = fixture.stackTrace("nested_unreachable")

        assertNull(stackTrace.frames.first().wasmOffset)
        assertEquals(0x153, stackTrace.frames[1].wasmOffset)
    }

    private suspend fun fixture(
        config: RuntimeConfig,
        streamed: Boolean = false,
        bytes: ByteArray = Resource(FIXTURE).readBytes(),
    ): Fixture {
        val store = store()
        val hostFunction = HostFunction { _, _ -> throw HostFunctionException("host failed") }
        val import = Import("env", "host_fail", function(store, functionType(), hostFunction))
        val module = if (streamed) {
            module(FakeSourceReader(bytes)).expect("decode")
        } else {
            module(bytes).expect("decode")
        }
        val instance = instance(store, module, listOf(import), config).expect("instantiate")

        return Fixture(instance)
    }

    private class Fixture(
        val instance: Instance,
    ) {

        fun invoke(name: String, vararg args: ExecutionValue) = invoke(instance, name, args.toList())

        fun stackTrace(
            name: String,
            vararg args: ExecutionValue,
        ): WasmStackTrace {

            val error = assertIs<ChasmResult.Error<*>>(invoke(name, *args)).error

            return assertNotNull(assertIs<ExecutionError.Trapped>(error).trap.stackTrace)
        }
    }

    private companion object {
        const val FIXTURE = "integration/trap_diagnostics.wasm"
        val DIAGNOSTICS = RuntimeConfig(debugInfo = true)
    }
}
