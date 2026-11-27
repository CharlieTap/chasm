package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EntryShapeTest {

    @Test
    fun `function types that differ only in the heap type of a reference share a shape`() {

        val funcReferenceShape = shape(listOf(I32, reference(AbstractHeapType.Func)), listOf(reference(AbstractHeapType.Any)))
        val externReferenceShape = shape(listOf(I32, reference(AbstractHeapType.Extern)), listOf(reference(AbstractHeapType.Extern)))

        assertEquals(funcReferenceShape, externReferenceShape)
        assertEquals(funcReferenceShape.hashCode(), externReferenceShape.hashCode())
    }

    @Test
    fun `a number and a reference in the same position do not share a shape`() {
        assertNotEquals(shape(listOf(I64), emptyList()), shape(listOf(reference(AbstractHeapType.Any)), emptyList()))
        assertNotEquals(shape(emptyList(), listOf(I64)), shape(emptyList(), listOf(reference(AbstractHeapType.Any))))
    }

    @Test
    fun `numbers of different kinds do not share a shape`() {
        assertNotEquals(shape(listOf(I32), emptyList()), shape(listOf(I64), emptyList()))
        assertNotEquals(shape(emptyList(), listOf(I32)), shape(emptyList(), listOf(F32)))
    }

    @Test
    fun `several results share a shape only when references sit in the same positions`() {

        val numberFirst = shape(emptyList(), listOf(I64, reference(AbstractHeapType.Func)))
        val referenceFirst = shape(emptyList(), listOf(reference(AbstractHeapType.Func), I64))

        assertNotEquals(numberFirst, referenceFirst)
        assertEquals(listOf(false, true), numberFirst.referenceResults)
        assertEquals(numberFirst, shape(emptyList(), listOf(I32, reference(AbstractHeapType.Extern))))
    }

    @Test
    fun `fewer than two results carry no reference result flags`() {
        assertEquals(emptyList(), shape(emptyList(), listOf(reference(AbstractHeapType.Any))).referenceResults)
        assertEquals(emptyList(), shape(emptyList(), emptyList()).referenceResults)
    }

    @Test
    fun `past the parameter limit, functions passing the same arrays share a shape`() {

        val longs = shape(List(MAX_PARAMETERS + 1) { I64 }, emptyList())
        val ints = shape(List(MAX_PARAMETERS + 50) { I32 }, emptyList())
        val references = shape(List(MAX_PARAMETERS + 1) { reference(AbstractHeapType.Any) }, emptyList())

        assertEquals(longs, ints)
        assertEquals(ArgumentPassing.ValueArray, longs.passing)
        assertEquals(listOf("[J"), longs.arguments)
        assertEquals(ArgumentPassing.ValueAndReferenceArrays, references.passing)
        assertEquals(listOf("[J", "[Ljava/lang/Object;"), references.arguments)
    }

    @Test
    fun `a shape's typed call is the typed call of each function type it describes`() {

        val types = listOf(
            FunctionType(ResultType(listOf(I32, reference(AbstractHeapType.Any))), ResultType(listOf(F64))),
            FunctionType(ResultType(List(MAX_PARAMETERS + 1) { I64 }), ResultType(emptyList())),
            FunctionType(ResultType(List(MAX_PARAMETERS + 1) { reference(AbstractHeapType.Any) }), ResultType(listOf(I64, I64))),
        )

        types.forEach { type ->
            assertEquals(typedCall("LOwner;", type, OBJECT_TYPE), EntryShape(type, OBJECT_TYPE).typedCall("LOwner;"))
        }
    }

    @Test
    fun `a shape's typed call takes the context and depth before one descriptor per argument`() {

        val typedCall = shape(listOf(I32, reference(AbstractHeapType.Any)), listOf(F64)).typedCall("LOwner;")

        assertEquals(MethodRef("LOwner;", TYPED_CALL, listOf(CONTEXT_TYPE, "I", "I", OBJECT_TYPE), "D"), typedCall)
    }

    private fun shape(
        parameters: List<ValueType>,
        results: List<ValueType>,
    ): EntryShape {
        return EntryShape(FunctionType(ResultType(parameters), ResultType(results)), OBJECT_TYPE)
    }

    private fun reference(heapType: AbstractHeapType): ValueType {
        return ValueType.Reference(ReferenceType.RefNull(heapType))
    }

    private companion object {
        val I32 = ValueType.Number(NumberType.I32)
        val I64 = ValueType.Number(NumberType.I64)
        val F32 = ValueType.Number(NumberType.F32)
        val F64 = ValueType.Number(NumberType.F64)
    }
}
