package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
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
import kotlin.test.assertFailsWith

class StructStoreTest {

    @Test
    fun `registration copies struct metadata before allocation`() {
        val field = numericField(Mutability.Var)
        val types = structTypes(listOf(field))
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(types)[0]
        field.mutability = Mutability.Const
        field.storageType = StorageType.Packed(io.github.charlietap.chasm.type.PackedType.I8)

        val repeatedRuntimeType = store.heap.registerRuntimeTypes(
            structTypes(listOf(numericField(Mutability.Var))),
        )[0]

        val reference = store.heap.allocateStruct(runtimeType, longArrayOf(17))
        val copiedField = store.heap.structFieldType(reference, 0)

        assertEquals(runtimeType, repeatedRuntimeType)
        assertEquals(Mutability.Var, copiedField.mutability)
        assertEquals(StorageType.Value(ValueType.Number(NumberType.I64)), copiedField.storageType)
        assertEquals(17, store.heap.getStructField(reference, 0))
    }

    @Test
    fun `checked access preserves struct trap ordering`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(structTypes(listOf(numericField(Mutability.Var))))[0]
        val reference = store.heap.allocateStruct(runtimeType, longArrayOf(31))

        val wrongKind = assertFailsWith<InvocationException> {
            store.heap.getStructField(4L, 0)
        }
        assertEquals(InvocationError.StructReferenceExpected, wrongKind.error)

        val invalidField = assertFailsWith<InvocationException> {
            store.heap.getStructField(reference, 1)
        }
        assertEquals(InvocationError.StructFieldLookupFailed(1), invalidField.error)

        store.heap.collectGarbage(store)
        val stale = assertFailsWith<InvocationException> {
            store.heap.getStructField(reference, 0)
        }
        assertEquals(
            InvocationError.StructLookupFailed(
                io.github.charlietap.chasm.runtime.address.Address.Struct((reference shr 8).toInt()),
            ),
            stale.error,
        )
    }

    @Test
    fun `trusted scalar access still rejects null and wrong aggregate tags`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(structTypes(listOf(numericField(Mutability.Var))))[0]
        val reference = store.heap.allocateStruct(runtimeType, longArrayOf(31))

        listOf(RV_TYPE_NULL, RV_TYPE_ARRAY).forEach { invalidReference ->
            val getFailure = assertFailsWith<InvocationException> {
                store.heap.getStructFieldTrusted(invalidReference, 0)
            }
            assertEquals(InvocationError.StructReferenceExpected, getFailure.error)

            val setFailure = assertFailsWith<InvocationException> {
                store.heap.setStructFieldTrusted(invalidReference, 0, 99)
            }
            assertEquals(InvocationError.StructReferenceExpected, setFailure.error)
        }

        assertEquals(31, store.heap.getStructField(reference, 0))
    }

    @Test
    fun `stack allocation preserves field order`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(
            structTypes(listOf(numericField(Mutability.Var), numericField(Mutability.Var))),
        )[0]
        val stack = ValueStack().apply {
            push(41)
            push(42)
        }

        store.heap.allocateStructFromStack(runtimeType, 2, stack)
        val reference = stack.pop()

        assertEquals(41, store.heap.getStructField(reference, 0))
        assertEquals(42, store.heap.getStructField(reference, 1))
    }

    @Test
    fun `pressure reports managed heap words and falls after sweep`() {
        assertEquals(false, Store().heap.shouldCollectGarbage(0))

        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(
            structTypes(listOf(numericField(Mutability.Var), numericField(Mutability.Var))),
        )[0]
        store.heap.allocateStruct(runtimeType, longArrayOf(1, 2))

        assertEquals(16, store.heap.allocatedGuestBytes())
        assertEquals(true, store.heap.shouldCollectGarbage(16))
        assertEquals(false, store.heap.shouldCollectGarbage(17))

        store.heap.collectGarbage(store)
        assertEquals(0, store.heap.allocatedGuestBytes())
    }

    @Test
    fun `equivalent self recursive registrations reuse canonical metadata without recursion`() {
        val store = Store()

        val first = store.heap.registerRuntimeTypes(selfRecursiveStructTypes())[0]
        val second = store.heap.registerRuntimeTypes(selfRecursiveStructTypes())[0]

        assertEquals(first, second)
        val reference = store.heap.allocateStruct(first, longArrayOf(0L))
        assertEquals(first.value, store.heap.structRuntimeTypeIdOrNegative(reference))
    }

    private companion object {
        fun numericField(mutability: Mutability) = FieldType(
            StorageType.Value(ValueType.Number(NumberType.I64)),
            mutability,
        )

        fun structTypes(fields: List<FieldType>): List<DefinedType> = DefinedTypeFactory(
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

        fun selfRecursiveStructTypes(): List<DefinedType> = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(
                                StructType(
                                    listOf(
                                        FieldType(
                                            StorageType.Value(
                                                ValueType.Reference(
                                                    io.github.charlietap.chasm.type.ReferenceType.RefNull(
                                                        ConcreteHeapType.TypeIndex(0),
                                                    ),
                                                ),
                                            ),
                                            Mutability.Var,
                                        ),
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
