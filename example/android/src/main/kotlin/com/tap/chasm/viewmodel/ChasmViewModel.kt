package com.tap.chasm.viewmodel

import android.content.res.AssetManager
import androidx.lifecycle.viewModelScope
import com.test.chasm.Fibonacci
import com.test.chasm.MultipleReturnFunctionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.memory.readUtf8String
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.stream.SourceReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Executable
import javax.inject.Inject

@HiltViewModel
class ChasmViewModel
    @Inject
    constructor(
        private val assetManager: AssetManager,
    ) : MVIViewModel<ChasmState, ChasmEvent, ChasmEffect>() {
        private val store = store()
        private lateinit var instance: Instance

        init {
            viewModelScope.launch {
                val bytes = readWasmFileBytes(FILENAME)
                instance = instantiateModule(bytes)
            }
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

        class FibonacciImpl(
            private val module: Module,
            private val imports: List<Import> = emptyList(),
            private val config: Config = Config(),
        ) : Fibonacci {

            constructor(
                binary: ByteArray,
                imports: List<Import> = emptyList(),
                config: Config = Config(),
            ) : this(
                module = module(binary, config.moduleConfig).expect("Failed to instantiate module"),
                imports = imports,
                config = config,
            )

            constructor(
                reader: SourceReader,
                imports: List<Import> = emptyList(),
                config: Config = Config(),
            ) : this(
                module = module(reader, config.moduleConfig).expect("Failed to instantiate module"),
                imports = imports,
                config = config,
            )

            private val store = store()
            private val instance = instance(store, module, imports, config.runtimeConfig).expect("Failed to instantiate module " + this::class.simpleName)

            override var mutableGlobal: Int
                get() {
                    val global = instance.exports.first { it.name == "mutable_global" }.value as Global
                    return readGlobal(store, global).map { (it as NumberValue.I32).value }.expect("Failed to read global")
                }
                set(value) {
                    val global = instance.exports.first { it.name == "mutable_global" }.value as Global
                    writeGlobal(store, global, NumberValue.I32(value))
                }

            override val immutableGlobal: Int
                get()  {
                    val global = instance.exports.first { it.name == "immutable_global" }.value as Global
                    return readGlobal(store, global).map { (it as NumberValue.I32).value }.expect("Failed to read global")
                }

            override fun intFunction(): Int {
                val args = listOf(
                    NumberValue.I32(1),
                )
                return invoke(store, instance, "int_function", args).map { (it.first() as NumberValue.I32).value }.expect("Failed to invoke function")
            }

            override fun longFunction(): Long {
                return invoke(store, instance, "long_function").map { (it.first() as NumberValue.I64).value }.expect("Failed to invoke function")
            }

            override fun floatFunction(): Float {
                return invoke(store, instance, "float_function").map { (it.first() as NumberValue.F32).value }.expect("Failed to invoke function")
            }

            override fun doubleFunction(): Double {
                return invoke(store, instance, "double_function").map { (it.first() as NumberValue.F64).value }.expect("Failed to invoke function")
            }

            override fun stringFunction(): String {
                val memory = instance.exports.firstNotNullOf { it.value as? Memory }
                return invoke(store, instance, "string_function").flatMap { (pointer, length) ->
                    readUtf8String(store, memory, (pointer as NumberValue.I32).value, (length as NumberValue.I32).value)
                }.expect("Failed to invoke function")
            }

            override fun unitFunction() {
                invoke(store, instance, "unit_function").expect("Failed to invoke function")
            }

            override fun multipleParamFunction(p0: Int, p1: Double) {
                val input = buildList {
                    add(NumberValue.I32(p0))
                    add(NumberValue.F64(p1))
                }
                invoke(store, instance, "multiple_param_function", input).expect("Failed to invoke function")
            }

            override fun multipleReturnFunction(): MultipleReturnFunctionResult {
                TODO("Not yet implemented")
            }

        }

        override fun handleEvent(event: ChasmEvent) {
            when (event) {
                is ChasmEvent.ChangeFibonacciIndex -> {
                    viewModelScope.launch {
                        nth.emit(event.nth)
                    }
                }
                is ChasmEvent.CalculateFibonacci -> {
                    viewModelScope.launch {
                        fibonacci.emit(calculateFibonacci(nth.value.toInt()))
                    }
                }
            }
        }

        private suspend fun readWasmFileBytes(filename: String): ByteArray = withContext(Dispatchers.IO) {
            assetManager.open(filename).readBytes()
        }

        private fun instantiateModule(byteArray: ByteArray): Instance {
            return module(byteArray)
                .flatMap { module ->
                    instance(store, module, emptyList())
                }.expect("Failed to instantiate module")
        }

        private fun calculateFibonacci(n: Int): Int {
            return invoke(store, instance, "fibonacci", listOf(NumberValue.I32(n)))
                .map {
                    (it.first() as NumberValue.I32).value
                }.expect("Failed to calculate fibonacci")
        }

        companion object {
            private const val FILENAME = "fibonacci.wasm"
        }
    }
