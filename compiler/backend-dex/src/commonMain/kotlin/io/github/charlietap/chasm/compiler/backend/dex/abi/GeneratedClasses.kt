@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.dex.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.TRAP_DIAGNOSTICS_SOURCE_FILE
import io.github.charlietap.chasm.type.FunctionType

internal fun codeClass(
    packagePath: String,
    batch: Int,
) = "L$packagePath/$CODE_CLASS_PREFIX$batch;"

internal fun entryClass(
    packagePath: String,
    index: Int,
) = "L$packagePath/Entry$index;"

internal fun installClass(
    packagePath: String,
) = "L$packagePath/Install;"

internal fun signatureClass(
    packagePath: String,
    index: Int,
) = "L$packagePath/Sig$index;"

internal fun functionName(
    index: Int,
) = "$FUNCTION_METHOD_PREFIX$index"

internal fun functionMethod(
    owner: String,
    index: Int,
    type: FunctionType,
) =
    MethodRef(owner, functionName(index), staticParameters(type), returnDescriptor(type))

internal const val TYPED_CALL = "typed"

internal const val SOURCE_FILE = TRAP_DIAGNOSTICS_SOURCE_FILE

internal fun chunkName(
    function: String,
    chunk: Int,
) = "$function$CHUNK_METHOD_SEPARATOR$chunk"
