package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.RecursiveType.Companion.STATE_SYNTAX
import io.github.charlietap.chasm.type.SubType

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
