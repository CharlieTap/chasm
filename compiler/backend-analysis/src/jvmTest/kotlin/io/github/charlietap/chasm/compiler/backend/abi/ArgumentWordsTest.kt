package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class ArgumentWordsTest {

    @Test
    fun `argument words match the static parameters after the binding, context and depth`() {
        val parameters = listOf(NumberType.I32, NumberType.I64, NumberType.F32, NumberType.F64).map(ValueType::Number)
        val type = function(parameters)

        assertEquals(staticParameters(type, OBJECT_TYPE).drop(3).sumOf(::descriptorWords), argumentWords(type))
        assertEquals(6, argumentWords(type))
    }

    @Test
    fun `a function at the parameter limit passes each argument`() {
        val type = function(List(MAX_PARAMETERS) { ValueType.Number(NumberType.I64) })

        assertEquals(MAX_PARAMETERS * 2, argumentWords(type))
    }

    @Test
    fun `a function past the parameter limit passes one argument array`() {
        val type = function(List(MAX_PARAMETERS + 1) { ValueType.Number(NumberType.I64) })

        assertEquals(staticParameters(type, OBJECT_TYPE).drop(3).sumOf(::descriptorWords), argumentWords(type))
        assertEquals(1, argumentWords(type))
    }

    private fun function(parameters: List<ValueType>): FunctionType {
        return FunctionType(ResultType(parameters), ResultType(emptyList()))
    }
}
