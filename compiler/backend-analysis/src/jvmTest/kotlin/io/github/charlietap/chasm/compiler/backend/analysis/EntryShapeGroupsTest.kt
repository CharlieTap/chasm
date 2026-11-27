package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.compiler.backend.abi.EntryShape
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class EntryShapeGroupsTest {

    @Test
    fun `functions kept apart by the partition never share a group, so a full group does not split a partition`() {

        val groups = entryShapeGroups(listOf("a1", "b1", "b2"), limit = 2, shape = { INT_SHAPE }, partition = { name -> name.first() })

        assertEquals(listOf(listOf("a1"), listOf("b1", "b2")), groups.map { group -> group.members })
    }

    @Test
    fun `without a partition a shape fills groups in function order`() {

        val groups = entryShapeGroups(listOf("a1", "b1", "b2"), limit = 2) { INT_SHAPE }

        assertEquals(listOf(listOf("a1", "b1"), listOf("b2")), groups.map { group -> group.members })
    }

    @Test
    fun `shapes come out in the order they first appear`() {

        val groups = entryShapeGroups(listOf("long", "int", "long2"), limit = 4) { name -> if (name.startsWith("long")) LONG_SHAPE else INT_SHAPE }

        assertEquals(listOf(LONG_SHAPE, INT_SHAPE), groups.map { group -> group.shape })
        assertEquals(listOf(listOf("long", "long2"), listOf("int")), groups.map { group -> group.members })
    }

    @Test
    fun `a shape with more functions than the limit yields adjacent groups in function order`() {

        val groups = entryShapeGroups(listOf("a", "other", "b", "c"), limit = 2) { name -> if (name == "other") LONG_SHAPE else INT_SHAPE }

        assertEquals(listOf(listOf("a", "b"), listOf("c"), listOf("other")), groups.map { group -> group.members })
    }

    private companion object {
        val INT_SHAPE = shape(NumberType.I32)
        val LONG_SHAPE = shape(NumberType.I64)

        fun shape(parameter: NumberType): EntryShape {
            return EntryShape(FunctionType(ResultType(listOf(ValueType.Number(parameter))), ResultType(emptyList())), OBJECT_TYPE)
        }
    }
}
