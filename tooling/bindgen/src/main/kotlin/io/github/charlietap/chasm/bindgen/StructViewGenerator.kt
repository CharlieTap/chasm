package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.charlietap.chasm.bindgen.ext.asTypeName
import io.github.charlietap.chasm.storage.layout.FieldSlots
import io.github.charlietap.chasm.storage.layout.InlineSlot
import io.github.charlietap.chasm.storage.layout.NumberSlot
import io.github.charlietap.chasm.storage.layout.ReferenceSlot
import io.github.charlietap.chasm.storage.layout.Slot
import io.github.charlietap.chasm.storage.layout.SpilledSlot
import io.github.charlietap.chasm.storage.layout.StructLayout
import io.github.charlietap.chasm.storage.layout.TypeCheck
import io.github.charlietap.chasm.storage.layout.WideNumberSlots
import io.github.charlietap.chasm.type.PackedType

internal class StructViewGenerator {
    operator fun invoke(view: StructView): TypeSpec {

        val constructor = FunSpec.constructorBuilder()
            .addParameter("reference", STRUCT_REFERENCE_CLASS_NAME)
            .build()
        val reference = PropertySpec.builder("reference", STRUCT_REFERENCE_CLASS_NAME)
            .initializer("reference")
            .build()
        val layout = view.layout

        return TypeSpec.classBuilder(view.className).apply {

            addAnnotation(JVM_INLINE)
            addModifiers(KModifier.VALUE)
            primaryConstructor(constructor)
            addProperty(reference)

            if (layout == null) {
                addProperties(view.fields.map(::fieldProperty))
            } else {
                addAnnotation(
                    AnnotationSpec.builder(OPT_IN_CLASS_NAME)
                        .addMember("%T::class", CHASM_INTERNAL_API_CLASS_NAME)
                        .build(),
                )
                addProperties(view.fields.map { field -> slotProperty(field, layout.fields[field.index].slots) })
                addFunction(storageFunction(view.className, layout))
            }
        }.build()
    }

    private fun fieldProperty(field: StructViewField): PropertySpec {

        val getter = FunSpec.getterBuilder()
            .addStatement("return %L", fieldReadExpression(field))
            .build()
        val setter = setterBuilder(field)
            .addStatement("%L", fieldWriteExpression(field))
            .build()

        return property(field, getter, setter)
    }

    private fun slotProperty(
        field: StructViewField,
        slots: FieldSlots,
    ): PropertySpec {

        val getter = FunSpec.getterBuilder()
        val setter = setterBuilder(field)

        when (slots) {
            is NumberSlot -> numberSlot(field, slots, getter, setter)
            is WideNumberSlots -> wideNumberSlots(field, slots, getter, setter)
            is ReferenceSlot -> referenceSlot(field, slots, getter, setter)
        }

        return property(field, getter.build(), setter.build())
    }

    private fun numberSlot(
        field: StructViewField,
        numberSlot: NumberSlot,
        getter: FunSpec.Builder,
        setter: FunSpec.Builder,
    ) {

        val access = slotRead("storage()", numberSlot.slot)

        if (field.type == Scalar.Float) {
            getter.addStatement("return %T.fromBits(%L)", FLOAT, access)
            setter.addStatement("%L = newValue.toRawBits()", access)
        } else {
            getter.addStatement("return %L", access)
            setter.addStatement("%L = %L", access, packedValue(numberSlot.packed))
        }
    }

    private fun referenceSlot(
        field: StructViewField,
        referenceSlot: ReferenceSlot,
        getter: FunSpec.Builder,
        setter: FunSpec.Builder,
    ) {

        getter.addStatement("return %L", referenceToValue(slotRead("storage()", referenceSlot.slot), field.type))

        val check = referenceSlot.check
        if (check != null) {
            checkedReferenceWrite(field, referenceSlot.slot, check, setter)
        } else {
            setter.addStatement("%L", fieldWriteExpression(field))
        }
    }

    private fun wideNumberSlots(
        field: StructViewField,
        slots: WideNumberSlots,
        getter: FunSpec.Builder,
        setter: FunSpec.Builder,
    ) {

        val low = slots.low
        val high = slots.high
        val bits = CodeBlock.of(
            "(%L.toLong() and 0xFFFFFFFFL) or (%L.toLong() shl 32)",
            slotRead("storage", low),
            slotRead("storage", high),
        )
        val value = if (field.type == Scalar.Double) "bits" else "newValue"

        getter.addStatement("val storage = storage()")
        setter.addStatement("val storage = storage()")

        if (field.type == Scalar.Double) {
            getter.addStatement("return %T.fromBits(%L)", DOUBLE, bits)
            setter.addStatement("val bits = newValue.toRawBits()")
        } else {
            getter.addStatement("return %L", bits)
        }

        setter.addStatement("%L = %L.toInt()", slotRead("storage", low), value)
        setter.addStatement("%L = (%L ushr 32).toInt()", slotRead("storage", high), value)
    }

