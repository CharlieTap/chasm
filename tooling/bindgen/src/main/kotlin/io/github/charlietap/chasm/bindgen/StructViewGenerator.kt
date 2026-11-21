package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.charlietap.chasm.bindgen.ext.asTypeName

internal class StructViewGenerator {
    operator fun invoke(view: StructView): TypeSpec {

        val constructor = FunSpec.constructorBuilder()
            .addParameter("reference", STRUCT_REFERENCE_CLASS_NAME)
            .build()
        val reference = PropertySpec.builder("reference", STRUCT_REFERENCE_CLASS_NAME)
            .initializer("reference")
            .build()

        return TypeSpec.classBuilder(view.className)
            .addAnnotation(JVM_INLINE)
            .addModifiers(KModifier.VALUE)
            .primaryConstructor(constructor)
            .addProperty(reference)
            .addProperties(view.fields.map(::fieldProperty))
            .build()
    }

    private fun fieldProperty(field: StructViewField): PropertySpec {

        val type = field.type.asTypeName()
        val getter = FunSpec.getterBuilder()
            .addStatement("return %L", fieldReadExpression(field))
            .build()

        return PropertySpec.builder(field.name, type).apply {

            mutable(field.mutable)
            getter(getter)

            if (field.mutable) {
                setter(
                    FunSpec.setterBuilder()
                        .addParameter("newValue", type)
                        .addStatement("%L", fieldWriteExpression(field))
                        .build(),
                )
            }
        }.build()
    }

    private fun fieldReadExpression(field: StructViewField): CodeBlock {
        return if (field.type.isReference) {
            referenceToValue(CodeBlock.of("%M(reference, %L)", READ_STRUCT_REFERENCE, field.index), field.type)
        } else {
            wordToValue(CodeBlock.of("%M(reference, %L)", READ_STRUCT_FIELD, field.index), field.type)
        }
    }

    private fun fieldWriteExpression(field: StructViewField): CodeBlock {

        val value = CodeBlock.of("newValue")

        return if (field.type.isReference) {
            CodeBlock.of("%M(reference, %L, %L)", WRITE_STRUCT_REFERENCE, field.index, valueToReference(value, field.type))
        } else {
            CodeBlock.of("%M(reference, %L, %L)", WRITE_STRUCT_FIELD, field.index, valueToWord(value, field.type))
        }
    }
}

private val JVM_INLINE = ClassName("kotlin.jvm", "JvmInline")
private val READ_STRUCT_FIELD = MemberName(COMPILED_PACKAGE, "readStructField")
private val READ_STRUCT_REFERENCE = MemberName(COMPILED_PACKAGE, "readStructReference")
private val WRITE_STRUCT_FIELD = MemberName(COMPILED_PACKAGE, "writeStructField")
private val WRITE_STRUCT_REFERENCE = MemberName(COMPILED_PACKAGE, "writeStructReference")
