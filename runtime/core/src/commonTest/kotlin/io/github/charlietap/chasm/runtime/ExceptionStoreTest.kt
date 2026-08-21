package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.gc.MAXIMUM_FIXED_PAYLOAD_WORDS
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExceptionStoreTest {

    @Test
    fun `tag publication preserves semantic payload order`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(RTT(7), tagType(listOf(i64Type, i64Type)))

        val exceptionReference = store.heap.allocateException(tagAddress, longArrayOf(11, 22))

        assertEquals(Address.Tag(0), store.heap.exceptionTagAddress(exceptionReference))
        assertEquals(11, store.heap.getExceptionFieldTrusted(exceptionReference, 0))
        assertEquals(22, store.heap.getExceptionFieldTrusted(exceptionReference, 1))
    }

    @Test
    fun `equal exception layouts retain distinct tag identities`() {
        val store = Store()
        val firstTag = store.heap.registerTag(RTT(7), tagType(listOf(i64Type)))
        val secondTag = store.heap.registerTag(RTT(7), tagType(listOf(i64Type)))

        val firstException = store.heap.allocateException(firstTag, longArrayOf(11))
        val secondException = store.heap.allocateException(secondTag, longArrayOf(22))

        assertEquals(Address.Tag(0), store.heap.exceptionTagAddress(firstException))
        assertEquals(Address.Tag(1), store.heap.exceptionTagAddress(secondException))
        assertEquals(11, store.heap.getExceptionFieldTrusted(firstException, 0))
        assertEquals(22, store.heap.getExceptionFieldTrusted(secondException, 0))
    }

    @Test
    fun `stack and frame allocation publish atomically`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(RTT(0), tagType(listOf(i64Type, i64Type)))
        val stack = ValueStack().apply {
            push(31)
            push(32)
        }

        val topReference = store.heap.allocateExceptionFromStack(tagAddress, stack)
        assertEquals(0, stack.depth())
        assertEquals(31, store.heap.getExceptionFieldTrusted(topReference, 0))
        assertEquals(32, store.heap.getExceptionFieldTrusted(topReference, 1))

        stack.reserveFrame(3)
        stack.setFrameSlot(0, 41)
        stack.setFrameSlot(1, 42)
        val frameReference = store.heap.allocateExceptionFromFrame(tagAddress, 0, stack)
        assertEquals(41, store.heap.getExceptionFieldTrusted(frameReference, 0))
        assertEquals(42, store.heap.getExceptionFieldTrusted(frameReference, 1))
        assertEquals(41, stack.getFrameSlot(0))
        assertEquals(42, stack.getFrameSlot(1))

        stack.shrink(0, 0)
        stack.push(51)
        assertFailsWith<InvocationException> {
            store.heap.allocateExceptionFromStack(tagAddress, stack)
        }
        assertEquals(51, stack.pop())
    }

    @Test
    fun `checked exception resolution preserves trap ordering and rejects stale values`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(RTT(0), tagType(emptyList()))
        val reference = store.heap.allocateException(tagAddress, longArrayOf())

        listOf(RV_TYPE_NULL, RV_TYPE_ARRAY).forEach { invalidReference ->
            val failure = assertFailsWith<InvocationException> {
                store.heap.exceptionTagAddress(invalidReference)
            }
            assertEquals(InvocationError.ExceptionReferenceExpected, failure.error)
        }

        store.heap.collectGarbage(store)
        val stale = assertFailsWith<InvocationException> {
            store.heap.exceptionTagAddress(reference)
        }
        assertEquals(
            InvocationError.ExceptionLookupFailed(Address.Exception((reference ushr 8).toInt())),
            stale.error,
        )
        assertEquals(-1, store.heap.exceptionTagAddressOrNegative(reference))
    }

    @Test
    fun `exception descriptors trace only declared reference payload fields`() {
        val store = Store()
        val structType = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(StructType(emptyList())),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )[0]
        val runtimeType = store.heap.registerRuntimeType(structType)
        val retainedStruct = store.heap.allocateStruct(runtimeType, LongArray(0))
        val falseCandidate = store.heap.allocateStruct(runtimeType, LongArray(0))
        val tagAddress = store.heap.registerTag(
            RTT(1),
            tagType(
                listOf(
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
                    ValueType.Number(io.github.charlietap.chasm.type.NumberType.I64),
                ),
            ),
        )
        val exceptionReference = store.heap.allocateException(
            tagAddress,
            longArrayOf(retainedStruct, falseCandidate),
        )
        val roots = ValueStack().apply { push(exceptionReference) }

        store.heap.collectGarbage(store, roots)

        assertEquals(runtimeType.value, store.heap.structRuntimeTypeIdOrNegative(retainedStruct))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(falseCandidate))
        assertEquals(tagAddress.address, store.heap.exceptionTagAddressOrNegative(exceptionReference))

        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(retainedStruct))
        assertEquals(-1, store.heap.exceptionTagAddressOrNegative(exceptionReference))
    }

    @Test
    fun `exception and array cycles collect after their final root disappears`() {
        val store = Store()
        val arrayType = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
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
        )[0]
        val arrayRuntimeType = store.heap.registerRuntimeType(arrayType)
        val arrayReference = store.heap.allocateArrayFilled(
            arrayRuntimeType,
            length = 1,
            value = RV_TYPE_NULL,
        )
        val tagAddress = store.heap.registerTag(
            RTT(1),
            tagType(
                listOf(
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
                ),
            ),
        )
        val exceptionReference = store.heap.allocateException(
            tagAddress,
            longArrayOf(arrayReference),
        )
        store.heap.setArrayElementTrusted(arrayReference, 0, exceptionReference)

        store.heap.collectGarbage(store, ValueStack().apply { push(exceptionReference) })
        assertEquals(tagAddress.address, store.heap.exceptionTagAddressOrNegative(exceptionReference))
        assertEquals(arrayRuntimeType.value, store.heap.arrayRuntimeTypeIdOrNegative(arrayReference))

        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.exceptionTagAddressOrNegative(exceptionReference))
        assertEquals(-1, store.heap.arrayRuntimeTypeIdOrNegative(arrayReference))
        assertEquals(0, store.heap.allocatedGuestBytes())
    }

    @Test
    fun `repeated dead throws retain no historical exception objects`() {
        val store = Store()
        val tagAddress = store.heap.registerTag(RTT(0), tagType(listOf(i64Type)))

        repeat(2_048) { value ->
            store.heap.allocateException(tagAddress, longArrayOf(value.toLong()))
            store.heap.collectGarbage(store)
            assertEquals(0, store.heap.allocatedGuestBytes())
        }

        val live = store.heap.allocateException(tagAddress, longArrayOf(73))
        assertEquals(Address.Tag(0), store.heap.exceptionTagAddress(live))
        assertEquals(73, store.heap.getExceptionFieldTrusted(live, 0))
    }

    @Test
    fun `tag parameter limit fails before publication`() {
        val store = Store()
        val maximum = tagType(List(MAXIMUM_FIXED_PAYLOAD_WORDS) { i64Type })
        val maximumAddress = store.heap.registerTag(RTT(0), maximum)
        assertEquals(Address.Tag(0), maximumAddress)

        assertFailsWith<IllegalArgumentException> {
            store.heap.registerTag(
                RTT(1),
                tagType(List(MAXIMUM_FIXED_PAYLOAD_WORDS + 1) { i64Type }),
            )
        }
        val nextAddress = store.heap.registerTag(RTT(2), tagType(emptyList()))
        assertEquals(Address.Tag(1), nextAddress)
    }

    private fun tagType(parameters: List<ValueType>) = TagType(
        attribute = TagType.Attribute.Exception,
        typeIndex = 0,
        functionType = FunctionType(
            params = ResultType(parameters),
            results = ResultType(emptyList()),
        ),
    )

    private val i64Type = ValueType.Number(io.github.charlietap.chasm.type.NumberType.I64)
}
