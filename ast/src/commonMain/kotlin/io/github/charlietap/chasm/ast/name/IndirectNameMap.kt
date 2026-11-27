package io.github.charlietap.chasm.ast.name

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class IndirectNameAssociation(val idx: UInt, val nameMap: NameMap)

@InternalChasmApi
typealias IndirectNameMap = List<IndirectNameAssociation>
