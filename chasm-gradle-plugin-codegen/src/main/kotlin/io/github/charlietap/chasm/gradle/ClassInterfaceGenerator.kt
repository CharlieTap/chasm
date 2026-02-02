package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.gradle.ext.asTypeName

internal class FunctionGenerator {
    operator fun invoke(
        packageName: String,
        function: Function,
    ) = FunSpec.builder(function.name).apply {
        addModifiers(KModifier.ABSTRACT)
        function.params.forEach { param ->
            addParameter(param.name, param.type.asTypeName())
        }
        val type = when (val type = function.returns.type) {
            Scalar.Integer -> INT
            Scalar.Long -> LONG
            Scalar.Float -> FLOAT
            Scalar.Double -> DOUBLE
            Scalar.String -> STRING
            Scalar.Unit -> UNIT
            is Aggregate -> ClassName(packageName, type.generated.name)
            else -> throw IllegalArgumentException("Unsupported return type: $type")
        }
        returns(type)
    }.build()
}

internal class PropertyGenerator {
    operator fun invoke(
        property: Property,
    ) = PropertySpec.builder(property.name, property.type.asTypeName()).apply {
        mutable(property.const.not())
    }.build()
}

internal class ClassInterfaceGenerator(
    private val functionGenerator: FunctionGenerator = FunctionGenerator(),
    private val propertyGenerator: PropertyGenerator = PropertyGenerator(),
) {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: TypeVisibility,
        wasmInterface: WasmInterface,
    ) = TypeSpec.interfaceBuilder(interfaceName).apply {

        val visibilityModifier = when (visibility) {
            TypeVisibility.INTERNAL -> KModifier.INTERNAL
            TypeVisibility.PUBLIC -> KModifier.PUBLIC
        }
        addModifiers(visibilityModifier)

        wasmInterface.functions.forEach { function ->
            addFunction(functionGenerator(packageName, function))
        }

        wasmInterface.properties.forEach { property ->
            addProperty(propertyGenerator(property))
        }
    }.build()
}
