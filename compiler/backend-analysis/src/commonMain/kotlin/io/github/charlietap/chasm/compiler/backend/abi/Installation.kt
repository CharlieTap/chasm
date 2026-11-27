package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi

internal const val COMPILED_FUNCTION = "Lio/github/charlietap/chasm/runtime/function/CompiledFunction;"

@InternalChasmApi
const val CODE_INSTALLATION = "Lio/github/charlietap/chasm/runtime/compiled/CodeInstallation;"

@InternalChasmApi
const val OBJECT_ENTRY = "Lio/github/charlietap/chasm/runtime/compiled/ObjectEntry;"

@InternalChasmApi
val ENTRY_CALL_OBJECTS = MethodRef(OBJECT_ENTRY, "callObjects", listOf(CONTEXT_TYPE, "[J", "[Ljava/lang/Object;", "I"), "V")

@InternalChasmApi
val INSTALL = MethodRef(CODE_INSTALLATION, "install", listOf("I", COMPILED_FUNCTION), "V")

@InternalChasmApi
val INSTALLATION_INSTANCE = MethodRef(CODE_INSTALLATION, "getInstance", emptyList(), MODULE_INSTANCE)

@InternalChasmApi
val INSTALLATION_MEMORY = installationAccessor("memory", MEMORY_INSTANCE)
internal val INSTALLATION_TABLE = installationAccessor("table", TABLE_INSTANCE)

@InternalChasmApi
val INSTALLATION_GLOBAL = installationAccessor("global", GLOBAL_INSTANCE)
internal val INSTALLATION_FUNCTION = installationAccessor("function", FUNCTION_INSTANCE)
internal val INSTALLATION_ELEMENT = installationAccessor("element", ELEMENT_INSTANCE)
internal val INSTALLATION_DATA = installationAccessor("data", DATA_INSTANCE)

@InternalChasmApi
val INSTALLATION_REFERENCE_GLOBAL = installationAccessor("referenceGlobal", OBJECT_TYPE)

@InternalChasmApi
val INSTALLATION_REFERENCE_TABLE = installationAccessor("referenceTable", OBJECT_TYPE)

@InternalChasmApi
val INSTALLATION_REFERENCE_SEGMENT = installationAccessor("referenceSegment", OBJECT_TYPE)
internal val INSTALLATION_RUNTIME_TYPE = installationAccessor("runtimeType", "I")
internal val INSTALLATION_TAG_ADDRESS = installationAccessor("tagAddress", "I")
internal val INSTALLATION_FUNCTION_REFERENCE = installationAccessor("functionReference", "J")

@InternalChasmApi
val INSTALLATION_MANAGED_FUNCTION = installationAccessor("managedFunction", OBJECT_TYPE)

@InternalChasmApi
val INSTALLATION_RTT = installationAccessor("rtt", OBJECT_TYPE)

@InternalChasmApi
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
    is Resource.FunctionObject -> INSTALLATION_MANAGED_FUNCTION
    is Resource.ReferenceGlobal -> INSTALLATION_REFERENCE_GLOBAL
    is Resource.ReferenceTable -> INSTALLATION_REFERENCE_TABLE
    is Resource.ReferenceSegment -> INSTALLATION_REFERENCE_SEGMENT
    is Resource.Rtt -> INSTALLATION_RTT
    else -> null
}

@InternalChasmApi
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
    is Resource.FunctionObject -> resource.index
    is Resource.ReferenceGlobal -> resource.index
    is Resource.ReferenceTable -> resource.index
    is Resource.ReferenceSegment -> resource.index
    is Resource.Rtt -> resource.typeIndex
    else -> error("$resource is not read by index from the installation")
}

private fun installationAccessor(
    name: String,
    returns: String,
) = MethodRef(CODE_INSTALLATION, name, listOf("I"), returns)
