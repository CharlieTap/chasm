package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.charlietap.chasm.bindgen.ext.asTypeName

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
    private val portableVmFactoryFunctionGenerator: PortableVmFactoryFunctionGenerator = PortableVmFactoryFunctionGenerator(),
    private val portableVmClassImplementationGenerator: PortableVmClassImplementationGenerator = PortableVmClassImplementationGenerator(),
    private val chasmFactoryFunctionGenerator: ChasmFactoryFunctionGenerator = ChasmFactoryFunctionGenerator(),
    private val chasmClassImplementationGenerator: ChasmClassImplementationGenerator = ChasmClassImplementationGenerator(),
    private val compiledClassImplementationGenerator: ChasmClassImplementationGenerator = ChasmClassImplementationGenerator("CompiledImpl"),
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

        val interfaceFile = interfaceFile(interfaceVisibility, wasmInterface)
        val compiledCode = config.compiledCode?.takeIf { config.runsCompiledCode }
        if (compiledCode == null) {
            return listOf(interfaceFile, implementationFile(factoryVisibility, wasmInterface, implementationVisibility, config))
        }

        return listOf(interfaceFile, compiledFile(factoryVisibility, wasmInterface, implementationVisibility, compiledCode))
    }

    private fun interfaceFile(
        interfaceVisibility: InterfaceVisibility,
        wasmInterface: WasmInterface,
    ): FileSpec = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName).apply {
        wasmInterface.types.forEach { type ->
            addType(dataClassGenerator(wasmInterface.packageName, type))
        }
        addType(classInterfaceGenerator(wasmInterface.packageName, wasmInterface.interfaceName, interfaceVisibility, wasmInterface))
    }.build()

    private fun implementationFile(
        factoryVisibility: FactoryVisibility,
        wasmInterface: WasmInterface,
        implementationVisibility: ImplementationVisibility,
        config: CodegenConfig,
    ): FileSpec = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName + "Impl").apply {
        addFunction(
            when (config.runtime) {
                CodegenRuntime.PORTABLE_VM -> portableVmFactoryFunctionGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = factoryVisibility,
                    generateSuspendingFactory = config.generateSuspendingFactories,
                )
                CodegenRuntime.CHASM -> chasmFactoryFunctionGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = factoryVisibility,
                    generateSuspendingFactory = config.generateSuspendingFactories,
                    linksWasiPreview1 = wasmInterface.linksWasiPreview1,
                )
            },
        )
        addType(
            when (config.runtime) {
                CodegenRuntime.PORTABLE_VM -> portableVmClassImplementationGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = implementationVisibility,
                    wasmInterface = wasmInterface,
                )
                CodegenRuntime.CHASM -> chasmClassImplementationGenerator(
                    packageName = wasmInterface.packageName,
                    interfaceName = wasmInterface.interfaceName,
                    visibility = implementationVisibility,
                    wasmInterface = wasmInterface,
                )
            },
        )
    }.build()

    private fun compiledFile(
        factoryVisibility: FactoryVisibility,
        wasmInterface: WasmInterface,
        implementationVisibility: ImplementationVisibility,
        compiledCode: String,
    ): FileSpec = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName + "Compiled").apply {
        addAnnotation(
            AnnotationSpec.builder(OPT_IN_CLASS_NAME)
                .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
                .addMember("%T::class", CHASM_EXPERIMENTAL_API_CLASS_NAME)
                .build(),
        )
        addFunction(
            chasmFactoryFunctionGenerator.compiledFactory(
                packageName = wasmInterface.packageName,
                interfaceName = wasmInterface.interfaceName,
                visibility = factoryVisibility,
                compiledCode = compiledCode,
            ),
        )
        addType(
            compiledClassImplementationGenerator(
                packageName = wasmInterface.packageName,
                interfaceName = wasmInterface.interfaceName,
                visibility = implementationVisibility,
                wasmInterface = wasmInterface,
            ),
        )
    }.build()
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
