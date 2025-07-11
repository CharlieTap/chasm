package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.value.NameValue

sealed interface Custom

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

data class NameAssociation(val idx: UInt, val name: NameValue)

typealias NameMap = List<NameAssociation>

data class IndirectNameAssociation(val idx: UInt, val nameMap: NameMap)

typealias IndirectNameMap = List<IndirectNameAssociation>

sealed interface NameSubsection

data class ModuleNameSubsection(val name: NameValue) : NameSubsection

data class FunctionNameSubsection(val nameMap: NameMap) : NameSubsection

data class LocalNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

data class LabelNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

data class TypeNameSubsection(val nameMap: NameMap) : NameSubsection

data class TableNameSubsection(val nameMap: NameMap) : NameSubsection

data class MemoryNameSubsection(val nameMap: NameMap) : NameSubsection

data class GlobalNameSubsection(val nameMap: NameMap) : NameSubsection

data class FieldNameSubsection(val indirectNameMap: IndirectNameMap) : NameSubsection

data class TagNameSubsection(val nameMap: NameMap) : NameSubsection

data class NameData(
    val subsections: List<NameSubsection>,
) : Custom
