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
    private val classImplementationGenerator: ClassImplementationGenerator = ClassImplementationGenerator(),
) {
    operator fun invoke(
        wasmInterface: WasmInterface,
    ): List<FileSpec> {

        val interfaceFile = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName).apply {
            wasmInterface.types.forEach { type ->
                addType(dataClassGenerator(wasmInterface.packageName, type))
            }
            addType(classInterfaceGenerator(wasmInterface.packageName, wasmInterface.interfaceName, wasmInterface))
        }.build()

        val implementationFile = FileSpec.builder(wasmInterface.packageName, wasmInterface.interfaceName + "Impl").apply {
            addType(classImplementationGenerator(wasmInterface.packageName, wasmInterface.interfaceName, wasmInterface))
        }.build()

        return listOf(interfaceFile, implementationFile)
    }
}
