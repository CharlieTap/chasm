package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.embedding.addFuel
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.interrupt
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.copyMemory
import io.github.charlietap.chasm.embedding.memory.fillMemory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.memoryByteSize
import io.github.charlietap.chasm.embedding.memory.moveMemory
import io.github.charlietap.chasm.embedding.memory.readByte
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.readDouble
import io.github.charlietap.chasm.embedding.memory.readFloat
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.memory.readLong
import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.readShort
import io.github.charlietap.chasm.embedding.memory.readUtf8String
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.memory.writeByte
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.memory.writeDouble
import io.github.charlietap.chasm.embedding.memory.writeFloat
import io.github.charlietap.chasm.embedding.memory.writeInt
import io.github.charlietap.chasm.embedding.memory.writeLong
import io.github.charlietap.chasm.embedding.memory.writeShort
import io.github.charlietap.chasm.embedding.memory.writeUtf8String
import io.github.charlietap.chasm.embedding.reference.arrayElementInfo
import io.github.charlietap.chasm.embedding.reference.arrayType
import io.github.charlietap.chasm.embedding.reference.exceptionTag
import io.github.charlietap.chasm.embedding.reference.isSubtype
import io.github.charlietap.chasm.embedding.reference.readPayload
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.reference.structFieldCount
import io.github.charlietap.chasm.embedding.reference.structFieldInfo
import io.github.charlietap.chasm.embedding.reference.structType
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.resetFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.moveTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue.F32
import io.github.charlietap.chasm.embedding.value.NumberValue.F64
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.NumberValue.I64
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.copy
import io.github.charlietap.chasm.host.fill
import io.github.charlietap.chasm.host.hasPending
import io.github.charlietap.chasm.host.move
import io.github.charlietap.chasm.host.raise
import io.github.charlietap.chasm.host.rawValue
import io.github.charlietap.chasm.host.read
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.runtimeType
import io.github.charlietap.chasm.host.size
import io.github.charlietap.chasm.host.structFieldCount
import io.github.charlietap.chasm.host.withGlobal
import io.github.charlietap.chasm.host.withMemory
import io.github.charlietap.chasm.host.withTable
import io.github.charlietap.chasm.host.write
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.host.writeUtf8String
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
import io.github.charlietap.chasm.config.Limits as RuntimeLimits

class EmbeddingApiTest {

    @Test
    fun memoryCallsReadAndWriteThroughTheCatalogueAndTheHost() {

        val (_, instance) = instantiate()
        val memory = checkNotNull(exports(instance).memory("memory"))

        writeInt(memory, 0, 41)
        call(instance, "call_touch_memory")

        assertEquals(42, readInt(memory, 0))
        assertEquals(42, call(instance, "load", I32(0)).single().let { value -> (value as I32).value })
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

        val (_, instance) = instantiate()
        val table = checkNotNull(exports(instance).table("table"))
        val inc = (call(instance, "inc_ref").single() as ReferenceValue.Function).reference

        writeTable(table, 0, inc)
        call(instance, "call_touch_table")
        moveTable(table, 0, table, 2, 2)

        assertEquals(I32(6), call(instance, "call_slot", I32(1), I32(5)).single())
        assertSame(inc, readTable(table, 3))
    }

    @Test
    fun globalsThroughTheCatalogueAndTheHost() {

        val (_, instance) = instantiate()
        val counter = checkNotNull(exports(instance).global("counter"))

        call(instance, "call_touch_global")

        assertEquals(15L, readGlobal(counter))
    }

    @Test
    fun createdResourcesLinkAsImports() {

        val (store, instance) = instantiate()

        writeGlobal(sharedGlobal, 9)

        assertEquals(I64(9), call(instance, "shared").single())
        assertFailsWith<IllegalArgumentException> { global(store, GlobalType(ValueType.Number(NumberType.I32), Mutability.Var), F64(1.0)) }
    }

