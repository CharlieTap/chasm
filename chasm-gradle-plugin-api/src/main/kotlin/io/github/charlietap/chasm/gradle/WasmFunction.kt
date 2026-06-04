package io.github.charlietap.chasm.gradle

import java.io.Serializable
import kotlin.ExperimentalVersionOverloading
import kotlin.IntroducedAt

data class FunctionParameterDefinition(
    val name: String,
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
    val stringAllocationStrategy: StringAllocationStrategy? = null,
) : Serializable

data class ReturnTypeDefinition(
    val type: Type,
    val stringEncodingStrategy: StringEncodingStrategy? = null,
) : Serializable

data class WasmFunction(
    val name: String,
    val parameters: List<FunctionParameterDefinition>,
    val returnType: ReturnTypeDefinition,
) : Serializable

class WasmFunctionBuilder(
    val name: String,
) {
    private val parameters: MutableList<FunctionParameterDefinition> = []
    private var returnType: ReturnTypeDefinition = ReturnTypeDefinition(Scalar.Unit)

    fun intParam(name: String) {
        parameters.add(FunctionParameterDefinition(name, Scalar.Integer))
    }

    fun longParam(name: String) {
        parameters.add(FunctionParameterDefinition(name, Scalar.Long))
    }

    fun floatParam(name: String) {
        parameters.add(FunctionParameterDefinition(name, Scalar.Float))
    }

    fun doubleParam(name: String) {
        parameters.add(FunctionParameterDefinition(name, Scalar.Double))
    }

    @OptIn(ExperimentalVersionOverloading::class)
    fun stringParam(
        name: String,
        @IntroducedAt("2.0.2")
        encodingStrategy: StringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
        @IntroducedAt("2.0.2")
        freeAfterCall: Boolean = false,
    ) {
        parameters.add(
            FunctionParameterDefinition(
                name,
                Scalar.String,
                encodingStrategy,
                StringAllocationStrategy(freeAfterCall),
            ),
        )
    }

    fun intReturnType() {
        returnType = ReturnTypeDefinition(Scalar.Integer)
    }

    fun longReturnType() {
        returnType = ReturnTypeDefinition(Scalar.Long)
    }

    fun floatReturnType() {
        returnType = ReturnTypeDefinition(Scalar.Float)
    }

    fun doubleReturnType() {
        returnType = ReturnTypeDefinition(Scalar.Double)
    }

    @OptIn(ExperimentalVersionOverloading::class)
    fun stringReturnType(
        @IntroducedAt("2.0.2")
        encodingStrategy: StringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
    ) {
        returnType = ReturnTypeDefinition(Scalar.String, encodingStrategy)
    }

    fun build() = WasmFunction(
        name = name,
        parameters = parameters,
        returnType = returnType,
    )
}
