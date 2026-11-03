package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.cir.HeapType

const val COMPILED_FUNCTION = "Lio/github/charlietap/chasm/runtime/function/CompiledFunction;"
const val CODE_INSTALLATION = "Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;"

val ENTRY_INVOKE = MethodRef(COMPILED_FUNCTION, "invoke", listOf(CONTEXT_TYPE, "[J", "I"), "V")
val ENTRY_CALL = MethodRef(COMPILED_FUNCTION, "call", listOf(CONTEXT_TYPE, "[J", "I"), "V")

val INSTALL = MethodRef(CODE_INSTALLATION, "install", listOf("I", COMPILED_FUNCTION), "V")
val INSTALLATION_INSTANCE = MethodRef(CODE_INSTALLATION, "getInstance", emptyList(), MODULE_INSTANCE)
val INSTALLATION_MEMORY = installationAccessor("memory", MEMORY_INSTANCE)
val INSTALLATION_TABLE = installationAccessor("table", TABLE_INSTANCE)
val INSTALLATION_GLOBAL = installationAccessor("global", GLOBAL_INSTANCE)
val INSTALLATION_FUNCTION = installationAccessor("function", FUNCTION_INSTANCE)
val INSTALLATION_ELEMENT = installationAccessor("element", ELEMENT_INSTANCE)
val INSTALLATION_DATA = installationAccessor("data", DATA_INSTANCE)
val INSTALLATION_RUNTIME_TYPE = installationAccessor("runtimeType", "I")
val INSTALLATION_TAG_ADDRESS = installationAccessor("tagAddress", "I")
val INSTALLATION_FUNCTION_REFERENCE = installationAccessor("functionReference", "J")
val INSTALLATION_TYPE_TEST = MethodRef(CODE_INSTALLATION, "typeTest", listOf("Z", "I"), OBJECT_TYPE)

fun installationAccessor(
    resource: Resource,
): MethodRef? = when (resource) {
    is Resource.Memory -> INSTALLATION_MEMORY
    is Resource.Table -> INSTALLATION_TABLE
    is Resource.Global -> INSTALLATION_GLOBAL
    is Resource.Function -> INSTALLATION_FUNCTION
    is Resource.Element -> INSTALLATION_ELEMENT
    is Resource.Data -> INSTALLATION_DATA
    is Resource.RuntimeType -> INSTALLATION_RUNTIME_TYPE
    is Resource.TagAddress -> INSTALLATION_TAG_ADDRESS
    is Resource.FunctionReference -> INSTALLATION_FUNCTION_REFERENCE
    else -> null
}

fun installationIndex(
    resource: Resource,
): Int = when (resource) {
    is Resource.Memory -> resource.index
    is Resource.Table -> resource.index
    is Resource.Global -> resource.index
    is Resource.Function -> resource.index
    is Resource.Element -> resource.index
    is Resource.Data -> resource.index
    is Resource.RuntimeType -> resource.typeIndex
    is Resource.TagAddress -> resource.index
    is Resource.FunctionReference -> resource.index
    else -> error("$resource is not read by index from the installation")
}

fun heapTypeNumber(
    heapType: HeapType,
): Int = when (heapType) {
    is HeapType.Concrete -> heapType.type.index
    is HeapType.Abstract -> -1 - ABSTRACT_HEAP_TYPES.indexOf(heapType.type.text).also { check(it >= 0) { "unknown heap type $heapType" } }
}

private val ABSTRACT_HEAP_TYPES = listOf("func", "nofunc", "extern", "noextern", "any", "eq", "i31", "struct", "array", "none", "exn", "noexn")

private fun installationAccessor(
    name: String,
    returns: String,
) = MethodRef(CODE_INSTALLATION, name, listOf("I"), returns)
