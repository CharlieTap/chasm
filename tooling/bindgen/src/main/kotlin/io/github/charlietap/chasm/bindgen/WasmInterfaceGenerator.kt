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

        return listOf(
            interfaceFile(interfaceVisibility, wasmInterface, config.storeControls),
            implementationFile(factoryVisibility, wasmInterface, implementationVisibility, config),
        )
    }

    private fun interfaceFile(
        interfaceVisibility: InterfaceVisibility,
        wasmInterface: WasmInterface,
        controls: List<StoreControl>,
    ): FileSpec = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName).apply {
        wasmInterface.types.forEach { type ->
            addType(dataClassGenerator(wasmInterface.packageName, type))
        }
        addType(classInterfaceGenerator(wasmInterface.packageName, wasmInterface.interfaceName, interfaceVisibility, wasmInterface, controls))
    }.build()

    private fun implementationFile(
        factoryVisibility: FactoryVisibility,
        wasmInterface: WasmInterface,
        implementationVisibility: ImplementationVisibility,
        config: CodegenConfig,
    ): FileSpec = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName + "Impl").apply {
        if (config.executionTier !is ExecutionTier.Interpreter) {
            addAnnotation(
                AnnotationSpec.builder(OPT_IN_CLASS_NAME)
                    .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
                    .addMember("%T::class", CHASM_EXPERIMENTAL_API_CLASS_NAME)
                    .build(),
            )
        }
        val controls = config.storeControls
        controls.forEach { control -> addAliasedImport(control.function, control.alias) }
        if (config.executionTier is ExecutionTier.Aot) addAliasedImport(CHASM_ADD_FUEL, CHASM_ADD_FUEL_ALIAS)
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
                    linksWasiPreview1 = wasmInterface.linksWasiPreview1,
                    config = config,
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
                    controls = controls,
                )
            },
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
