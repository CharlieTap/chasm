package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class GarbageCollectorTest {

    @Test
    fun `can sweep an unreferenced struct`() {
        val store = store()
        val runtimeType = store.registerStruct(referenceFields = emptySet())
        val reference = store.heap.allocateStruct(runtimeType, LongArray(0))

        assertEquals(Ok(Unit), GarbageCollector(store, vstack()))
        assertDead(store, reference)
    }

    @Test
    fun `can sweep an unreferenced array`() {
        val store = store()
        val (_, reference) = store.allocateArray(
            ValueType.Number(NumberType.I64),
            LongArray(0),
        )

        assertEquals(Ok(Unit), GarbageCollector(store, vstack()))
        assertArrayDead(store, reference)
    }

    @Test
    fun `preserves a struct referenced in the stack`() {
        val store = store()
        val runtimeType = store.registerStruct(referenceFields = emptySet())
        val reference = store.heap.allocateStruct(runtimeType, LongArray(0))
        val stack = vstack().apply { push(reference) }

        assertEquals(Ok(Unit), GarbageCollector(store, stack))
        assertLive(store, runtimeType, reference)
    }

    @Test
    fun `preserves an array referenced in the stack`() {
        val store = store()
        val (runtimeType, reference) = store.allocateArray(
            ValueType.Number(NumberType.I64),
            LongArray(0),
        )

        assertEquals(Ok(Unit), GarbageCollector(store, vstack(listOf(arrayReference(reference)))))
        assertArrayLive(store, runtimeType, reference)
    }

    @Test
    fun `preserves a struct referenced in an array`() {
        val store = store()
        val runtimeType = store.registerStruct(referenceFields = emptySet())
        val structReference = store.heap.allocateStruct(runtimeType, LongArray(0))
        val (arrayRuntimeType, rawArrayReference) = store.allocateArray(
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
            longArrayOf(structReference),
        )

        assertEquals(Ok(Unit), GarbageCollector(store, vstack(listOf(arrayReference(rawArrayReference)))))
        assertArrayLive(store, arrayRuntimeType, rawArrayReference)
        assertLive(store, runtimeType, structReference)
    }

    @Test
    fun `preserves an array referenced in a struct field`() {
        val store = store()
        val (arrayRuntimeType, rawArrayReference) = store.allocateArray(
            ValueType.Number(NumberType.I64),
            LongArray(0),
        )
        val runtimeType = store.registerStruct(referenceFields = setOf(0))
        val structReference = store.heap.allocateStruct(runtimeType, longArrayOf(rawArrayReference))

        assertEquals(Ok(Unit), GarbageCollector(store, vstack().apply { push(structReference) }))
        assertLive(store, runtimeType, structReference)
        assertArrayLive(store, arrayRuntimeType, rawArrayReference)
    }

    @Test
    fun `preserves objects referenced by an exception`() {
        val store = store()
        val (arrayRuntimeType, rawArrayReference) = store.allocateArray(
            ValueType.Number(NumberType.I64),
            LongArray(0),
        )
        val runtimeType = store.registerStruct(referenceFields = setOf(0))
        val structReference = store.heap.allocateStruct(runtimeType, longArrayOf(rawArrayReference))
        val tagAddress = store.heap.registerTag(
            RTT(0),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(referenceValueType())),
                ),
            ),
        )
        val exceptionReference = store.heap.allocateException(tagAddress, longArrayOf(structReference))

        assertEquals(Ok(Unit), GarbageCollector(store, vstack().apply { push(exceptionReference) }))
        assertLive(store, runtimeType, structReference)
        assertArrayLive(store, arrayRuntimeType, rawArrayReference)
    }

    @Test
    fun `can collect multiple unreferenced objects`() {
        val store = store()
        val runtimeType = store.registerStruct(referenceFields = emptySet())
        val structs = List(3) { store.heap.allocateStruct(runtimeType, LongArray(0)) }
        val arrayRuntimeType = store.registerArray(ValueType.Number(NumberType.I64)).first
        val arrays = List(3) { store.heap.allocateArrayFromElements(arrayRuntimeType, LongArray(0), 0, 0) }

        val stack = vstack(listOf(arrayReference(arrays[2]))).apply { push(structs[1]) }

        assertEquals(Ok(Unit), GarbageCollector(store, stack))
        assertDead(store, structs[0])
        assertLive(store, runtimeType, structs[1])
        assertDead(store, structs[2])
        assertArrayDead(store, arrays[0])
        assertArrayDead(store, arrays[1])
        assertArrayLive(store, arrayRuntimeType, arrays[2])
    }

    @Test
    fun `can collect unreferenced objects in a cyclic reference`() {
        val store = store()
        val arrayRuntimeType = store.registerArray(
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
        ).first
        val arrayReference = store.heap.allocateArrayFilled(arrayRuntimeType, 1, RV_TYPE_NULL)
        val runtimeType = store.registerStruct(referenceFields = setOf(0))
        val structReference = store.heap.allocateStruct(runtimeType, longArrayOf(arrayReference))
        store.heap.setArrayElement(arrayReference, 0, structReference)

        assertEquals(Ok(Unit), GarbageCollector(store, vstack()))
        assertDead(store, structReference)
        assertArrayDead(store, arrayReference)
    }

    @Test
    fun `only reference typed globals retain matching bit patterns`() {
        val numericStore = store()
        val numericType = numericStore.registerStruct(referenceFields = emptySet())
        val numericReference = numericStore.heap.allocateStruct(numericType, LongArray(0))
        numericStore.globals += globalInstance(value = numericReference)

        assertEquals(Ok(Unit), GarbageCollector(numericStore, null))
        assertDead(numericStore, numericReference)

        val referenceStore = store()
        val referenceType = referenceStore.registerStruct(referenceFields = emptySet())
        val reference = referenceStore.heap.allocateStruct(referenceType, LongArray(0))
        referenceStore.globals += globalInstance(
            type = globalType(referenceValueType()),
            value = reference,
        )
        assertEquals(Ok(Unit), GarbageCollector(referenceStore, null))
        assertLive(referenceStore, referenceType, reference)
    }

    @Test
    fun `deep array chain reaches a managed struct without recursion`() {
        val store = store()
        val runtimeType = store.registerStruct(referenceFields = emptySet())
        val structReference = store.heap.allocateStruct(runtimeType, LongArray(0))
        val arrayRuntimeType = store.registerArray(
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
        ).first
        val arrayCount = 10_000
        val arrays = LongArray(arrayCount) {
            store.heap.allocateArrayFilled(arrayRuntimeType, 1, RV_TYPE_NULL)
        }
        var arrayIndex = 0
        while (arrayIndex < arrayCount) {
            val nextReference = if (arrayIndex == arrayCount - 1) {
                structReference
            } else {
                arrays[arrayIndex + 1]
            }
            store.heap.setArrayElement(arrays[arrayIndex], 0, nextReference)
            arrayIndex++
        }

        val root = arrayReference(arrays[0])
        assertEquals(Ok(Unit), GarbageCollector(store, vstack(listOf(root))))

        arrays.forEach { assertArrayLive(store, arrayRuntimeType, it) }
        assertLive(store, runtimeType, structReference)
    }

    @Test
    fun `numeric array bit patterns do not retain structs but reference arrays do`() {
        val numericStore = store()
        val numericType = numericStore.registerStruct(referenceFields = emptySet())
        val numericStruct = numericStore.heap.allocateStruct(numericType, LongArray(0))
        val numericArrayType = numericStore.registerArray(
            ValueType.Number(NumberType.I64),
        ).first
        val numericArray = numericStore.heap.allocateArrayFromElements(
            numericArrayType,
            longArrayOf(numericStruct),
            0,
            1,
        )

        assertEquals(
            Ok(Unit),
            GarbageCollector(numericStore, vstack(listOf(arrayReference(numericArray)))),
        )
        assertDead(numericStore, numericStruct)

        val referenceStore = store()
        val referenceType = referenceStore.registerStruct(referenceFields = emptySet())
        val referencedStruct = referenceStore.heap.allocateStruct(referenceType, LongArray(0))
        val referenceArrayType = referenceStore.registerArray(
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
        ).first
        val referenceArray = referenceStore.heap.allocateArrayFromElements(
            referenceArrayType,
            longArrayOf(referencedStruct),
            0,
            1,
        )

        assertEquals(
            Ok(Unit),
            GarbageCollector(referenceStore, vstack(listOf(arrayReference(referenceArray)))),
        )
        assertLive(referenceStore, referenceType, referencedStruct)
    }

    private fun Store.registerStruct(referenceFields: Set<Int>): RTT {
        val fieldCount = (referenceFields.maxOrNull() ?: -1) + 1
        val fields = List(fieldCount) { fieldIndex ->
            val valueType = if (fieldIndex in referenceFields) {
                ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))
            } else {
                ValueType.Number(NumberType.I64)
            }
            FieldType(StorageType.Value(valueType), Mutability.Var)
        }
        val types = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(StructType(fields)),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )
        return heap.registerRuntimeTypes(types)[0]
    }

    private fun Store.registerArray(valueType: ValueType): Pair<RTT, ArrayType> {
        val arrayType = ArrayType(FieldType(StorageType.Value(valueType), Mutability.Var))
        val types = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(arrayType),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )
        return heap.registerRuntimeTypes(types)[0] to arrayType
    }

    private fun assertLive(
        store: Store,
        runtimeType: RTT,
        reference: Long,
    ) {
        assertEquals(runtimeType.value, store.heap.structRuntimeTypeIdOrNegative(reference))
    }

    private fun assertDead(
        store: Store,
        reference: Long,
    ) {
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(reference))
    }

    private fun assertArrayLive(
        store: Store,
        runtimeType: RTT,
        reference: Long,
    ) {
        assertEquals(runtimeType.value, store.heap.arrayRuntimeTypeIdOrNegative(reference))
    }

    private fun assertArrayDead(
        store: Store,
        reference: Long,
    ) {
        assertEquals(-1, store.heap.arrayRuntimeTypeIdOrNegative(reference))
    }

    private fun structReference(rawReference: Long): ReferenceValue.Struct {
        val address = (rawReference shr RV_SHIFT_BITS).toInt()
        assertEquals(RV_TYPE_STRUCT, rawReference and 0xff)
        return ReferenceValue.Struct(Address.Struct(address))
    }

    private fun arrayReference(rawReference: Long): ReferenceValue.Array {
        val address = (rawReference shr RV_SHIFT_BITS).toInt()
        assertEquals(RV_TYPE_ARRAY, rawReference and 0xff)
        return ReferenceValue.Array(Address.Array(address))
    }

    private fun Store.allocateArray(
        valueType: ValueType,
        fields: LongArray,
    ): Pair<RTT, Long> {
        val runtimeType = registerArray(valueType).first
        return runtimeType to heap.allocateArrayFromElements(runtimeType, fields, 0, fields.size)
    }
}
