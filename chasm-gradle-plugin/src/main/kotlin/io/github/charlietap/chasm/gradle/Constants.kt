package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import io.github.charlietap.chasm.vm.Import
import io.github.charlietap.chasm.vm.Instance
import io.github.charlietap.chasm.vm.Memory
import io.github.charlietap.chasm.vm.Module
import io.github.charlietap.chasm.vm.Store
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import io.github.charlietap.chasm.vm.codegen.CodegenImport

internal val WASM_VIRTUAL_MACHINE_CLASS_NAME = WasmVirtualMachine::class.asClassName()
internal val STORE_CLASS_NAME = Store::class.asClassName()
internal val MODULE_CLASS_NAME = Module::class.asClassName()
internal val INSTANCE_CLASS_NAME = Instance::class.asClassName()
internal val MEMORY_CLASS_NAME = Memory::class.asClassName()
internal val CODEGEN_IMPORT_CLASS_NAME = CodegenImport::class.asClassName()
internal val CODEGEN_IMPORT_LIST_CLASS_NAME = List::class.asClassName().parameterizedBy(CODEGEN_IMPORT_CLASS_NAME)
internal val IMPORT_CLASS_NAME = Import::class.asClassName()
internal val IMPORT_LIST_CLASS_NAME = List::class.asClassName().parameterizedBy(IMPORT_CLASS_NAME)
internal val MODULE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "ModuleFactory")
internal val INSTANCE_FACTORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.vm", "InstanceFactory")
internal val VM_FACTORY_CLASS_NAME = MemberName("io.github.charlietap.chasm.vm", "virtualMachineFactory")
internal val IMPORT_FACTORY_CLASS_NAME = MemberName("io.github.charlietap.chasm.vm", "importFactory")

internal const val CREATE_STORE_FUNCTION = "storeInit"
internal const val CREATE_MODULE_FUNCTION = "moduleDecode"
internal const val CREATE_INSTANCE_FUNCTION = "moduleInstantiate"
internal const val INVOKE_FUNCTION = "functionInvoke"

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
