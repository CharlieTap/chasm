package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag

internal class DataClassGenerator
{
    operator fun invoke(
        type: GeneratedType,
    ): TypeSpec = TypeSpec.classBuilder(type.name).apply {
        addModifiers(KModifier.DATA)

        val constructor = FunSpec.constructorBuilder()

        type.fields.forEach { field ->

            val typeName = field.type.asTypeName()
            constructor.addParameter(field.name, typeName)
            addProperty(PropertySpec.builder(field.name, typeName).build())
        }
        primaryConstructor(constructor.build())
    }.build()
}

internal class FunctionGenerator
{
    operator fun invoke(
        function: Function,
    ) = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.ABSTRACT)
        function.params.forEach { param ->
            addParameter(param.name, param.type.asTypeName())
        }
        when(function.returns) {
            is FunctionReturn.Primitive -> returns(function.returns.type.asTypeName())
            FunctionReturn.String -> returns(STRING)
            is FunctionReturn.Type -> TODO()
            FunctionReturn.Unit -> returns(UNIT)
        }
    }.build()
}

internal class PropertyGenerator
{
    operator fun invoke(
        property: Property,
    ): PropertySpec {

        val clazz = when(property) {
            is Property.GlobalProperty -> Global::class
            is Property.MemoryProperty -> Memory::class
            is Property.TableProperty -> Table::class
            is Property.TagProperty -> Tag::class
        }
        return PropertySpec.builder(property.name, clazz).build()
    }
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
    ): FileSpec = FileSpec.builder(name, packageName).apply {


        wasmInterface.types.forEach { type ->
           addType(dataClassGenerator(type))
        }

        val builder = TypeSpec.interfaceBuilder(name)

        wasmInterface.properties.forEach { property ->
            builder.addProperty(propertyGenerator(property))
        }

        wasmInterface.functions.forEach { function ->
            builder.addFunction(functionGenerator(function))
        }

        addType(builder.build())
    }.build()
}
