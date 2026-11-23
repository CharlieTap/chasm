package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import org.objectweb.asm.Type

internal const val CLASS_FILE_VERSION = 55

internal val COMPILED_MODULE: String = Type.getDescriptor(CompiledModule::class.java)
internal const val STACK_OVERFLOW = "Ljava/lang/StackOverflowError;"
internal const val THROWABLE = "Ljava/lang/Throwable;"

internal const val BUILD_DECLARATIONS = "buildDeclarations"
internal const val RESOURCE_ROOT = "resourceRoot"
internal const val RUNTIME_ABI = "runtimeAbi"
internal const val META_INTERRUPTS = "interrupts"
internal const val META_STACKTRACES = "stacktraces"
internal const val META_FUEL = "fuel"
internal const val META_LINEAR_MEMORY_BYTES = "linearMemoryBytes"
internal const val META_GC_HEAP_BYTES = "gcHeapBytes"
internal const val META_TABLE_BYTES = "tableBytes"
internal const val META_STACK_BYTES = "stackBytes"
internal const val META_TRAP_ON_GROWTH_FAILURE = "trapOnGrowthFailure"
internal const val MODULE_DECLARATIONS = "Lio/github/charlietap/chasm/declarations/ModuleDeclarations;"
internal const val DATA_SOURCE_TYPE = "Lio/github/charlietap/chasm/declarations/DataSource;"

internal val DATA_SOURCE = MethodRef(
    "Lio/github/charlietap/chasm/runtime/compiled/CompiledResources;",
    "dataSource",
    listOf("Ljava/lang/Class;", "Ljava/lang/String;"),
    DATA_SOURCE_TYPE,
)

internal val OBJECT_CONSTRUCTOR = MethodRef(OBJECT_TYPE, "<init>", emptyList(), "V")
