package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.RecursiveType
import io.github.charlietap.chasm.ir.type.RecursiveType.Companion.STATE_SYNTAX
import io.github.charlietap.chasm.ir.type.SubType

fun recursiveType(
    subTypes: List<SubType> = emptyList(),
    state: Byte = STATE_SYNTAX,
) = RecursiveType(
    subTypes = subTypes,
    state = state,
)

fun functionRecursiveType(
    functionType: FunctionType = functionType(),
    state: Byte = STATE_SYNTAX,
) = RecursiveType(
    subTypes = listOf(
        SubType.Final(emptyList(), CompositeType.Function(functionType)),
    ),
    state = state,
)
