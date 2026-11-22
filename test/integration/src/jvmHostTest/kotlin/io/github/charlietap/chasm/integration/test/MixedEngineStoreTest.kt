package io.github.charlietap.chasm.integration.test

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.integration.Aot
import io.github.charlietap.chasm.integration.Interpreter
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

class MixedEngineStoreTest {

    @Test
    fun `an interpreter store rejects a compiled module`() = runTest {
        val aot = Aot()
        val interpreter = Interpreter()
        val module = aot.module(bytes(), aot.store(), RuntimeConfig())

        val result = instance(interpreter.store(), module)

        assertIs<ExecutionError.Unsupported>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    @Test
    fun `an aot store rejects a decoded module`() = runTest {
        val aot = Aot()
        val interpreter = Interpreter()
        val module = interpreter.module(bytes(), interpreter.store(), RuntimeConfig())

        val result = instance(aot.store(), module)

        assertIs<ExecutionError.Unsupported>(assertIs<ChasmResult.Error<*>>(result).error)
    }

    private fun bytes(): ByteArray {
        return Resource("integration/$FIXTURE.wasm").readBytes()
    }

    private companion object {
        const val FIXTURE = "fuel"
    }
}
