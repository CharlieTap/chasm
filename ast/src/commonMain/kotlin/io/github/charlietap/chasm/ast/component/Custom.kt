package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
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
data class ComponentNameSubsection(val name: NameValue) : NameSubsection

@InternalChasmApi
data class SortNameSubsection(
    val sort: NameSort,
    val nameMap: NameMap,
) : NameSubsection

@InternalChasmApi
sealed interface NameSort {

    data object CoreFunction : NameSort

    data object CoreTable : NameSort

    data object CoreMemory : NameSort

    data object CoreGlobal : NameSort

    data object CoreTag : NameSort

    data object CoreType : NameSort

    data object CoreModule : NameSort

    data object CoreInstance : NameSort

    data object Function : NameSort

    data object Value : NameSort

    data object Type : NameSort

    data object Component : NameSort

    data object Instance : NameSort
}

@InternalChasmApi
data class NameData(
    val subsections: List<NameSubsection>,
) : Custom