    @Test
    fun gcTypeQueriesAnswerFromTheStoreAndTheHost() {

        val (store, instance) = instantiate()
        val box = structType(call(instance, "box", I32(1)).single().let { value -> (value as ReferenceValue.Struct).reference })
        val bigBox = structType(call(instance, "big_box").single().let { value -> (value as ReferenceValue.Struct).reference })
        val bytes = arrayType(call(instance, "bytes", I32(2)).single().let { value -> (value as ReferenceValue.Array).reference })

        assertEquals(2, structFieldCount(store, box))
        assertTrue(structFieldInfo(store, box, 1).isPackedI8)
        assertTrue(structFieldInfo(store, box, 0).mutable)
        assertTrue(arrayElementInfo(store, bytes).isPackedI8)
        assertTrue(isSubtype(store, bigBox, box))
        assertFalse(isSubtype(store, box, bigBox))
        assertEquals(I32(2), call(instance, "call_types").single())
    }

    @Test
    fun hostsReRaiseExceptionsFromNestedCalls() {

        val (_, instance) = instantiate()

        assertEquals(I32(4), call(instance, "relay_caught", I32(4)).single())
    }

    @Test
    fun boxedCallsConvertValuesByType() {

        val (_, instance) = instantiate()

        assertIs<ExecutionError.Rejected>(error(invoke(instance, "inc", emptyList())))
        assertIs<ExecutionError.Rejected>(error(invoke(instance, "inc", listOf(F32(1f)))))
        assertEquals(ReferenceValue.Null(AbstractHeapType.NoFunc), call(instance, "null_func").single())
        assertEquals(ReferenceValue.Null(AbstractHeapType.NoException), call(instance, "null_exn").single())
        assertEquals(5, (call(instance, "i31", I32(5)).single() as ReferenceValue.I31).reference.value)
        assertIs<FunctionReference>((call(instance, "inc_ref").single() as ReferenceValue.Function).reference)
        assertIs<I31Reference>((call(instance, "i31", I32(1)).single() as ReferenceValue.I31).reference)
    }

    @Test
    fun fuelIsMeteredThroughStoreControl() {

        val store = dexStore(RuntimeConfig(limits = RuntimeLimits(cpu = CpuLimits.Metered(0.fuel))))

        addFuel(store, 1_000)
        val added = remainingFuel(store)
        resetFuel(store)

        assertEquals(1_000L, added)
        assertEquals(0L, remainingFuel(store))
        assertFailsWith<IllegalArgumentException> { addFuel(store, -1) }
        assertFailsWith<IllegalStateException> { remainingFuel(dexStore()) }
    }

    @Test
    fun interruptsStopRunningGuests() {

        val (store, instance) = instantiate(RuntimeConfig(interrupts = true))
        var result: ChasmResult<List<ExecutionValue>, *>? = null
        val guest = Thread { result = invoke(instance, "spin") }

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
        val box = (call(instance, "box", I32(1)).single() as ReferenceValue.Struct).reference
        val failed = assertIs<ExecutionError.Thrown>(error(invoke(instance, "fail", listOf(I32(2))))).exception

        dropStore(store)

        assertFailsWith<IllegalStateException> { readStructField(box, 0) }
        assertFailsWith<IllegalStateException> { readPayload(failed, 0) }
    }

    @Test
    fun functionReferencesOfHandlesAreTheGuestsReferences() {

        val (store, instance) = instantiate()
        val inc = checkNotNull(exports(instance).function("inc"))
        val guest = (call(instance, "inc_ref").single() as ReferenceValue.Function).reference
        val host = function(store, functionType(emptyList(), emptyList()), touchGlobal)

        assertSame(guest, functionReference(inc))
        assertSame(functionReference(host), functionReference(host))
        assertSame(store._engine(), functionReference(host).engine())
    }

