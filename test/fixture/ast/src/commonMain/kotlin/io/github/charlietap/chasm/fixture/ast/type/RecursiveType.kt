package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.RecursiveType.Companion.STATE_SYNTAX
import io.github.charlietap.chasm.ast.type.SubType

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
