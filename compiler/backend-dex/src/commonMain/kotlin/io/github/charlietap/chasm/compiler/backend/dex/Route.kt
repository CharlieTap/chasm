package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.dex.abi.FITS_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.abi.SPLIT_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.abi.WHOLE_FUNCTION_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.analysis.DexSizeEstimate
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionFacts
import io.github.charlietap.chasm.compiler.backend.dex.analysis.analyzeFunction
import io.github.charlietap.chasm.compiler.backend.dex.analysis.estimateDexSize

internal enum class LoweringStrategy {
    WholeInOrder,
    Helper,
    Inline,
    Split,
}

internal enum class Route(
    val strategies: Array<LoweringStrategy>,
) {
    TryInOrder(arrayOf(LoweringStrategy.WholeInOrder, LoweringStrategy.Split)),
    WholeHelper(arrayOf(LoweringStrategy.Helper, LoweringStrategy.Split, LoweringStrategy.Inline)),
    Split(arrayOf(LoweringStrategy.Split, LoweringStrategy.WholeInOrder)),
    SplitOnly(arrayOf(LoweringStrategy.Split)),
}

internal data class WholeFunctionAnalysis(
    val analysis: FunctionAnalysis,
    val estimate: DexSizeEstimate,
)

internal fun wholeFunctionAnalysis(
    module: ModuleLowering,
    function: CirFunction,
    facts: FunctionFacts,
): WholeFunctionAnalysis? {

    if (module.options.splitInstructions != null || function.instructions.size > WHOLE_FUNCTION_INSTRUCTIONS) {
        return null
    }

    val analysis = analyzeFunction(module, function, facts, facts.memoryMode)

    return WholeFunctionAnalysis(analysis, estimateDexSize(module, function, analysis))
}

internal fun interface FunctionRouter {
    fun route(whole: WholeFunctionAnalysis?): Route
}

internal class EstimateRouter : FunctionRouter {
    override fun route(whole: WholeFunctionAnalysis?): Route {
        return when {
            whole == null -> Route.SplitOnly
            whole.estimate.inline < SPLIT_CODE_UNITS -> Route.TryInOrder
            !whole.analysis.usesInlineMemory -> Route.Split
            whole.estimate.helper <= FITS_CODE_UNITS -> Route.WholeHelper
            whole.estimate.helper >= SPLIT_CODE_UNITS -> Route.Split
            else -> Route.TryInOrder
        }
    }
}
