package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.aot.compiled.generate
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.reference.allocateStruct
import io.github.charlietap.chasm.embedding.reference.structType
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class SharedTypeLinkingTest {

    private val producerModule = generate(fixture("shared_producer"), JvmOptions(entryClass = "chasm.aot.shared.producer.WasmModule")).compiledModule()
    private val consumerModule = generate(fixture("shared_consumer"), JvmOptions(entryClass = "chasm.aot.shared.consumer.WasmModule")).compiledModule()
    private val store = store(producerModule)
    private val producer = runBlocking { instance(store, producerModule) }.expect("producer instantiates")
    private val consumer = runBlocking {
        val makePoint = assertNotNull(exports(producer).function("make_point"))
        instance(store, consumerModule, listOf(Import("producer", "make_point", makePoint)))
    }.expect("consumer instantiates")

    @Test
    fun `two modules defining the same struct type share one RTT`() {

        val produced = struct(producer, "make_point", I32(1), I32(2))
        val consumed = struct(consumer, "make_point", I32(3), I32(4))

        assertSame((produced.reference as CompiledStruct).rtt, (consumed.reference as CompiledStruct).rtt)
        assertEquals(structType(produced.reference), structType(consumed.reference))
    }

    @Test
    fun `a struct made by one module is read by another`() {

        val produced = struct(producer, "make_point", I32(1), I32(2))

        assertEquals(listOf(I32(3)), call(consumer, "sum", produced))
    }

    @Test
    fun `ref_cast across modules follows the subtype chain`() {

        val point = struct(producer, "make_point", I32(1), I32(2))
        val point3 = struct(producer, "make_point3", I32(1), I32(2), I32(9))

        assertEquals(listOf(I32(9)), call(consumer, "third", point3))
        assertEquals(listOf(I32(1)), call(consumer, "is_point3", point3))
        assertEquals(listOf(I32(0)), call(consumer, "is_point3", point))
    }

    @Test
    fun `an import passing a struct type another module defines links`() {
        assertEquals(listOf(I32(5)), call(consumer, "sum_imported"))
    }

    @Test
    fun `host allocation of a shared type uses its storage class`() {

        val produced = struct(producer, "make_point", I32(1), I32(2))

        val allocated = allocateStruct(store, structType(produced.reference), longArrayOf(4L, 5L), arrayOfNulls(2))

        assertSame(produced.reference.javaClass, allocated.javaClass)
        assertEquals(listOf(I32(9)), call(consumer, "sum", ReferenceValue.Struct(allocated)))
    }

    private fun struct(
        instance: Instance,
        name: String,
        vararg arguments: ExecutionValue,
    ): ReferenceValue.Struct {
        return assertIs<ReferenceValue.Struct>(call(instance, name, *arguments).single())
    }

    private fun call(
        instance: Instance,
        name: String,
        vararg arguments: ExecutionValue,
    ): List<ExecutionValue> {
        return invoke(instance, name, arguments.toList()).expect(name)
    }
}
