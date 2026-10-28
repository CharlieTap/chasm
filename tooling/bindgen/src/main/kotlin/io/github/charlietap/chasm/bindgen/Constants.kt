package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName

private const val VM_PACKAGE = "io.github.charlietap.chasm.vm"

internal val WASM_VIRTUAL_MACHINE_CLASS_NAME = ClassName(VM_PACKAGE, "WasmVirtualMachine")
internal val SUSPENDING_WASM_VIRTUAL_MACHINE_CLASS_NAME = ClassName(VM_PACKAGE, "SuspendingWasmVirtualMachine")
internal val WASM32_ALLOCATOR_CLASS_NAME = ClassName(VM_PACKAGE, "Wasm32Allocator")
internal val STORE_CLASS_NAME = ClassName(VM_PACKAGE, "Store")
internal val MODULE_CLASS_NAME = ClassName(VM_PACKAGE, "Module")
internal val INSTANCE_CLASS_NAME = ClassName(VM_PACKAGE, "Instance")
internal val GLOBAL_CLASS_NAME = ClassName(VM_PACKAGE, "Global")
internal val MEMORY_CLASS_NAME = ClassName(VM_PACKAGE, "Memory")
internal val PREPARED_FUNCTION_CLASS_NAME = ClassName(VM_PACKAGE, "PreparedFunction")
internal val VALUE_CLASS_NAME = WASM_VIRTUAL_MACHINE_CLASS_NAME.nestedClass("Value")
internal val VALUE_I32_CLASS_NAME = VALUE_CLASS_NAME.nestedClass("I32")
internal val VALUE_I64_CLASS_NAME = VALUE_CLASS_NAME.nestedClass("I64")
internal val VALUE_F32_CLASS_NAME = VALUE_CLASS_NAME.nestedClass("F32")
internal val VALUE_F64_CLASS_NAME = VALUE_CLASS_NAME.nestedClass("F64")
internal val VALUE_MUTABLE_LIST_CLASS_NAME = ClassName("kotlin.collections", "MutableList").parameterizedBy(VALUE_CLASS_NAME)
internal val VALUE_TYPE_CLASS_NAME = ClassName(VM_PACKAGE, "ValueType")
internal val VALUE_TYPE_LIST_CLASS_NAME = List::class.asClassName().parameterizedBy(VALUE_TYPE_CLASS_NAME)
internal val VALUE_TYPE_NUMBER_CLASS_NAME = VALUE_TYPE_CLASS_NAME.nestedClass("Number")
internal val NUMBER_TYPE_CLASS_NAME = ClassName(VM_PACKAGE, "NumberType")
internal val CODEGEN_IMPORT_CLASS_NAME = ClassName("$VM_PACKAGE.codegen", "CodegenImport")
internal val CODEGEN_IMPORT_LIST_CLASS_NAME = List::class.asClassName().parameterizedBy(CODEGEN_IMPORT_CLASS_NAME)
internal val IMPORT_CLASS_NAME = ClassName(VM_PACKAGE, "Import")
internal val IMPORT_LIST_CLASS_NAME = List::class.asClassName().parameterizedBy(IMPORT_CLASS_NAME)
internal val MODULE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "ModuleFactory")
internal val INSTANCE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "InstanceFactory")
internal val SUSPEND_MODULE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "SuspendModuleFactory")
internal val SUSPEND_INSTANCE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "SuspendInstanceFactory")
internal val VM_FACTORY_CLASS_NAME = MemberName("io.github.charlietap.chasm.vm", "virtualMachineFactory")
internal val SUSPENDING_VM_FACTORY_CLASS_NAME =
    MemberName("io.github.charlietap.chasm.vm", "suspendingVirtualMachineFactory")
internal val IMPORT_FACTORY_CLASS_NAME = MemberName("io.github.charlietap.chasm.vm", "importFactory")

internal const val CREATE_STORE_FUNCTION = "storeInit"
internal const val CREATE_MODULE_FUNCTION = "moduleDecode"
internal const val CREATE_INSTANCE_FUNCTION = "moduleInstantiate"
internal const val CREATE_MODULE_SUSPENDING_FUNCTION = "moduleDecodeSuspending"
internal const val CREATE_INSTANCE_SUSPENDING_FUNCTION = "moduleInstantiateSuspending"
internal const val PREPARE_FUNCTION = "prepareFunction"
internal const val INVOKE_TYPED_FUNCTION = "functionInvokeTyped"
internal const val FUNCTION_INPUT_BUFFER_NAME = "functionInputBuffer"

internal const val EXPORT_FUNCTION = "exportFunction"
internal const val EXPORT_GLOBAL = "exportGlobal"
internal const val EXPORT_MEMORY = "exportMemory"
internal const val EXPORT_TABLE = "exportTable"

internal const val READ_GLOBAL_FUNCTION = "globalRead"
internal const val WRITE_GLOBAL_FUNCTION = "globalWrite"
internal const val READ_BYTES_FUNCTION = "memoryReadBytes"
internal const val WRITE_BYTES_FUNCTION = "memoryWriteBytes"
internal const val READ_INT_FUNCTION = "memoryReadInt"
internal const val WRITE_INT_FUNCTION = "memoryWriteInt"
internal const val READ_STRING_FUNCTION = "memoryReadUtf8String"
internal const val READ_NULL_STRING_FUNCTION = "memoryReadUtf8NullTerminatedUtf8String"

internal val MAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "map")
internal val FLATMAP_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "flatMap")

internal val EXPECT_RESULT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expect")
internal val EXPECT_VALUE_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectValue")
internal val EXPECT_INT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectInt")
internal val EXPECT_LONG_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectLong")
internal val EXPECT_FLOAT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFloat")
internal val EXPECT_DOUBLE_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectDouble")
internal val EXPECT_FIRST_VALUE_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFirstValue")
internal val EXPECT_FIRST_INT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFirstInt")
internal val EXPECT_FIRST_LONG_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFirstLong")
internal val EXPECT_FIRST_FLOAT_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFirstFloat")
internal val EXPECT_FIRST_DOUBLE_FUNCTION = MemberName("io.github.charlietap.chasm.vm", "expectFirstDouble")
