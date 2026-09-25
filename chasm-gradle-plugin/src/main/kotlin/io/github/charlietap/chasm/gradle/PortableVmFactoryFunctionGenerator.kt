package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec

internal class PortableVmFactoryFunctionGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: FactoryVisibility,
        generateSuspendingFactory: Boolean,
    ): FunSpec {
        val virtualMachineType = if (generateSuspendingFactory) {
            SUSPENDING_WASM_VIRTUAL_MACHINE_CLASS_NAME
        } else {
            WASM_VIRTUAL_MACHINE_CLASS_NAME
        }
        val moduleFactoryType = if (generateSuspendingFactory) {
            SUSPEND_MODULE_FACTORY_CLASS_NAME
        } else {
            MODULE_FACTORY_CLASS_NAME
        }
        val instanceFactoryType = if (generateSuspendingFactory) {
            SUSPEND_INSTANCE_FACTORY_CLASS_NAME
        } else {
            INSTANCE_FACTORY_CLASS_NAME
        }
        val virtualMachineFactory = if (generateSuspendingFactory) {
            SUSPENDING_VM_FACTORY_CLASS_NAME
        } else {
            VM_FACTORY_CLASS_NAME
        }
        val moduleDecode = if (generateSuspendingFactory) {
            CREATE_MODULE_SUSPENDING_FUNCTION
        } else {
            CREATE_MODULE_FUNCTION
        }
        val moduleInstantiate = if (generateSuspendingFactory) {
            CREATE_INSTANCE_SUSPENDING_FUNCTION
        } else {
            CREATE_INSTANCE_FUNCTION
        }
        val visibilityModifier = when (visibility) {
            FactoryVisibility.INTERNAL -> KModifier.INTERNAL
            FactoryVisibility.PUBLIC -> KModifier.PUBLIC
        }

        return FunSpec.builder(interfaceName.replaceFirstChar { character -> character.lowercaseChar() }).apply {
            addModifiers(visibilityModifier)
            if (generateSuspendingFactory) {
                addModifiers(KModifier.SUSPEND)
            }
            addParameter("binary", ByteArray::class)
            addParameter(
                ParameterSpec.builder("imports", CODEGEN_IMPORT_LIST_CLASS_NAME)
                    .defaultValue("emptyList()")
                    .build(),
            )
            addParameter(
                ParameterSpec.builder("virtualMachine", virtualMachineType)
                    .defaultValue("%M()", virtualMachineFactory)
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

            addStatement("val store: %T = virtualMachine.%L()", STORE_CLASS_NAME, CREATE_STORE_FUNCTION)
            addCode("\n")
            addStatement(
                "val module: %T = moduleFactory?.invoke(binary) ?: virtualMachine.%L(binary).%M(%S)",
                MODULE_CLASS_NAME,
                moduleDecode,
                EXPECT_RESULT_FUNCTION,
                "Failed to decode binary",
            )
            addCode("\n")
            addStatement(
                "val allocatedImports: %T = virtualMachine.%M(store, imports)",
                IMPORT_LIST_CLASS_NAME,
                IMPORT_FACTORY_CLASS_NAME,
            )
            addCode("\n")
            addStatement(
                "val instance: %T = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                    "virtualMachine.%L(store, module, allocatedImports).%M(%S)",
                INSTANCE_CLASS_NAME,
                moduleInstantiate,
                EXPECT_RESULT_FUNCTION,
                "Failed to instantiate module",
            )
            addCode("\n")
            addCode(
                CodeBlock.builder()
                    .add("return %T(\n", ClassName(packageName, interfaceName + "Impl"))
                    .indent()
                    .add("imports = allocatedImports,\n")
                    .add("instance = instance,\n")
                    .add("store = store,\n")
                    .add("virtualMachine = virtualMachine,\n")
                    .unindent()
                    .add(")\n")
                    .build(),
            )
        }.build()
    }
}