    @Test
    fun exceptionsOfOneTagShareItsHandle() {

        val (store, instance) = instantiate()
        val failed = checkNotNull(exports(instance).tag("failed"))
        val first = assertIs<ExecutionError.Thrown>(error(invoke(instance, "fail", listOf(I32(1))))).exception
        val second = assertIs<ExecutionError.Thrown>(error(invoke(instance, "fail", listOf(I32(2))))).exception

        assertEquals(failed, exceptionTag(first))
        assertSame(exceptionTag(first), exceptionTag(second))
        assertSame(store._engine(), first.engine())
    }

    @Test
    fun modulesNotDecodedByModuleAreUnsupported() {

        val store = dexStore()
        val module = _module(emptyList(), emptyList(), Any())

        val result = runBlocking { instance(store, module) }

        assertIs<ExecutionError.Unsupported>(error(result))
    }

    @Test
    fun compiledModulesAreAlreadyValid() {

        val module = dexModule("embedding_api")

        assertSame(module, runBlocking { validate(module) }.expect("validate"))
    }

    @Test
    fun moduleExportsCarryTheirIndexInTheModule() {

        val module = dexModule("embedding_api")
        val indices = module.exports.associate { export -> export.name to export.index }

        assertEquals(ModuleIndex.MemoryIndex(1), indices["memory"])
        assertEquals(ModuleIndex.TableIndex(1), indices["table"])
        assertEquals(ModuleIndex.GlobalIndex(1), indices["counter"])
        assertEquals(ModuleIndex.TagIndex(1), indices["failed"])
        assertEquals(ModuleIndex.FunctionIndex(5), indices["inc"])
    }

    @Test
    fun moduleImportsCarryTheirIndexInTheModule() {

        val module = dexModule("embedding_api")
        val indices = module.imports.associate { import -> import.entityName to import.index }

        assertEquals(ModuleIndex.FunctionIndex(0), indices["touch_memory"])
        assertEquals(ModuleIndex.FunctionIndex(4), indices["types"])
        assertEquals(ModuleIndex.MemoryIndex(0), indices["shared_memory"])
        assertEquals(ModuleIndex.GlobalIndex(0), indices["shared_global"])
        assertEquals(ModuleIndex.TableIndex(0), indices["shared_table"])
        assertEquals(ModuleIndex.TagIndex(0), indices["shared_tag"])
    }

    private lateinit var sharedGlobal: Global

    private fun instantiate(config: RuntimeConfig = RuntimeConfig()): Pair<Store, Instance> {

        val module = dexModule("embedding_api", config)
        val store = store(module)

        return store to instantiate(store, module)
    }

    private fun instantiate(
        store: Store,
        module: Module,
    ): Instance {

        sharedGlobal = global(store, GlobalType(ValueType.Number(NumberType.I64), Mutability.Var), I64(3))
        val imports = listOf(
            Import("host", "touch_memory", function(store, functionType(emptyList(), emptyList()), touchMemory)),
            Import("host", "touch_table", function(store, functionType(emptyList(), emptyList()), touchTable)),
            Import("host", "touch_global", function(store, functionType(emptyList(), emptyList()), touchGlobal)),
            Import("host", "relay", function(store, functionType(listOf(I32_TYPE), listOf(I32_TYPE)), relay)),
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

    private val relay = HostFunction { parameters, results ->
        val module = contextOf<HostModuleInstance>()
        val thrown = invoke(module.instance, "fail", listOf(I32(parameters.readI32(0))))
        val exception = assertIs<ExecutionError.Thrown>(error(thrown)).exception
        check(!hasPending)
        results.writeI32(0, 0)
        raise(exception)
    }

    private val types = HostFunction { _, results ->
        results.writeI32(0, structFieldCount(runtimeType(0)))
    }

    private fun call(
        instance: Instance,
        name: String,
        vararg args: ExecutionValue,
    ): List<ExecutionValue> {
        return invoke(instance, name, args.toList()).expect(name)
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
