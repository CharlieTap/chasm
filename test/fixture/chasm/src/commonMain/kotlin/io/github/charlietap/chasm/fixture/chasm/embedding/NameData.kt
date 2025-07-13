package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.embedding.shapes.NameData

fun nameData(): NameData = functionNameData()

fun functionNameData(
    name: String? = null,
    localNames: List<String>? = null,
) = FunctionNameData(
    name = name,
    localNames = localNames,
)
