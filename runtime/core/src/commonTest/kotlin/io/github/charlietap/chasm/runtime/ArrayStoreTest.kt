package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArrayStoreTest {

    @Test
    fun `registration copies array metadata before allocation`() {
        val field = numericField(Mutability.Var)
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(field))[0]
        field.mutability = Mutability.Const
        field.storageType = StorageType.Packed(PackedType.I8)

        val reference = store.heap.allocateArrayFromElements(runtimeType, longArrayOf(17, 18), 0, 2)
        val copiedField = store.heap.arrayFieldType(reference)

        assertEquals(Mutability.Var, copiedField.mutability)
        assertEquals(StorageType.Value(ValueType.Number(NumberType.I64)), copiedField.storageType)
        assertEquals(runtimeType.value, store.heap.arrayRuntimeTypeIdOrNegative(reference))
        assertEquals(2, store.heap.arrayLength(reference))
        assertEquals(17, store.heap.getArrayElement(reference, 0))
        assertEquals(18, store.heap.getArrayElement(reference, 1))
    }

    @Test
    fun `checked access preserves array trap ordering`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val reference = store.heap.allocateArrayFilled(runtimeType, 1, 31)

        val wrongKind = assertFailsWith<InvocationException> {
            store.heap.getArrayElement(RV_TYPE_STRUCT, 0)
        }
        assertEquals(InvocationError.ArrayReferenceExpected, wrongKind.error)

        val invalidIndex = assertFailsWith<InvocationException> {
            store.heap.getArrayElement(reference, 1)
        }
        assertEquals(InvocationError.ArrayFieldLookupFailed(1), invalidIndex.error)

        store.heap.collectGarbage(store)
        val stale = assertFailsWith<InvocationException> {
            store.heap.getArrayElement(reference, 0)
        }
        assertEquals(
            InvocationError.ArrayLookupFailed(Address.Array((reference shr 8).toInt())),
            stale.error,
        )
    }

    @Test
    fun `trusted scalar access retains the mandatory array tag check`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val reference = store.heap.allocateArrayFilled(runtimeType, 1, 41)

        listOf(RV_TYPE_NULL, RV_TYPE_STRUCT).forEach { invalidReference ->
            val getFailure = assertFailsWith<InvocationException> {
                store.heap.getArrayElementTrusted(invalidReference, 0)
            }
            assertEquals(InvocationError.ArrayReferenceExpected, getFailure.error)

            val setFailure = assertFailsWith<InvocationException> {
                store.heap.setArrayElementTrusted(invalidReference, 0, 42)
            }
            assertEquals(InvocationError.ArrayReferenceExpected, setFailure.error)

            val lengthFailure = assertFailsWith<InvocationException> {
                store.heap.arrayLengthTrusted(invalidReference)
            }
            assertEquals(InvocationError.ArrayReferenceExpected, lengthFailure.error)
        }

        assertEquals(41, store.heap.getArrayElement(reference, 0))
    }

    @Test
    fun `stack and bulk seams preserve direct managed array semantics`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val stack = ValueStack().apply {
            push(0)
            push(1)
            push(2)
            push(3)
            push(4)
            push(5)
        }

        store.heap.allocateArrayFromStack(runtimeType, 6, stack)
        val reference = stack.pop()
        store.heap.fillArray(reference, 1, 2, 9)
        store.heap.copyArray(reference, 0, reference, 2, 4)

        assertContentEquals(
            longArrayOf(0, 9, 0, 9, 9, 3),
            LongArray(6) { store.heap.getArrayElement(reference, it) },
        )

        val dedicated = store.heap.allocateArrayFilled(runtimeType, 1024, 0)
        store.heap.setArrayElementTrusted(dedicated, 1023, 77)
        assertEquals(1024, store.heap.arrayLengthTrusted(dedicated))
        assertEquals(77, store.heap.getArrayElementTrusted(dedicated, 1023))
    }

    @Test
    fun `frame allocation copies before overlapping publication`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val stack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(0, 11)
            setFrameSlot(1, 22)
            setFrameSlot(2, 33)
        }

        store.heap.allocateArrayFromFrame(runtimeType, 0, 3, 0, stack)
        val reference = stack.getFrameSlot(0)

        assertContentEquals(
            longArrayOf(11, 22, 33),
            LongArray(3) { store.heap.getArrayElement(reference, it) },
        )
        assertEquals(22, stack.getFrameSlot(1))
        assertEquals(33, stack.getFrameSlot(2))
    }

    @Test
    fun `data allocation and initialization preserve exact scalar bits`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val source = ubyteArrayOf(
            0x00u,
            0x00u,
            0x00u,
            0x80u,
            0x78u,
            0x56u,
            0x34u,
            0x12u,
        )

        val reference = store.heap.allocateArrayFromData(runtimeType, source, 0, 2, 4)
        assertEquals(Int.MIN_VALUE.toLong(), store.heap.getArrayElement(reference, 0))
        assertEquals(0x12345678L, store.heap.getArrayElement(reference, 1))

        store.heap.initializeArrayFromData(reference, 1, ubyteArrayOf(0xFFu, 0xFFu), 0, 1, 2)
        assertEquals(-1L, store.heap.getArrayElement(reference, 1))
    }

    @Test
    fun `managed arrays cross every ordinary size class and the dedicated cutoff`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val capacities = (0..96).toList() + listOf(128, 192, 256, 384, 512, 768, 1023)
        var previousCapacity = -1

        capacities.forEach { capacity ->
            listOf(previousCapacity + 1, capacity).forEach { length ->
                val reference = store.heap.allocateArrayFilled(runtimeType, length, length.toLong())
                val roots = ValueStack().apply { push(reference) }

                store.heap.collectGarbage(store, roots)

                assertEquals(length, store.heap.arrayLength(reference))
                if (length > 0) {
                    assertEquals(length.toLong(), store.heap.getArrayElement(reference, 0))
                    assertEquals(length.toLong(), store.heap.getArrayElement(reference, length - 1))
                    store.heap.setArrayElement(reference, length - 1, -length.toLong())
                    assertEquals(-length.toLong(), store.heap.getArrayElement(reference, length - 1))
                }
            }
            previousCapacity = capacity
        }

        val dedicated = store.heap.allocateArrayFilled(runtimeType, 1024, 1024)
        store.heap.collectGarbage(store, ValueStack().apply { push(dedicated) })
        assertEquals(1024, store.heap.arrayLength(dedicated))
        assertEquals(1024, store.heap.getArrayElement(dedicated, 0))
        assertEquals(1024, store.heap.getArrayElement(dedicated, 1023))
    }

    @Test
    fun `same array overlap remains correct across collections`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(arrayTypes(numericField(Mutability.Var)))[0]
        val reference = store.heap.allocateArrayFromElements(runtimeType, LongArray(8) { it.toLong() }, 0, 8)
        val roots = ValueStack().apply { push(reference) }

        store.heap.collectGarbage(store, roots)
        store.heap.copyArray(reference, 0, reference, 2, 6)
        assertContentEquals(
            longArrayOf(0, 1, 0, 1, 2, 3, 4, 5),
            LongArray(8) { store.heap.getArrayElement(reference, it) },
        )

        store.heap.copyArray(reference, 2, reference, 0, 6)
        store.heap.collectGarbage(store, roots)
        assertContentEquals(
            longArrayOf(0, 1, 2, 3, 4, 5, 4, 5),
            LongArray(8) { store.heap.getArrayElement(reference, it) },
        )
    }

    @Test
    fun `managed collection ignores stale reference slack after size class reuse`() {
        val store = Store()
        val runtimeTypes = store.heap.registerRuntimeTypes(referenceArrayAndStructTypes())
        val structRuntimeType = runtimeTypes[0]
        val arrayRuntimeType = runtimeTypes[1]
        val staleStruct = store.heap.allocateStruct(structRuntimeType, longArrayOf())
        val arrays = LongArray(15) {
            store.heap.allocateArrayFilled(arrayRuntimeType, 128, staleStruct)
        }
        val poisoned = arrays[7]
        val initialRoots = ValueStack().apply {
            push(staleStruct)
            arrays.forEachIndexed { index, reference ->
                if (index != 7) push(reference)
            }
        }

        store.heap.collectGarbage(store, initialRoots)
        val shorter = store.heap.allocateArrayFilled(arrayRuntimeType, 97, RV_TYPE_NULL)
        assertEquals(poisoned, shorter)

        store.heap.collectGarbage(store, ValueStack().apply { push(shorter) })

        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(staleStruct))
        assertEquals(RV_TYPE_NULL, store.heap.getArrayElement(shorter, 0))
        assertEquals(RV_TYPE_NULL, store.heap.getArrayElement(shorter, 96))
        val outOfBounds = assertFailsWith<InvocationException> {
            store.heap.getArrayElement(shorter, 97)
        }
        assertEquals(InvocationError.ArrayFieldLookupFailed(97), outOfBounds.error)
    }

    private companion object {
        fun numericField(mutability: Mutability) = FieldType(
            StorageType.Value(ValueType.Number(NumberType.I64)),
            mutability,
        )

        fun arrayTypes(field: FieldType): List<DefinedType> = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(ArrayType(field)),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )

        fun referenceArrayAndStructTypes(): List<DefinedType> = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(StructType(emptyList())),
                        ),
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Array(
                                ArrayType(
                                    FieldType(
                                        StorageType.Value(
                                            ValueType.Reference(
                                                ReferenceType.RefNull(AbstractHeapType.Any),
                                            ),
                                        ),
                                        Mutability.Var,
                                    ),
                                ),
                            ),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )
    }
}
