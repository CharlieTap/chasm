package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.gradle.ext.asValue
import io.github.charlietap.chasm.vm.WasmVirtualMachine

internal class PointerAndLengthStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args).%M { (pointer, length) ->\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value, (length as %T).value)\n" +
                "}.expect(%S)",
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            READ_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class NullTerminatedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args).%M { (pointer) ->\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value)\n" +
                "}.expect(%S)",
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            READ_NULL_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class LengthPrefixedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args).%M { (pointer) ->\n" +
                "    val length = virtualMachine.%L(store, memory, (pointer as %T).value).%M(%S)\n" +
                "    virtualMachine.%L(store, memory, (pointer as %T).value + 4, length)\n" +
                "}.expect(%S)",
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            READ_INT_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            EXPECT_RESULT_FUNCTION,
            "Failed to read string length",
            READ_STRING_FUNCTION,
            WasmVirtualMachine.Value.I32::class,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class PackedStringFunctionReturnGenerator {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ) = CodeBlock.builder().apply {
        add(
            "virtualMachine.%L(store, instance, %S, args).%M { (pointerAndLength) ->\n" +
                "    val packed = (pointerAndLength as %T).value\n" +
                "    val pointer = (packed ushr 32).toInt()\n" +
                "    val length = packed.toInt()\n" +
                "    virtualMachine.%L(store, memory, pointer, length)\n" +
                "}.expect(%S)",
            INVOKE_FUNCTION,
            proxy.name,
            FLATMAP_RESULT_FUNCTION,
            WasmVirtualMachine.Value.I64::class,
            READ_STRING_FUNCTION,
            "Failed to invoke function ${function.name}",
        )
    }.build()
}

internal class StringFunctionReturnImplementationGenerator(
    private val pointerAndLengthStrategy: PointerAndLengthStringFunctionReturnGenerator = PointerAndLengthStringFunctionReturnGenerator(),
    private val nullTerminatedStrategy: NullTerminatedStringFunctionReturnGenerator = NullTerminatedStringFunctionReturnGenerator(),
    private val lengthPrefixedStrategy: LengthPrefixedStringFunctionReturnGenerator = LengthPrefixedStringFunctionReturnGenerator(),
    private val packedStringStrategy: PackedStringFunctionReturnGenerator = PackedStringFunctionReturnGenerator(),
) {
    operator fun invoke(
        function: Function,
        proxy: FunctionProxy,
    ): CodeBlock = when (requireNotNull(function.returns.stringEncodingStrategy)) {
        StringEncodingStrategy.POINTER_AND_LENGTH -> pointerAndLengthStrategy(function, proxy)
        StringEncodingStrategy.NULL_TERMINATED -> nullTerminatedStrategy(function, proxy)
        StringEncodingStrategy.LENGTH_PREFIXED -> lengthPrefixedStrategy(function, proxy)
        StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> packedStringStrategy(function, proxy)
    }
}

internal class FunctionReturnImplementationGenerator(
    private val stringReturnGenerator: StringFunctionReturnImplementationGenerator = StringFunctionReturnImplementationGenerator(),
) {
    operator fun invoke(
        builder: FunSpec.Builder,
        function: Function,
        proxy: FunctionProxy,
        returnType: TypeName,
        freeAllocs: List<String>,
    ) = builder.apply {
        when (val type = function.returns.type) {
            Scalar.Integer,
            Scalar.Long,
            Scalar.Float,
            Scalar.Double,
            -> {
                addStatement(
                    "val result = virtualMachine.%L(store, instance, %S, args).%M { (it.first() as %T).value }.%M(%S)",
                    INVOKE_FUNCTION,
                    proxy.name,
                    MAP_RESULT_FUNCTION,
                    function.returns.type.asValue(),
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }

            Scalar.Unit -> {
                addStatement(
                    "virtualMachine.%L(store, instance, %S, args).%M(%S)",
                    INVOKE_FUNCTION,
                    proxy.name,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
            }

            Scalar.String -> {
                val expr = stringReturnGenerator(function, proxy)
                addStatement("val result = %L", expr)
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }

            is Aggregate -> {
                val generatedType = type.generated.fields.mapIndexed { idx, field ->
                    CodeBlock.of("r%L = (it[%L] as %T).value", idx, idx, field.type.asValue())
                }.joinToCode(",\n")

                addStatement(
                    """
                    val result = virtualMachine.%L(store, instance, %S, args).%M {
                        %T(
                            %L
                        )
                    }.%M(%S)
                    """.trimIndent(),
                    INVOKE_FUNCTION,
                    proxy.name,
                    MAP_RESULT_FUNCTION,
                    returnType,
                    generatedType,
                    EXPECT_RESULT_FUNCTION,
                    "Failed to invoke function ${function.name}",
                )
                freeAllocs.forEach { allocVar -> addStatement("allocator.free(%L)", allocVar) }
                addStatement("return result")
            }
        }
    }
}
