package io.github.charlietap.chasm.compiler.backend.jvm.abi

import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_MODULE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE

internal const val CLASS_FILE_VERSION = 55

internal val MODULE_STRUCT_LANES = MethodRef(COMPILED_MODULE, "structLanes", emptyList(), "[J")
internal val MODULE_NEW_STRUCT = MethodRef(COMPILED_MODULE, "newStruct", listOf("I", OBJECT_TYPE), OBJECT_TYPE)
internal const val STACK_OVERFLOW = "Ljava/lang/StackOverflowError;"
internal const val THROWABLE = "Ljava/lang/Throwable;"

internal const val BUILD_DECLARATIONS = "buildDeclarations"
internal const val RESOURCE_ROOT = "resourceRoot"
internal const val MODULE_DECLARATIONS = "Lio/github/charlietap/chasm/declarations/ModuleDeclarations;"
internal const val DATA_SOURCE_TYPE = "Lio/github/charlietap/chasm/declarations/DataSource;"

internal val OBJECT_CONSTRUCTOR = MethodRef(OBJECT_TYPE, "<init>", emptyList(), "V")
