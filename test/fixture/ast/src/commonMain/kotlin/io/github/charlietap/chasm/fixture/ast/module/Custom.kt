package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.NameSubsection
import io.github.charlietap.chasm.ast.module.Uninterpreted
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.fixture.ast.value.nameValue

fun custom(): Custom = uninterpreted()

fun uninterpreted(
    name: NameValue = nameValue(),
    data: UByteArray = ubyteArrayOf(),
) = Uninterpreted(
    name = name,
    data = data,
)

fun nameData(
    subsections: List<NameSubsection>,
) = NameData(
    subsections = subsections,
)
