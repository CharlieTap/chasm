package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.addFuel
import io.github.charlietap.chasm.embedding.compiled.arrayElementInfo
import io.github.charlietap.chasm.embedding.compiled.arrayType
import io.github.charlietap.chasm.embedding.compiled.collect
import io.github.charlietap.chasm.embedding.compiled.copyMemory
import io.github.charlietap.chasm.embedding.compiled.dropInstance
import io.github.charlietap.chasm.embedding.compiled.dropStore
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.fillMemory
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.global
import io.github.charlietap.chasm.embedding.compiled.growMemory
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.HostModuleInstance
import io.github.charlietap.chasm.embedding.compiled.host.copy
import io.github.charlietap.chasm.embedding.compiled.host.fill
import io.github.charlietap.chasm.embedding.compiled.host.hasPending
import io.github.charlietap.chasm.embedding.compiled.host.move
import io.github.charlietap.chasm.embedding.compiled.host.raise
import io.github.charlietap.chasm.embedding.compiled.host.rawValue
import io.github.charlietap.chasm.embedding.compiled.host.read
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.runtimeType
import io.github.charlietap.chasm.embedding.compiled.host.size
import io.github.charlietap.chasm.embedding.compiled.host.structFieldCount
import io.github.charlietap.chasm.embedding.compiled.host.withGlobal
import io.github.charlietap.chasm.embedding.compiled.host.withMemory
import io.github.charlietap.chasm.embedding.compiled.host.withTable
import io.github.charlietap.chasm.embedding.compiled.host.write
import io.github.charlietap.chasm.embedding.compiled.host.writeI32
import io.github.charlietap.chasm.embedding.compiled.host.writeUtf8String
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.interrupt
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.isSubtype
import io.github.charlietap.chasm.embedding.compiled.memory
import io.github.charlietap.chasm.embedding.compiled.memoryByteSize
import io.github.charlietap.chasm.embedding.compiled.moveMemory
import io.github.charlietap.chasm.embedding.compiled.moveTable
import io.github.charlietap.chasm.embedding.compiled.readByte
import io.github.charlietap.chasm.embedding.compiled.readBytes
import io.github.charlietap.chasm.embedding.compiled.readDouble
import io.github.charlietap.chasm.embedding.compiled.readFloat
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readInt
import io.github.charlietap.chasm.embedding.compiled.readLong
import io.github.charlietap.chasm.embedding.compiled.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.compiled.readPayload
import io.github.charlietap.chasm.embedding.compiled.readShort
import io.github.charlietap.chasm.embedding.compiled.readStructField
import io.github.charlietap.chasm.embedding.compiled.readTable
import io.github.charlietap.chasm.embedding.compiled.readUtf8String
import io.github.charlietap.chasm.embedding.compiled.remainingFuel
import io.github.charlietap.chasm.embedding.compiled.resetFuel
import io.github.charlietap.chasm.embedding.compiled.sizeMemory
import io.github.charlietap.chasm.embedding.compiled.structFieldCount
import io.github.charlietap.chasm.embedding.compiled.structFieldInfo
import io.github.charlietap.chasm.embedding.compiled.structType
import io.github.charlietap.chasm.embedding.compiled.table
import io.github.charlietap.chasm.embedding.compiled.tag
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.F32
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.F64
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.compiled.writeByte
import io.github.charlietap.chasm.embedding.compiled.writeBytes
import io.github.charlietap.chasm.embedding.compiled.writeDouble
import io.github.charlietap.chasm.embedding.compiled.writeFloat
import io.github.charlietap.chasm.embedding.compiled.writeGlobal
import io.github.charlietap.chasm.embedding.compiled.writeInt
import io.github.charlietap.chasm.embedding.compiled.writeLong
import io.github.charlietap.chasm.embedding.compiled.writeShort
import io.github.charlietap.chasm.embedding.compiled.writeTable
import io.github.charlietap.chasm.embedding.compiled.writeUtf8String
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EmbeddingApiTest {

    @Test
    fun memoryCallsReadAndWriteThroughTheCatalogueAndTheHost() {

        val (store, instance) = instantiate()
        val memory = checkNotNull(exports(instance).memory("memory"))

        writeInt(memory, 0, 41)
        call(store, instance, "call_touch_memory")

        assertEquals(42, readInt(memory, 0))
        assertEquals(42, call(store, instance, "load", I32(0)).single().let { value -> (value as I32).value })
        assertContentEquals(byteArrayOf(1, 2, 3), readBytes(memory, ByteArray(3), 8, 3))
        assertEquals(9, readByte(memory, 16).toInt())
        assertContentEquals(byteArrayOf(1, 1, 2, 3), readBytes(memory, ByteArray(4), 32, 4))
        assertEquals("hi", readUtf8String(memory, 64, 2))
        assertEquals("hi", readNullTerminatedUtf8String(memory, 64))

        writeByte(memory, 100, 7)
        writeShort(memory, 102, 300)
        writeLong(memory, 104, Long.MAX_VALUE)
        writeFloat(memory, 112, 1.5f)
        writeDouble(memory, 120, 2.5)
        writeBytes(memory, 128, byteArrayOf(4, 5, 6))
        writeUtf8String(memory, 136, "ok")
        fillMemory(memory, 140, 3, 2)
        copyMemory(memory, 128, memory, 144, 3)
        moveMemory(memory, 144, memory, 145, 2)

        assertEquals(7, readByte(memory, 100).toInt())
        assertEquals(300, readShort(memory, 102).toInt())
        assertEquals(Long.MAX_VALUE, readLong(memory, 104))
        assertEquals(1.5f, readFloat(memory, 112))
        assertEquals(2.5, readDouble(memory, 120))
        assertEquals("ok", readUtf8String(memory, 136, 2))
        assertContentEquals(byteArrayOf(3, 3), readBytes(memory, ByteArray(2), 140, 2))
        assertContentEquals(byteArrayOf(4, 4, 5), readBytes(memory, ByteArray(3), 144, 3))
        assertEquals(1, sizeMemory(memory))
        assertEquals(1, growMemory(memory, 1))
        assertEquals(2 * PAGE_BYTES, memoryByteSize(memory))
        assertEquals(-1, growMemory(memory, 8))
        assertFailsWith<IndexOutOfBoundsException> { readInt(memory, 2 * PAGE_BYTES) }
    }

    @Test
    fun tablesThroughTheCatalogueAndTheHost() {

        val (store, instance) = instantiate()
        val table = checkNotNull(exports(instance).table("table"))
        val inc = (call(store, instance, "inc_ref").single() as ReferenceValue.Function).reference

        writeTable(table, 0, inc)
        call(store, instance, "call_touch_table")
        moveTable(table, 0, table, 2, 2)

        assertEquals(I32(6), call(store, instance, "call_slot", I32(1), I32(5)).single())
        assertSame(inc, readTable(table, 3))
    }

    @Test
    fun globalsThroughTheCatalogueAndTheHost() {

        val (store, instance) = instantiate()
        val counter = checkNotNull(exports(instance).global("counter"))

        call(store, instance, "call_touch_global")

        assertEquals(15L, readGlobal(counter))
    }

    @Test
    fun createdResourcesLinkAsImports() {

        val (store, instance) = instantiate()

        writeGlobal(sharedGlobal, 9)

        assertEquals(I64(9), call(store, instance, "shared").single())
        assertFailsWith<IllegalArgumentException> { global(store, GlobalType(ValueType.Number(NumberType.I32), Mutability.Var), F64(1.0)) }
    }

    @Test
    fun gcTypeQueriesAnswerFromTheStoreAndTheHost() {

        val (store, instance) = instantiate()
        val box = structType(call(store, instance, "box", I32(1)).single().let { value -> (value as ReferenceValue.Struct).reference })
        val bigBox = structType(call(store, instance, "big_box").single().let { value -> (value as ReferenceValue.Struct).reference })
        val bytes = arrayType(call(store, instance, "bytes", I32(2)).single().let { value -> (value as ReferenceValue.Array).reference })

        assertEquals(2, structFieldCount(store, box))
        assertTrue(structFieldInfo(store, box, 1).isPackedI8)
        assertTrue(structFieldInfo(store, box, 0).mutable)
        assertTrue(arrayElementInfo(store, bytes).isPackedI8)
        assertTrue(isSubtype(store, bigBox, box))
        assertFalse(isSubtype(store, box, bigBox))
        assertEquals(I32(2), call(store, instance, "call_types").single())
    }

    @Test
    fun hostsReRaiseExceptionsFromNestedCalls() {

        val (store, instance) = instantiate()

        assertEquals(I32(4), call(store, instance, "relay_caught", I32(4)).single())
    }

    @Test
    fun boxedCallsConvertValuesByType() {

        val (store, instance) = instantiate()

        assertIs<ExecutionError.Rejected>(error(invoke(store, instance, "inc", emptyList())))
        assertIs<ExecutionError.Rejected>(error(invoke(store, instance, "inc", listOf(F32(1f)))))
        assertEquals(ReferenceValue.Null(AbstractHeapType.NoFunc), call(store, instance, "null_func").single())
        assertEquals(ReferenceValue.Null(AbstractHeapType.NoException), call(store, instance, "null_exn").single())
        assertEquals(5, (call(store, instance, "i31", I32(5)).single() as ReferenceValue.I31).reference.value)
        assertIs<FunctionReference>((call(store, instance, "inc_ref").single() as ReferenceValue.Function).reference)
        assertIs<I31Reference>((call(store, instance, "i31", I32(1)).single() as ReferenceValue.I31).reference)
    }

    @Test
    fun fuelIsMeteredThroughStoreControl() {

        val store = store(StoreConfig(meterFuel = true))

        addFuel(store, 1_000)
        val added = remainingFuel(store)
        resetFuel(store)

        assertEquals(1_000L, added)
        assertEquals(0L, remainingFuel(store))
        assertFailsWith<IllegalArgumentException> { addFuel(store, -1) }
        assertFailsWith<IllegalStateException> { remainingFuel(store()) }
    }

    @Test
    fun interruptsStopRunningGuests() {

        val store = store(StoreConfig(interruptible = true))
        val instance = instantiate(store)
        var result: ChasmResult<List<ExecutionValue>, *>? = null
        val guest = Thread { result = invoke(store, instance, "spin") }

        guest.start()
        val deadline = System.nanoTime() + INTERRUPT_DEADLINE
        while (!interrupt(store) && System.nanoTime() < deadline) {
            Thread.sleep(1)
        }
        guest.join(INTERRUPT_JOIN)
        collect(store)

        assertFalse(guest.isAlive, "the guest kept running after the interrupt")

        assertEquals(TrapReason.INTERRUPTED, assertIs<ExecutionError.Trapped>(error(checkNotNull(result))).trap.reason)
    }

    @Test
    fun dropsAreCheckedAndDroppedStoresRefuseCalls() {

        val (store, instance) = instantiate()
        val memory = checkNotNull(exports(instance).memory("memory"))

        dropInstance(instance)

        assertFailsWith<IllegalStateException> { dropInstance(instance) }

        dropStore(store)

        assertFailsWith<IllegalStateException> { dropStore(store) }
        assertFailsWith<IllegalStateException> { readInt(memory, 0) }
        assertFailsWith<IllegalStateException> { memory(store, memoryType()) }
    }

    @Test
    fun objectsRefuseStoreCallsOnceTheirStoreIsDropped() {

        val (store, instance) = instantiate()
        val box = (call(store, instance, "box", I32(1)).single() as ReferenceValue.Struct).reference
        val failed = assertIs<ExecutionError.Thrown<ExceptionReference>>(error(invoke(store, instance, "fail", listOf(I32(2))))).exception

        dropStore(store)

        assertFailsWith<IllegalStateException> { readStructField(box, 0) }
        assertFailsWith<IllegalStateException> { readPayload(failed, 0) }
    }

    @Test
    fun unvalidatedModulesValidate() {

        val unvalidated = runBlocking { module(resourceBytes("modules/embedding_api.wasm"), validate = false) }.expect("module")

        validate(unvalidated).expect("validate")
    }

    private lateinit var sharedGlobal: Global

    private fun instantiate(): Pair<Store, Instance> {

        val store = store()

        return store to instantiate(store)
    }

    private fun instantiate(store: Store): Instance {

        sharedGlobal = global(store, GlobalType(ValueType.Number(NumberType.I64), Mutability.Var), I64(3))
        val module = runBlocking { module(resourceBytes("modules/embedding_api.wasm")) }.expect("module")
        val imports = listOf(
            Import("host", "touch_memory", function(store, functionType(emptyList(), emptyList()), touchMemory)),
            Import("host", "touch_table", function(store, functionType(emptyList(), emptyList()), touchTable)),
            Import("host", "touch_global", function(store, functionType(emptyList(), emptyList()), touchGlobal)),
            Import("host", "relay", function(store, functionType(listOf(I32_TYPE), listOf(I32_TYPE)), relay(store))),
            Import("host", "types", function(store, functionType(emptyList(), listOf(I32_TYPE)), types)),
            Import("env", "shared_memory", memory(store, memoryType())),
            Import("env", "shared_global", sharedGlobal),
            Import("env", "shared_table", table(store, TableType(AddressType.I32, ReferenceType.RefNull(AbstractHeapType.Func), Limits(2u)), null)),
            Import("env", "shared_tag", tag(store, TagType(TagType.Attribute.Exception, 0, functionType(listOf(I32_TYPE), emptyList())))),
        )

        return runBlocking { instance(store, module, imports) }.expect("instance")
    }

    private val touchMemory = HostFunction { _, _ ->
        withMemory(1) {
            writeI32(0, readI32(0) + 1)
            write(8, byteArrayOf(1, 2, 3))
            fill(16, 9, 4)
            copy(8, 32, 3)
            move(32, 33, 3)
            writeUtf8String(64, "hi")
        }
    }

    private val touchTable = HostFunction { _, _ ->
        withTable(1) {
            write(1, read(0))
            check(size == 4)
        }
    }

    private val touchGlobal = HostFunction { _, _ ->
        withGlobal(1) {
            rawValue += 5
        }
    }

    private fun relay(store: Store): HostFunction {
        return HostFunction { parameters, results ->
            val module = contextOf<HostModuleInstance>()
            val thrown = invoke(store, module.instance, "fail", listOf(I32(parameters.readI32(0))))
            val exception = assertIs<ExecutionError.Thrown<ExceptionReference>>(error(thrown)).exception
            check(!hasPending)
            results.writeI32(0, 0)
            raise(exception)
        }
    }

    private val types = HostFunction { _, results ->
        results.writeI32(0, structFieldCount(runtimeType(0)))
    }

    private fun call(
        store: Store,
        instance: Instance,
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> {
        return invoke(store, instance, name, args.toList()).expect(name)
    }

    private fun error(result: ChasmResult<*, *>): Any? {
        return assertIs<ChasmResult.Error<*>>(result).error
    }

    private fun memoryType(): MemoryType {
        return MemoryType(AddressType.I32, Limits(1u, 2u), SharedStatus.Unshared)
    }

    private fun functionType(
        parameters: List<ValueType>,
        results: List<ValueType>,
    ): FunctionType {
        return FunctionType(ResultType(parameters), ResultType(results))
    }

    private companion object {
        const val PAGE_BYTES = 65_536
        const val INTERRUPT_DEADLINE = 10_000_000_000L
        const val INTERRUPT_JOIN = 10_000L
        val I32_TYPE = ValueType.Number(NumberType.I32)
    }
}
