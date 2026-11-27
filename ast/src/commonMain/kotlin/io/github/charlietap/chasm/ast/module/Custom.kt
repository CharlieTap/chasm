package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.name.IndirectNameMap
import io.github.charlietap.chasm.ast.name.NameMap
import io.github.charlietap.chasm.ast.value.NameValue

@InternalChasmApi
sealed interface Custom

@InternalChasmApi
data class Uninterpreted(val name: NameValue, val data: UByteArray) : Custom {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is Uninterpreted) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int = name.hashCode()
}

@InternalChasmApi
sealed interface NameSubsection

@InternalChasmApi
data class ModuleNameSubsection(val name: NameValue) : NameSubsection

@InternalChasmApi
data class FunctionNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class LocalNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

@InternalChasmApi
data class LabelNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

@InternalChasmApi
data class TypeNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class TableNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class MemoryNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class GlobalNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class FieldNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

@InternalChasmApi
data class TagNameSubsection(val nameMap: NameMap) : NameSubsection

@InternalChasmApi
data class NameData(
    val subsections: List<NameSubsection>,
) : Custom
