package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType

fun recursiveType(
    subTypes: List<SubType> = emptyList(),
) = RecursiveType(subTypes)

fun functionRecursiveType(
    functionType: FunctionType = functionType(),
) = RecursiveType(
    listOf(
        SubType.Final(emptyList(), CompositeType.Function(functionType)),
    ),
)
