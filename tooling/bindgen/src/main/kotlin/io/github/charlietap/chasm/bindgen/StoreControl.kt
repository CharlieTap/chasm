package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT

internal class StoreControl(
    val name: String,
    val parameters: List<ParameterSpec>,
    val returnType: TypeName,
    val function: MemberName,
    val alias: String,
    val failure: String,
) {
    fun signature(): FunSpec.Builder = FunSpec.builder(name)
        .addParameters(parameters)
        .returns(returnType)
}

internal val CodegenConfig.storeControls: List<StoreControl>
    get() = buildList {
        if (cpu is CpuLimits.Metered) {
            add(
                StoreControl(
                    name = "addFuel",
                    parameters = listOf(ParameterSpec("units", LONG)),
                    returnType = UNIT,
                    function = CHASM_ADD_FUEL,
                    alias = CHASM_ADD_FUEL_ALIAS,
                    failure = "Failed to add fuel",
                ),
            )
            add(
                StoreControl(
                    name = "remainingFuel",
                    parameters = emptyList(),
                    returnType = LONG,
                    function = CHASM_REMAINING_FUEL,
                    alias = CHASM_REMAINING_FUEL_ALIAS,
                    failure = "Failed to read fuel",
                ),
            )
        }
        if (interrupts) {
            add(
                StoreControl(
                    name = "interrupt",
                    parameters = emptyList(),
                    returnType = BOOLEAN,
                    function = CHASM_INTERRUPT,
                    alias = CHASM_INTERRUPT_ALIAS,
                    failure = "Failed to interrupt",
                ),
            )
        }
    }
