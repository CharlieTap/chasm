package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT

internal class DataClassGenerator
{
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

internal class FunctionGenerator
{
    operator fun invoke(
        packageName: String,
        function: Function,
    ) = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.ABSTRACT)
        function.params.forEach { param ->
            addParameter(param.name, param.type.asTypeName())
        }
        val type = when(val type = function.returns.type) {
            Scalar.Integer -> INT
            Scalar.Long -> LONG
            Scalar.Float -> FLOAT
            Scalar.Double -> DOUBLE
            Scalar.String -> STRING
            Scalar.Unit -> UNIT
            is Aggregate -> ClassName(packageName, type.generated.name)
        }
        returns(type)
    }.build()
}

internal class PropertyGenerator
{
    operator fun invoke(
        property: Property,
    ) = PropertySpec.builder(property.name, property.type.asTypeName()).apply{
        mutable(property.const.not())
    }.build()
}

internal class WasmInterfaceGenerator(
    private val dataClassGenerator: DataClassGenerator = DataClassGenerator(),
    private val functionGenerator: FunctionGenerator = FunctionGenerator(),
    private val propertyGenerator: PropertyGenerator = PropertyGenerator(),
) {
    operator fun invoke(
        name: String,
        packageName: String,
        wasmInterface: WasmInterface,
    ): FileSpec = FileSpec.builder(packageName, name).apply {

        wasmInterface.types.forEach { type ->
           addType(dataClassGenerator(packageName, type))
        }

        val builder = TypeSpec.interfaceBuilder(name)

        wasmInterface.functions.forEach { function ->
            builder.addFunction(functionGenerator(packageName, function))
        }

        wasmInterface.properties.forEach { property ->
            builder.addProperty(propertyGenerator(property))
        }

        addType(builder.build())
    }.build()
}
