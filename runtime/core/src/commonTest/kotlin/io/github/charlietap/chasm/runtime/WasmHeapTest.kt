package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext as executionContextFixture

class WasmHeapTest {

    @Test
    fun `an allocation past the collection threshold collects first`() {

        val store = Store()
        val structType = store.heap.registerRuntimeType(definedType(CompositeType.Struct(StructType(emptyList()))))
        val arrayType = store.heap.registerRuntimeType(definedType(CompositeType.Array(ArrayType(numberField()))))
        val context = executionContextFixture(store = store)
        val garbage = store.heap.allocateArrayFilled(arrayType, COLLECTION_THRESHOLD_LENGTH, 0L)

        val struct = store.heap.allocateStruct(context, structType, LongArray(0))

        assertEquals(-1, store.heap.arrayRuntimeTypeIdOrNegative(garbage))
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(struct))
    }

    @Test
    fun `an allocation into a full limited heap collects before failing`() {

        val store = Store(config = RuntimeConfig(limits = Limits(memory = MemoryLimits(gcHeap = Bytes(PAGE_BYTES)))))
        val structType = store.heap.registerRuntimeType(definedType(CompositeType.Struct(StructType(emptyList()))))
        val arrayType = store.heap.registerRuntimeType(definedType(CompositeType.Array(ArrayType(numberField()))))
        val context = executionContextFixture(store = store)
        store.heap.allocateStruct(structType, LongArray(0))

        val array = store.heap.allocateArrayFilled(context, arrayType, 1, 0L)

        assertNotEquals(-1, store.heap.arrayRuntimeTypeIdOrNegative(array))
    }

    private fun numberField(): FieldType {
        return FieldType(StorageType.Value(ValueType.Number(NumberType.I64)), Mutability.Var)
    }

    private fun definedType(compositeType: CompositeType): DefinedType {

        val subType = SubType.Final(emptyList(), compositeType)

        return DefinedTypeFactory(listOf(RecursiveType(listOf(subType), RecursiveType.State.SYNTAX))).single()
    }

    private companion object {
        const val PAGE_BYTES = 16L * 1024L
        const val COLLECTION_THRESHOLD_LENGTH = 8 * 1024 * 1024 / Long.SIZE_BYTES
    }
}
