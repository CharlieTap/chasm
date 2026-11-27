@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.dex.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.TRAP_DIAGNOSTICS_SOURCE_FILE
import io.github.charlietap.chasm.type.FunctionType

internal fun entryClass(
    entryPath: String,
    index: Int,
): String {
    return "L$entryPath\$Entry$index;"
}

internal fun installClass(entryPath: String): String {
    return "L$entryPath\$Install;"
}

internal fun installEntriesClass(
    entryPath: String,
    group: Int,
): String {
    return "L$entryPath\$InstallEntries$group;"
}

internal fun functionName(
    index: Int,
) = "$FUNCTION_METHOD_PREFIX$index"

internal fun functionMethod(
    owner: String,
    index: Int,
    type: FunctionType,
    reference: String,
) =
    MethodRef(owner, functionName(index), bindingParameters(type, reference), returnDescriptor(type, reference))

internal const val SOURCE_FILE = TRAP_DIAGNOSTICS_SOURCE_FILE

internal fun chunkName(
    function: String,
    chunk: Int,
) = "$function$CHUNK_METHOD_SEPARATOR$chunk"
