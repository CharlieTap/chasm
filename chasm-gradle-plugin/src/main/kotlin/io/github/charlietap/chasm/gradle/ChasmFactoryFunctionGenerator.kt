package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName

internal class ChasmFactoryFunctionGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: FactoryVisibility,
        generateSuspendingFactory: Boolean,
    ): FunSpec {
        val moduleFactoryType = if (generateSuspendingFactory) {
            CHASM_SUSPEND_MODULE_FACTORY_CLASS_NAME
        } else {
            CHASM_MODULE_FACTORY_CLASS_NAME
        }
        val instanceFactoryType = if (generateSuspendingFactory) {
            CHASM_SUSPEND_INSTANCE_FACTORY_CLASS_NAME
        } else {
            CHASM_INSTANCE_FACTORY_CLASS_NAME
        }
        val createModule = if (generateSuspendingFactory) CHASM_SUSPENDING_MODULE else CHASM_MODULE
        val createInstance = if (generateSuspendingFactory) CHASM_SUSPENDING_INSTANCE else CHASM_INSTANCE
        val visibilityModifier = when (visibility) {
            FactoryVisibility.INTERNAL -> KModifier.INTERNAL
            FactoryVisibility.PUBLIC -> KModifier.PUBLIC
        }

        return FunSpec.builder(interfaceName.replaceFirstChar { character -> character.lowercaseChar() }).apply {
            addModifiers(visibilityModifier)
            if (generateSuspendingFactory) addModifiers(KModifier.SUSPEND)
            addParameter("binary", ByteArray::class)
            addParameter(
                ParameterSpec.builder("imports", CHASM_CODEGEN_IMPORT_LIST_CLASS_NAME)
                    .defaultValue("emptyList()")
                    .build(),
            )
            addParameter(
                ParameterSpec.builder("moduleFactory", moduleFactoryType.copy(nullable = true))
                    .defaultValue("null")
                    .build(),
            )
            addParameter(
                ParameterSpec.builder("instanceFactory", instanceFactoryType.copy(nullable = true))
                    .defaultValue("null")
                    .build(),
            )
            returns(ClassName(packageName, interfaceName))

            addStatement("val store = %M()", CHASM_STORE)
            addStatement(
                "val module = moduleFactory?.invoke(binary) ?: %M(binary).%M(%S)",
                createModule,
                CHASM_EXPECT,
                "Failed to decode binary",
            )
            addStatement("val allocatedImports = %M(store, imports)", CHASM_ALLOCATE_IMPORTS)
            addStatement(
                "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                    "%M(store, module, allocatedImports).%M(%S)",
                createInstance,
                CHASM_EXPECT,
                "Failed to instantiate module",
            )
            addStatement(
                "return %T(store = store, instance = instance)",
                ClassName(packageName, interfaceName + "Impl"),
            )
        }.build()
    }
}

internal val CHASM_STORE_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Store")
internal val CHASM_INSTANCE_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Instance")
internal val CHASM_MEMORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Memory")
internal val CHASM_GLOBAL_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Global")
internal val CHASM_PREPARED_FUNCTION_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "PreparedFunction")
internal val CHASM_EXECUTION_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "ExecutionValue")
internal val CHASM_I32_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "I32")
internal val CHASM_I64_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "I64")
internal val CHASM_F32_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "F32")
internal val CHASM_F64_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "F64")
internal val CHASM_CODEGEN_IMPORT_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "CodegenImport")
internal val CHASM_CODEGEN_IMPORT_LIST_CLASS_NAME =
    List::class.asClassName().parameterizedBy(CHASM_CODEGEN_IMPORT_CLASS_NAME)
internal val CHASM_MODULE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "ModuleFactory")
internal val CHASM_SUSPEND_MODULE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "SuspendModuleFactory")
internal val CHASM_INSTANCE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "InstanceFactory")
internal val CHASM_SUSPEND_INSTANCE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "SuspendInstanceFactory")

internal val CHASM_STORE = MemberName("io.github.charlietap.chasm.embedding", "store")
internal val CHASM_MODULE = MemberName("io.github.charlietap.chasm.embedding", "module")
internal val CHASM_INSTANCE = MemberName("io.github.charlietap.chasm.embedding", "instance")
internal val CHASM_SUSPENDING_MODULE = MemberName("io.github.charlietap.chasm.coroutines", "module")
internal val CHASM_SUSPENDING_INSTANCE = MemberName("io.github.charlietap.chasm.coroutines", "instance")
internal val CHASM_ALLOCATE_IMPORTS = MemberName("io.github.charlietap.chasm.embedding.codegen", "allocateImports")
internal val CHASM_EXPECT = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
