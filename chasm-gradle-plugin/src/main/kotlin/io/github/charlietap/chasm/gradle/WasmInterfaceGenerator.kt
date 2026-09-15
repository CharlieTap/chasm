package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.charlietap.chasm.gradle.ext.asTypeName

internal class DataClassGenerator {
    operator fun invoke(
        packageName: String,
        type: GeneratedType,
    ): TypeSpec = TypeSpec.classBuilder(ClassName(packageName, type.name)).apply {
        addModifiers(KModifier.DATA)

        val constructor = FunSpec.constructorBuilder()

        type.fields.forEach { field ->
            val typeName = field.type.asTypeName()
            val param = ParameterSpec.builder(field.name, typeName).build()
            constructor.addParameter(param)

            val property = PropertySpec.builder(field.name, typeName).apply {
                initializer(field.name)
            }.build()
            addProperty(property)
        }
        primaryConstructor(constructor.build())
    }.build()
}

internal class WasmInterfaceGenerator(
    private val dataClassGenerator: DataClassGenerator = DataClassGenerator(),
    private val classInterfaceGenerator: ClassInterfaceGenerator = ClassInterfaceGenerator(),
    private val factoryFunctionGenerator: FactoryFunctionGenerator = FactoryFunctionGenerator(),
    private val classImplementationGenerator: ClassImplementationGenerator = ClassImplementationGenerator(),
    private val visibilityValidator: VisibilityValidator = VisibilityValidator(),
) {
    operator fun invoke(
        interfaceVisibility: InterfaceVisibility,
        factoryVisibility: FactoryVisibility,
        wasmInterface: WasmInterface,
        implementationVisibility: ImplementationVisibility = ImplementationVisibility.PRIVATE,
        config: CodegenConfig = CodegenConfig(),
    ): List<FileSpec> {
        visibilityValidator(
            interfaceName = wasmInterface.interfaceName,
            interfaceVisibility = interfaceVisibility,
            factoryVisibility = factoryVisibility,
            implementationVisibility = implementationVisibility,
        )

        val interfaceFile = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName).apply {
            wasmInterface.types.forEach { type ->
                addType(dataClassGenerator(wasmInterface.packageName, type))
            }
            addType(classInterfaceGenerator(wasmInterface.packageName, wasmInterface.interfaceName, interfaceVisibility, wasmInterface))
        }.build()

        val implementationFile = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName + "Impl").apply {
            addFunction(
                factoryFunctionGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = factoryVisibility,
                    generateSuspendingFactory = config.generateSuspendingFactories,
                ),
            )
            addType(
                classImplementationGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = implementationVisibility,
                    wasmInterface = wasmInterface,
                ),
            )
        }.build()

        return listOf(interfaceFile, implementationFile)
    }
}

internal class VisibilityValidator {
    operator fun invoke(
        interfaceName: String,
        interfaceVisibility: InterfaceVisibility,
        factoryVisibility: FactoryVisibility,
        implementationVisibility: ImplementationVisibility,
    ) {
        if (interfaceVisibility == InterfaceVisibility.INTERNAL && factoryVisibility == FactoryVisibility.PUBLIC) {
            throw IllegalStateException(
                "Cannot generate public factory for internal interface $interfaceName. " +
                    "Set factoryVisibility to INTERNAL or make the interface PUBLIC.",
            )
        }
        if (
            interfaceVisibility == InterfaceVisibility.INTERNAL &&
            implementationVisibility == ImplementationVisibility.PUBLIC
        ) {
            throw IllegalStateException(
                "Cannot generate public implementation for internal interface $interfaceName. " +
                    "Set implementationVisibility to INTERNAL or PRIVATE, or make the interface PUBLIC.",
            )
        }
    }
}
