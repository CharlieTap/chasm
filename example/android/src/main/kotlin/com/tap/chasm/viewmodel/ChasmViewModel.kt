package com.tap.chasm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.chasm.FactorialService
import com.test.chasm.FibonacciService
import com.test.chasm.InteropService
import com.test.chasm.StringService
import com.test.chasm.TestService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@ViewModelKey(ChasmViewModel::class)
@Inject
class ChasmViewModel(
    private val factorialService: FactorialService,
    private val fibonacciService: FibonacciService,
    private val interopService: InteropService,
    private val stringService: StringService,
    private val testService: TestService,
) : MVIViewModel<ChasmState, ChasmEvent, ChasmEffect>() {

    init {
        factorialPrinter()
        interopPrinter()
        testPrinter()
    }

    private val nth = MutableStateFlow(ChasmState.DEFAULT.nth)
    private val fibonacci = MutableStateFlow<Int?>(null)

    private val _state = combine(nth, fibonacci) { n, fib ->

        val sliderInfo = "Compute the {${n.toInt()}} number in the fibonacci sequence"

        val result = fib?.let {
            "The calculated fibonacci number is: $fib"
        } ?: ChasmState.DEFAULT.result

        ChasmState.DEFAULT.copy(
            nth = n,
            sliderInfo = sliderInfo,
            result = result,
        )
    }
    override val state: StateFlow<ChasmState> = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChasmState.DEFAULT)

    override fun handleEvent(event: ChasmEvent) {
        when (event) {
            is ChasmEvent.ChangeFibonacciIndex -> {
                viewModelScope.launch {
                    nth.emit(event.nth)
                }
            }

            is ChasmEvent.CalculateFibonacci -> {
                viewModelScope.launch {
                    val fib = runCatching { calculateFibonacci(nth.value.toInt()) }
                    Log.d("ChasmViewModel", fib.toString())
                    fibonacci.emit(calculateFibonacci(nth.value.toInt()))
                }
            }
        }
    }

    private fun calculateFibonacci(n: Int): Int {
        return fibonacciService.fibonacci(n)
    }

    private fun factorialPrinter() {
        val factorial = factorialService.factorial(5)
        Log.d("ChasmViewModel", "factorial: $factorial")
    }

    private fun interopPrinter() {
        val interopMultipleReturn = interopService.interopMultipleReturn()
        Log.d("ChasmViewModel", "interop int: ${interopService.intFunction()}")
        Log.d("ChasmViewModel", "interop long: ${interopService.longFunction()}")
        Log.d("ChasmViewModel", "interop float: ${interopService.floatFunction()}")
        Log.d("ChasmViewModel", "interop double: ${interopService.doubleFunction()}")
        Log.d("ChasmViewModel", "interop i32 param: ${interopService.identityI32(123)}")
        Log.d("ChasmViewModel", "interop i64 param: ${interopService.identityI64(456)}")
        Log.d("ChasmViewModel", "interop f32 param: ${interopService.identityF32(7.5f)}")
        Log.d("ChasmViewModel", "interop f64 param: ${interopService.identityF64(8.25)}")
        Log.d("ChasmViewModel", "interop mixed params: ${interopService.mixedNumericFunction(1, 2, 3.5f, 4.25)}")
        Log.d(
            "ChasmViewModel",
            "interop multi return: r0 = ${interopMultipleReturn.r0}, r1 = ${interopMultipleReturn.r1}, r2 = ${interopMultipleReturn.r2}, r3 = ${interopMultipleReturn.r3}",
        )
        Log.d("ChasmViewModel", "interop pointer and length string: ${interopService.pointerAndLengthString()}")
        Log.d("ChasmViewModel", "interop length prefixed string: ${interopService.lengthPrefixedString()}")
        Log.d("ChasmViewModel", "interop null terminated string: ${interopService.nullTerminatedString()}")
        Log.d("ChasmViewModel", "interop packed string: ${interopService.packedI64String()}")
        Log.d("ChasmViewModel", "interop immutable global: ${interopService.immutableGlobal}")
        Log.d("ChasmViewModel", "interop mutable global before: ${interopService.mutableGlobal}")
        interopService.unitFunction()
        Log.d("ChasmViewModel", "interop mutable global after unit: ${interopService.mutableGlobal}")
        interopService.mutableGlobal = 512
        Log.d("ChasmViewModel", "interop mutable global after write: ${interopService.mutableGlobal}")
    }

    private fun testPrinter() {
        var mutableGlobal = testService.mutableGlobal
        Log.d("ChasmViewModel", "mutableGlobal: $mutableGlobal")
        val immutableGlobal = testService.immutableGlobal
        Log.d("ChasmViewModel", "immutableGlobal: $immutableGlobal")

        testService.mutableGlobal = 118
        mutableGlobal = testService.mutableGlobal
        Log.d("ChasmViewModel", "mutableGlobal after change: $mutableGlobal")

        val intFunction = testService.intFunction()
        Log.d("ChasmViewModel", "int function: $intFunction")

        val longFunction = testService.longFunction()
        Log.d("ChasmViewModel", "long function: $longFunction")

        val floatFunction = testService.floatFunction()
        Log.d("ChasmViewModel", "float function: $floatFunction")

        val doubleFunction = testService.doubleFunction()
        Log.d("ChasmViewModel", "double function: $doubleFunction")

        val unitFunction = testService.unitFunction()
        Log.d("ChasmViewModel", "unit function: $unitFunction ${testService.mutableGlobal}")

        val multipleParamFunction = testService.multipleParamFunction(5, 2.2)
        Log.d("ChasmViewModel", "multiple param function: $multipleParamFunction")

        val multipleReturnFunction = testService.multipleReturnFunction()
        Log.d("ChasmViewModel", "multiple return function: r0 = ${multipleReturnFunction.r0} r1 = ${multipleReturnFunction.r1}")

        val palStringFunction = testService.palStringFunction()
        Log.d("ChasmViewModel", "pointer and length string function: $palStringFunction")

        val nullTerminatedStringFunction = testService.nullTerminatedStringFunction()
        Log.d("ChasmViewModel", "null terminated string function: $nullTerminatedStringFunction")

        val lengthPrefixedStringFunction = testService.lengthPrefixedStringFunction()
        Log.d("ChasmViewModel", "length prefixed string function: $lengthPrefixedStringFunction")

        val packedI64StringFunction = testService.packedI64StringFunction()
        Log.d("ChasmViewModel", "packed i64 string function: $packedI64StringFunction")

        val truncated = stringService.truncate("abcde")
        Log.d("ChasmViewModel", "truncate function: $truncated")

        val truncateNullTerminated = stringService.truncateNullTerminated("foobar")
        Log.d("ChasmViewModel", "truncate null terminated function: $truncateNullTerminated")

        val truncateLenPrefixed = stringService.truncateLenPrefixed("hijkl")
        Log.d("ChasmViewModel", "truncate len prefixed function: $truncateLenPrefixed")

        val truncatePacked = stringService.truncatePacked("packed")
        Log.d("ChasmViewModel", "truncate packed function: $truncatePacked")
    }
}
