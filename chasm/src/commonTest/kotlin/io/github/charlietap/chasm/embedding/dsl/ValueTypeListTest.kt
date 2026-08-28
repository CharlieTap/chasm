package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueTypeListTest {

    @Test
    fun `can build numeric and reference value types`() {
        val referenceType = ReferenceType.Ref(AbstractHeapType.Any)

        val actual = valueTypeList {
            i32()
            i64()
            f32()
            f64()
            reference(referenceType)
            ref(AbstractHeapType.Struct)
            refNull(AbstractHeapType.Array)
            funcref()
            exnref()
            externref()
            anyref()
            eqref()
            i31ref()
            structref()
            arrayref()
        }

        val expected = listOf(
            ValueType.Number(NumberType.I32),
            ValueType.Number(NumberType.I64),
            ValueType.Number(NumberType.F32),
            ValueType.Number(NumberType.F64),
            ValueType.Reference(referenceType),
            ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Struct)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Array)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Exception)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.I31)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Struct)),
            ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Array)),
        )

        assertEquals(expected, actual)
    }
}
