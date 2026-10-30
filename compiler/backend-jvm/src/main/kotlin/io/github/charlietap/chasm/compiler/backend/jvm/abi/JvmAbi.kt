@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import org.objectweb.asm.Type

internal const val CLASS_FILE_VERSION = 55

internal const val COMPILED_FUNCTION = "Lio/github/charlietap/chasm/runtime/function/CompiledFunction;"
internal val COMPILED_MODULE: String = Type.getDescriptor(CompiledModule::class.java)
internal val COMPILED_CODE: String = Type.getDescriptor(CompiledCode::class.java)
internal val CODE_INSTALLATION: String = Type.getDescriptor(CodeInstallation::class.java)
internal const val STACK_OVERFLOW = "Ljava/lang/StackOverflowError;"
internal const val THROWABLE = "Ljava/lang/Throwable;"

private const val MEMORY = "Lio/github/charlietap/chasm/runtime/compiled/CompiledMemory;"
private const val RUNTIME = "Lio/github/charlietap/chasm/runtime/compiled/CompiledRuntime;"

internal val ENTRY_INVOKE = MethodRef(COMPILED_FUNCTION, "invoke", listOf(CONTEXT_TYPE, "[J", "I"), "V")

internal val ENTRY_CALL = MethodRef(COMPILED_FUNCTION, "call", listOf(CONTEXT_TYPE, "[J", "I"), "V")

internal val COMPILED_MODULE_FACTORY = MethodRef(
    "Lio/github/charlietap/chasm/aot/internal/CompiledModuleKt;",
    "compiledModule",
    listOf(COMPILED_CODE),
    COMPILED_MODULE,
)

internal val INSTALL = MethodRef(CODE_INSTALLATION, "install", listOf("I", COMPILED_FUNCTION), "V")
internal val INSTALLATION_INSTANCE = MethodRef(CODE_INSTALLATION, "getInstance", emptyList(), "Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;")

internal val DIVIDE_BY_ZERO = MethodRef(RUNTIME, "divideByZero", emptyList(), "Ljava/lang/Void;")
internal val INTEGER_OVERFLOW = MethodRef(RUNTIME, "integerOverflow", emptyList(), "Ljava/lang/Void;")
internal val CONVERSION_FAILED = MethodRef(RUNTIME, "conversionFailed", emptyList(), "Ljava/lang/Void;")
internal val MEMORY_OUT_OF_BOUNDS = MethodRef(RUNTIME, "memoryOutOfBounds", emptyList(), "Ljava/lang/Void;")

internal fun memoryAccess(
    name: String,
    value: String?,
    result: String?,
) = MethodRef(
    MEMORY,
    name,
    listOf(MEMORY_INSTANCE, "I", "I") + listOfNotNull(value),
    result ?: "V",
)

internal const val UNSAFE_TYPE = "Lsun/misc/Unsafe;"
internal const val MEMORY_BYTE_ARRAY_FIELD = "byteArray"
internal val MEMORY_SIZE = MethodRef(MEMORY_INSTANCE, "getSize", emptyList(), "I")
internal val UNSAFE_INSTANCE = MethodRef(MEMORY, "unsafe", emptyList(), OBJECT_TYPE)
internal val BYTE_ARRAY_BASE = MethodRef(MEMORY, "byteArrayBase", emptyList(), "J")
internal val UNSAFE_GET_BYTE = MethodRef(UNSAFE_TYPE, "getByte", listOf(OBJECT_TYPE, "J"), "B")
internal val UNSAFE_PUT_BYTE = MethodRef(UNSAFE_TYPE, "putByte", listOf(OBJECT_TYPE, "J", "B"), "V")
internal val UNSAFE_GET_INT = MethodRef(UNSAFE_TYPE, "getInt", listOf(OBJECT_TYPE, "J"), "I")
internal val UNSAFE_GET_LONG = MethodRef(UNSAFE_TYPE, "getLong", listOf(OBJECT_TYPE, "J"), "J")
internal val UNSAFE_PUT_INT = MethodRef(UNSAFE_TYPE, "putInt", listOf(OBJECT_TYPE, "J", "I"), "V")
internal val UNSAFE_PUT_LONG = MethodRef(UNSAFE_TYPE, "putLong", listOf(OBJECT_TYPE, "J", "J"), "V")

internal const val BUILD_DECLARATIONS = "buildDeclarations"
internal const val MODULE_DECLARATIONS = "Lio/github/charlietap/chasm/declarations/ModuleDeclarations;"
internal const val DATA_SOURCE_TYPE = "Lio/github/charlietap/chasm/declarations/DataSource;"

internal val DATA_SOURCE = MethodRef(
    "Lio/github/charlietap/chasm/runtime/compiled/CompiledResources;",
    "dataSource",
    listOf("Ljava/lang/Class;", "Ljava/lang/String;"),
    DATA_SOURCE_TYPE,
)

internal val MEMORY_COPY = MethodRef(MEMORY, "memoryCopy", listOf(MEMORY_INSTANCE, MEMORY_INSTANCE, "I", "I", "I"), "V")
internal val MEMORY_FILL = MethodRef(MEMORY, "memoryFill", listOf(MEMORY_INSTANCE, "I", "I", "I"), "V")

internal val OBJECT_CONSTRUCTOR = MethodRef(OBJECT_TYPE, "<init>", emptyList(), "V")
