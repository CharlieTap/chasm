package io.github.charlietap.chasm.ast.name

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.value.NameValue

@InternalChasmApi
data class NameAssociation(val idx: UInt, val name: NameValue)

@InternalChasmApi
typealias NameMap = List<NameAssociation>
