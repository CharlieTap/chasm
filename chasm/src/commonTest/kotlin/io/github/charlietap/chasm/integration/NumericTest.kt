package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.Chasm
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.flatMap
import io.github.charlietap.chasm.instance
import io.github.charlietap.chasm.invoke
import io.github.charlietap.chasm.module
import io.github.charlietap.chasm.store
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericTest {

    @Test
    fun `can run a wasm file with numeric instructions and return a correct result`() {

        val byteStream = Resource("src/commonTest/resources/numeric.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val store = Chasm.store()

        val result = Chasm.module(reader)
            .flatMap { module ->
                Chasm.instance(store, module, emptyList())
            }.flatMap { instance ->
                Chasm.invoke(store, instance, "multiply", listOf(NumberValue.I32(16), NumberValue.I32(4)))
            }

        val expected = listOf(NumberValue.I32(4))

        assertEquals(ChasmResult.Success(expected), result)
    }
}
