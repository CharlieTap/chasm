package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType

fun recursiveType(
    subTypes: List<SubType>,
) = RecursiveType(subTypes)
