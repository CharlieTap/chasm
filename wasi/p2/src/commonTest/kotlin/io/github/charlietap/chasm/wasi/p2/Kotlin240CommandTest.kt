package io.github.charlietap.chasm.wasi.p2

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ComponentExportInstance
import io.github.charlietap.chasm.embedding.shapes.ComponentFunction
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlin.test.Test
import kotlin.test.assertEquals

class Kotlin240CommandTest {

    @Test
    fun `runs a Kotlin 2_4_0 WASI P2 command component`() {
        val bytes = Resource(COMPONENT_RESOURCE).readBytes()
        val stdoutBytes = mutableListOf<Byte>()
        val stderrBytes = mutableListOf<Byte>()
        val stdout = WasiP2ByteSink { written -> stdoutBytes.addAll(written.toList()) }
        val stderr = WasiP2ByteSink { written -> stderrBytes.addAll(written.toList()) }
        val entropy = WasiP2Entropy { size -> ByteArray(size) }
        val store = store()
        val imports = wasiP2CommandImports(store, stdout, stderr, entropy)
        val component = component(bytes).expect("Kotlin component decoding failed")
        val validated = validate(component).expect("Kotlin component validation failed")
        val instance = instance(store, validated, imports).expect("Kotlin component instantiation failed")
        val runInstance = instance.exports
            .first { export -> export.name == WASI_CLI_RUN }
            .value as ComponentExportInstance
        val run = runInstance.exports
            .first { export -> export.name == "run" }
            .value as ComponentFunction

        val invocation = invoke(store, run)
        val instanceDrop = dropInstance(store, instance)
        val storeDrop = dropStore(store)
        val actual = listOf(
            invocation,
            stdoutBytes.toByteArray().decodeToString(),
            stderrBytes.toByteArray().decodeToString(),
            instanceDrop,
            storeDrop,
        )
        val expected = listOf(
            ChasmResult.Success(listOf(ComponentValue.Result.Ok())),
            "Hello from Kotlin WASI P2\n",
            "",
            ChasmResult.Success(Unit),
            ChasmResult.Success(Unit),
        )

        assertEquals(expected, actual)
    }
}

private const val COMPONENT_RESOURCE = "kotlin-2.4.0-wasi-p2.wasm"
private const val WASI_CLI_RUN = "wasi:cli/run@0.2.6"
