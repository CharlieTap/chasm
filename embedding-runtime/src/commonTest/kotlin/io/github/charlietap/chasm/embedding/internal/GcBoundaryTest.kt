package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.fixture.GC_BOUNDARY_MODULE
import io.github.charlietap.chasm.embedding.fixture.collectPlatformGarbage
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerArrayType
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.reference.allocateNumericArray
import io.github.charlietap.chasm.embedding.reference.allocateStruct
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.reference.runtimeType
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.readReference
import io.github.charlietap.chasm.host.writeReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import io.github.charlietap.chasm.config.Limits as RuntimeLimits

class GcBoundaryTest {

    @Test
    fun `a prepared call accepts a struct of the parameter's defined type and rejects an unrelated one`() = runTest {

        val store = interpreterStore()
        val instance = boundaryInstance(store)
        val point = allocateStruct(store, runtimeType(instance, POINT), longArrayOf(1L), arrayOfNulls(1))
        val other = allocateStruct(store, runtimeType(instance, OTHER), longArrayOf(2L), arrayOfNulls(1))

        val accepted = invoke(instance, "point_identity", listOf(ReferenceValue.Struct(point))).expect("point_identity runs")
        val rejected = invoke(instance, "point_identity", listOf(ReferenceValue.Struct(other)))

        assertEquals(point, assertIs<ReferenceValue.Struct>(accepted.single()).reference)
        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<*>>(rejected).error)
    }

    @Test
    fun `a host function returning a struct of an unrelated defined type fails the call`() = runTest {

        val store = interpreterStore()
        lateinit var instance: Instance
        val makePoint = HostFunction { _, results ->
            results.writeReference(0, allocateStruct(store, runtimeType(instance, OTHER), longArrayOf(2L), arrayOfNulls(1)))
        }
        instance = boundaryInstance(store, makePoint = makePoint)

        val result = invoke(instance, "call_make_point")

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun `extern convert any round trips a struct to the same guest object`() = runTest {

        val store = interpreterStore()
        val instance = boundaryInstance(store)
        val point = allocateStruct(store, runtimeType(instance, POINT), longArrayOf(117L), arrayOfNulls(1))

        val externalized = invoke(instance, "externalize", listOf(ReferenceValue.Struct(point))).expect("externalize runs")
        val extern = assertIs<ReferenceValue.Extern>(externalized.single()).value
        val internalized = invoke(instance, "internalize", listOf(ReferenceValue.Extern(extern))).expect("internalize runs")

        assertEquals(point, extern)
        assertEquals(point, assertIs<ReferenceValue.Struct>(internalized.single()).reference)
    }

    @Test
    fun `a struct returned through extern convert any stays alive once the original reference is gone`() = runTest {

        val store = interpreterStore()
        val instance = boundaryInstance(store)
        val extern = externalizedPoint(store, instance, 117L)

        collectPlatformGarbage()
        collect(store)

        assertEquals(117L, readStructField(assertIs<StructReference>(extern), 0))
    }

    @Test
    fun `preparing a function with a v128 signature is rejected`() = runTest {

        val store = interpreterStore()
        val instance = boundaryInstance(store)

        val prepared = prepareFunction(instance, "vector")

        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<*>>(prepared).error)
    }

    @Test
    fun `a host call at a deep stack offset decodes and encodes its references`() = runTest {

        val store = interpreterStore()
        val offsets = mutableListOf<Int>()
        val echo = HostFunction { parameters, results ->
            offsets += parameters
            results.writeReference(0, parameters.readReference(0))
        }
        val instance = boundaryInstance(store, echo = echo)

        repeat(ROUNDS) {

            val argument = Any()

            val result = invoke(instance, "deep_echo", listOf(ReferenceValue.Extern(argument))).expect("deep_echo runs")

            assertSame(argument, assertIs<ReferenceValue.Extern>(result.single()).value)
        }
        assertTrue(offsets.all { offset -> offset > DEEP_LOCALS }, "offsets $offsets")
    }

    @Test
    fun `host allocation inside a call collects when the heap is full`() = runTest {

        val runtime = store(config = RuntimeConfig(limits = RuntimeLimits(memory = MemoryLimits(gcHeap = Bytes(GC_HEAP_BYTES)))))
        val store = interpreterStore(runtime)
        val externref = ReferenceType.RefNull(AbstractHeapType.Extern)
        val table = table(store, TableType(AddressType.I32, externref, Limits(2u)), null)
        val arrayType = registerArrayType(runtime, FieldType(StorageType.Value(ValueType.Number(NumberType.I64)), Mutability.Var))
        runtime.heap.allocateArrayFilled(RTT(arrayType.id), 1, 0L)
        val words = mutableListOf<Long>()
        val burn = HostFunction { _, _ ->
            writeTable(table, 0, "released")
            words += runtime.tables.single().elements[0]
            writeTable(table, 0, null)
            allocateNumericArray(store, arrayType, ARRAY_LENGTH, 0L)
            writeTable(table, 1, "replacement")
            words += runtime.tables.single().elements[1]
        }
        val instance = boundaryInstance(store, burn = burn)

        invoke(instance, "call_burn").expect("call_burn runs")

        assertEquals(words[0], words[1])
    }

    @Test
    fun `host allocation inside a call collects once the heap passes the collection threshold`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val externref = ReferenceType.RefNull(AbstractHeapType.Extern)
        val table = table(store, TableType(AddressType.I32, externref, Limits(2u)), null)
        val arrayType = registerArrayType(runtime, FieldType(StorageType.Value(ValueType.Number(NumberType.I64)), Mutability.Var))
        val garbage = runtime.heap.allocateArrayFilled(RTT(arrayType.id), COLLECTION_THRESHOLD_LENGTH, 0L)
        val words = mutableListOf<Long>()
        val burn = HostFunction { _, _ ->
            writeTable(table, 0, "released")
            words += runtime.tables.single().elements[0]
            writeTable(table, 0, null)
            allocateNumericArray(store, arrayType, ARRAY_LENGTH, 0L)
            writeTable(table, 1, "replacement")
            words += runtime.tables.single().elements[1]
        }
        val instance = boundaryInstance(store, burn = burn)

        invoke(instance, "call_burn").expect("call_burn runs")

        assertEquals(-1, runtime.heap.arrayRuntimeTypeIdOrNegative(garbage))
        assertEquals(words[0], words[1])
    }

    private suspend fun externalizedPoint(
        store: Store,
        instance: Instance,
        value: Long,
    ): Any {

        val point = allocateStruct(store, runtimeType(instance, POINT), longArrayOf(value), arrayOfNulls(1))
        val externalized = invoke(instance, "externalize", listOf(ReferenceValue.Struct(point))).expect("externalize runs")

        return assertIs<ReferenceValue.Extern>(externalized.single()).value
    }

    private suspend fun boundaryInstance(
        store: Store,
        echo: HostFunction = HostFunction { parameters, results -> results.writeReference(0, parameters.readReference(0)) },
        makePoint: HostFunction = HostFunction { _, results -> results.writeReference(0, null) },
        burn: HostFunction = HostFunction { _, _ -> },
    ): Instance {

        val externref = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern))
        val point = ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(pointType())))
        val imports = listOf(
            Import("host", "echo", function(store, FunctionType(ResultType(listOf(externref)), ResultType(listOf(externref))), echo)),
            Import("host", "make_point", function(store, FunctionType(ResultType(emptyList()), ResultType(listOf(point))), makePoint)),
            Import("host", "burn", function(store, FunctionType(ResultType(emptyList()), ResultType(emptyList())), burn)),
        )

        return instantiate(store, GC_BOUNDARY_MODULE, imports)
    }

    private fun pointType(): DefinedType {

        val field = FieldType(StorageType.Value(ValueType.Number(NumberType.I32)), Mutability.Var)
        val subType = SubType.Final(emptyList(), CompositeType.Struct(StructType(listOf(field))))

        return DefinedTypeFactory(listOf(RecursiveType(listOf(subType), RecursiveType.State.SYNTAX))).single()
    }

    private companion object {
        const val POINT = 0
        const val OTHER = 1
        const val ROUNDS = 100
        const val DEEP_LOCALS = 200
        const val ARRAY_LENGTH = 64
        const val GC_HEAP_BYTES = 16L * 1024L
        const val COLLECTION_THRESHOLD_LENGTH = 8 * 1024 * 1024 / Long.SIZE_BYTES
    }
}
