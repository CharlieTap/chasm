package io.github.charlietap.chasm.compiler.backend.dex.binding

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.heapTypeNumber
import io.github.charlietap.chasm.compiler.backend.analysis.FunctionCensus
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleAnalysis
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces

internal class BindingLayout(
    declarations: ModuleIndexSpaces,
    analysis: ModuleAnalysis,
) {

    private val bridgedFunctions = analysis.bridgedFunctions
    private val referencedFunctions = analysis.referencedFunctions

    private val memories = FIRST_DECLARATION_SLOT
    private val globals = memories + declarations.memoryCount
    private val tables = globals + declarations.globalCount
    private val functions = tables + declarations.tableCount
    private val elements = functions + bridgedFunctions.count
    private val data = elements + declarations.elementCount
    private val typeTests = data + declarations.dataCount

    val objectCount = typeTests + (declarations.typeCount + ABSTRACT_HEAP_TYPES) * 2

    private val runtimeTypes = 0
    private val functionReferences = runtimeTypes + declarations.typeCount
    private val tags = functionReferences + referencedFunctions.count

    val valueCount = tags + declarations.tagCount

    fun objectSlot(resource: Resource): Int {
        return when (resource) {
            Resource.Instance -> INSTANCE_SLOT
            Resource.Unsafe -> UNSAFE_SLOT
            is Resource.Memory -> ranged(memories, resource.index, globals, resource)
            is Resource.Global -> ranged(globals, resource.index, tables, resource)
            is Resource.Table -> ranged(tables, resource.index, functions, resource)
            is Resource.Element -> ranged(elements, resource.index, data, resource)
            is Resource.Data -> ranged(data, resource.index, typeTests, resource)
            is Resource.TypeTest -> {
                val index = (heapTypeNumber(resource.type.heapType) + ABSTRACT_HEAP_TYPES) * 2 + if (resource.type.nullable) 1 else 0
                ranged(typeTests, index, objectCount, resource)
            }
            is Resource.Function -> functions + censusOrdinal(bridgedFunctions, resource.index, resource)
            else -> throw IllegalArgumentException("$resource has no binding object slot")
        }
    }

    fun valueSlot(resource: Resource): Int {
        return when (resource) {
            is Resource.RuntimeType -> ranged(runtimeTypes, resource.typeIndex, functionReferences, resource)
            is Resource.FunctionReference -> functionReferences + censusOrdinal(referencedFunctions, resource.index, resource)
            is Resource.TagAddress -> ranged(tags, resource.index, valueCount, resource)
            else -> throw IllegalArgumentException("$resource has no binding value slot")
        }
    }

    private fun ranged(
        base: Int,
        index: Int,
        end: Int,
        resource: Resource,
    ): Int {
        val slot = base + index
        check(index >= 0 && slot < end) { "$resource is outside the module's declarations" }
        return slot
    }

    private fun censusOrdinal(
        census: FunctionCensus,
        function: Int,
        resource: Resource,
    ): Int {
        val ordinal = census.ordinal(function)
        check(ordinal != FunctionCensus.NOT_INCLUDED) { "$resource is missing from the module's function census" }
        return ordinal
    }

    companion object {
        const val VALUES_SLOT = 0
        private const val INSTANCE_SLOT = 1
        private const val UNSAFE_SLOT = 2
        private const val FIRST_DECLARATION_SLOT = 3
        private const val ABSTRACT_HEAP_TYPES = 12
    }
}
