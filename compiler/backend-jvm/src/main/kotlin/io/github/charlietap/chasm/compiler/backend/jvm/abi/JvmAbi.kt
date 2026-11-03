@file:OptIn(ExperimentalChasmApi::class, InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import org.objectweb.asm.Type

internal const val CLASS_FILE_VERSION = 55

internal val COMPILED_MODULE: String = Type.getDescriptor(CompiledModule::class.java)
internal val COMPILED_CODE: String = Type.getDescriptor(CompiledCode::class.java)
internal const val STACK_OVERFLOW = "Ljava/lang/StackOverflowError;"
internal const val THROWABLE = "Ljava/lang/Throwable;"

internal val COMPILED_MODULE_FACTORY = MethodRef(
    "Lio/github/charlietap/chasm/aot/internal/CompiledModuleKt;",
    "compiledModule",
    listOf(COMPILED_CODE),
    COMPILED_MODULE,
)

internal const val BUILD_DECLARATIONS = "buildDeclarations"
internal const val TRAP_SITES_RESOURCE = "trapSitesResource"
internal const val RUNTIME_ABI = "runtimeAbi"
internal const val META_METER_FUEL = "meterFuel"
internal const val META_INTERRUPTIBLE = "interruptible"
internal const val META_STACKTRACES = "stacktraces"
internal const val META_INITIAL_FUEL = "initialFuel"
internal const val META_GC_STRATEGY = "gcStrategy"
internal const val META_GC_THRESHOLD_KIND = "gcThresholdKind"
internal const val META_GC_THRESHOLD_VALUE = "gcThresholdValue"
internal const val META_GC_HEAP_BYTES = "gcHeapBytes"
internal const val META_MEMORY_PAGES = "memoryPages"
internal const val META_TABLE_ELEMENTS = "tableElements"
internal const val META_TRAP_ON_GROW_FAILURE = "trapOnGrowFailure"
internal const val META_STACK_BYTES = "stackBytes"
internal const val MODULE_DECLARATIONS = "Lio/github/charlietap/chasm/declarations/ModuleDeclarations;"
internal const val DATA_SOURCE_TYPE = "Lio/github/charlietap/chasm/declarations/DataSource;"

internal val DATA_SOURCE = MethodRef(
    "Lio/github/charlietap/chasm/runtime/compiled/CompiledResources;",
    "dataSource",
    listOf("Ljava/lang/Class;", "Ljava/lang/String;"),
    DATA_SOURCE_TYPE,
)

internal val OBJECT_CONSTRUCTOR = MethodRef(OBJECT_TYPE, "<init>", emptyList(), "V")
