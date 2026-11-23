@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION
import io.github.charlietap.chasm.runtime.compiled.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.TRAP_DIAGNOSTICS_SOURCE_FILE
import io.github.charlietap.chasm.type.FunctionType

internal fun bindingClassDescriptor(
    entryPath: String,
) = "L$entryPath\$Binding;"

internal fun codeClassDescriptor(
    entryPath: String,
    batch: Int,
) = "L$entryPath\$$CODE_CLASS_PREFIX$batch;"

internal fun chunkClassDescriptor(
    entryPath: String,
    index: Int,
    group: Int,
) = "L$entryPath\$$CHUNK_CLASS_PREFIX$index\$$group;"

internal fun entriesClassDescriptor(
    entryPath: String,
    group: Int,
) = "L$entryPath\$Entries$group;"

internal fun signatureClassDescriptor(
    entryPath: String,
    index: Int,
) = "L$entryPath\$Sig$index;"

internal fun moduleClassDescriptor(
    entryPath: String,
) = "L$entryPath;"

internal fun dataResource(
    entryPath: String,
) = "$entryPath.$DATA_EXTENSION"

internal fun installClassDescriptor(
    entryPath: String,
    chunk: Int,
) = "L$entryPath\$Install$chunk;"

internal fun functionName(
    index: Int,
) = "$FUNCTION_METHOD_PREFIX$index"

internal fun functionMethod(
    owner: String,
    binding: String,
    index: Int,
    type: FunctionType,
) =
    MethodRef(owner, functionName(index), staticParameters(type, binding), returnDescriptor(type))

internal const val TYPED_CALL = "typed"

internal const val SOURCE_FILE = TRAP_DIAGNOSTICS_SOURCE_FILE
