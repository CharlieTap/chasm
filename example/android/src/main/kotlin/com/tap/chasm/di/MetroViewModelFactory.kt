package com.tap.chasm.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@Inject
class MetroViewModelFactory(
    private val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider =
            viewModelProviders[modelClass.kotlin]
                ?: throw IllegalArgumentException("Unknown model class $modelClass")

        return try {
            provider() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