    private fun checkedReferenceWrite(
        field: StructViewField,
        slot: Slot,
        check: TypeCheck,
        setter: FunSpec.Builder,
    ) {

        val compiledClass = when (val type = field.type) {
            is StructViewType -> COMPILED_STRUCT_CLASS_NAME.copy(nullable = type.nullable)
            is ArrayReferenceType -> COMPILED_ARRAY_CLASS_NAME.copy(nullable = type.nullable)
            else -> error("Field ${field.index} does not hold a struct or array reference")
        }
        val sameStoreAndType = CodeBlock.of("value.rtt.engine·===·storage.rtt.engine && %L", rttCheck("value.rtt", check))
        val acceptsValue = if (compiledClass.isNullable) {
            CodeBlock.of("value·==·null || (%L)", sameStoreAndType)
        } else {
            sameStoreAndType
        }

        setter.addStatement("val storage = storage()")
        setter.addStatement("val value = %L as %T", valueToReference(CodeBlock.of("newValue"), field.type), compiledClass)
        setter.addStatement("require(%L) { %S }", acceptsValue, "the value does not match the type of field ${field.index}")
        setter.addStatement("%L = value", slotRead("storage", slot))
    }

    private fun storageFunction(
        className: ClassName,
        layout: StructLayout,
    ): FunSpec {

        val storageClass = ClassName.bestGuess(layout.storageClass)

        return FunSpec.builder("storage")
            .addModifiers(KModifier.PRIVATE)
            .returns(storageClass)
            .addStatement("val struct = reference as %T", storageClass)
            .addStatement("require(%L) { %S }", rttCheck("struct.rtt", layout.check), "the struct is not a ${className.simpleName}")
            .addStatement("return struct")
            .build()
    }

    private fun rttCheck(
        rtt: String,
        check: TypeCheck,
    ): CodeBlock {
        return when {
            check.final -> CodeBlock.of("%L.key·===·%S", rtt, check.rttKey)
            check.supertypes == 0 -> CodeBlock.of("%L.supertypes[0].key·===·%S", rtt, check.rttKey)
            else -> CodeBlock.of("%L.depth·>=·%L && %L.supertypes[%L].key·===·%S", rtt, check.supertypes, rtt, check.supertypes, check.rttKey)
        }
    }

    private fun slotRead(
        receiver: String,
        slot: Slot,
    ): CodeBlock {
        return when (slot) {
            is InlineSlot -> CodeBlock.of("%L.%L", receiver, slot.field)
            is SpilledSlot -> CodeBlock.of("%L.%L!![%L]", receiver, slot.array, slot.index)
        }
    }

    private fun packedValue(packedType: PackedType?): CodeBlock {
        return when (packedType) {
            PackedType.I8 -> CodeBlock.of("newValue and 0xFF")
            PackedType.I16 -> CodeBlock.of("newValue and 0xFFFF")
            null -> CodeBlock.of("newValue")
        }
    }

    private fun setterBuilder(field: StructViewField): FunSpec.Builder {
        return FunSpec.setterBuilder().addParameter("newValue", field.type.asTypeName())
    }

    private fun property(
        field: StructViewField,
        getter: FunSpec,
        setter: FunSpec,
    ): PropertySpec {
        return PropertySpec.builder(field.name, field.type.asTypeName()).apply {

            mutable(field.mutable)
            getter(getter)

            if (field.mutable) {
                setter(setter)
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

private const val REFERENCE_PACKAGE = "io.github.charlietap.chasm.embedding.reference"
private const val COMPILED_GC_PACKAGE = "io.github.charlietap.chasm.runtime.compiled.gc"

private val JVM_INLINE = ClassName("kotlin.jvm", "JvmInline")
private val COMPILED_STRUCT_CLASS_NAME = ClassName(COMPILED_GC_PACKAGE, "CompiledStruct")
private val COMPILED_ARRAY_CLASS_NAME = ClassName(COMPILED_GC_PACKAGE, "CompiledArray")
private val READ_STRUCT_FIELD = MemberName(REFERENCE_PACKAGE, "readStructField")
private val READ_STRUCT_REFERENCE = MemberName(REFERENCE_PACKAGE, "readStructReference")
private val WRITE_STRUCT_FIELD = MemberName(REFERENCE_PACKAGE, "writeStructField")
private val WRITE_STRUCT_REFERENCE = MemberName(REFERENCE_PACKAGE, "writeStructReference")
