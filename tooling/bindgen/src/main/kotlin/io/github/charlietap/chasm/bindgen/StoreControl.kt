package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import io.github.charlietap.chasm.config.CpuLimits

internal class StoreControl(
    val name: String,
    val parameters: List<ParameterSpec>,
    val returnType: TypeName,
    val function: StoreFunction,
) {
    fun signature(): FunSpec.Builder = FunSpec.builder(name)
        .addParameters(parameters)
        .returns(returnType)
}

internal val CodegenConfig.storeControls: List<StoreControl>
    get() = buildList {
        if (runtimeConfig.limits.cpu is CpuLimits.Metered) {
            add(
                StoreControl(
                    name = "addFuel",
                    parameters = listOf(ParameterSpec("units", LONG)),
                    returnType = UNIT,
                    function = StoreFunction.AddFuel,
                ),
            )
            add(
                StoreControl(
                    name = "remainingFuel",
                    parameters = emptyList(),
                    returnType = LONG,
                    function = StoreFunction.RemainingFuel,
                ),
            )
        }
        if (runtimeConfig.interrupts) {
            add(
                StoreControl(
                    name = "interrupt",
                    parameters = emptyList(),
                    returnType = BOOLEAN,
                    function = StoreFunction.Interrupt,
                ),
            )
        }
    }

internal enum class StoreFunction(
    val functionName: String,
    val alias: String,
) {
    AddFuel("addFuel", "chasmAddFuel"),
    RemainingFuel("remainingFuel", "chasmRemainingFuel"),
    Interrupt("interrupt", "chasmInterrupt"),
}
