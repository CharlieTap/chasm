package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.Chasm
import io.github.charlietap.chasm.SourceReader
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
    fun `can run a numeric test`() {

        val byteStream = Resource("src/commonTest/resources/numeric.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)

        val store = Chasm.store()

        val result = Chasm.module(reader)
            .flatMap { module ->
                println(module)
                Chasm.instance(store, module, emptyList())
            }.flatMap { instance ->
                println(instance)
                Chasm.invoke(store, instance, "multiply", listOf(NumberValue.I32(16), NumberValue.I32(4)))
            }

        println(result)

        assertEquals(1, 1)
    }

    fun FakeSourceReader(
        byte: () -> Byte = { 0x00 },
        bytes: (Int) -> ByteArray = { byteArrayOf() },
        exhausted: () -> Boolean = { false },
        peek: () -> SourceReader = { FakeSourceReader() },
    ): SourceReader = object : SourceReader {
        override fun byte(): Byte = byte()

        override fun bytes(amount: Int): ByteArray = bytes(amount)

        override fun exhausted(): Boolean = exhausted()

        override fun peek(): SourceReader = peek()
    }

    fun FakeSourceReader(
        byteStream: Sequence<Byte>,
    ): SourceReader = object : SourceReader {
        private val iter = byteStream.iterator()

        override fun byte(): Byte = iter.next()

        override fun bytes(amount: Int): ByteArray = (1..amount).map {
            iter.next()
        }.toByteArray()

        override fun exhausted(): Boolean = iter.hasNext().not()

        override fun peek(): SourceReader = FakeSourceReader()
    }
}
