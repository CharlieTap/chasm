package com.tap.chasm.di

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import com.tap.chasm.app.ChasmApplication
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@Keep
class MetroAppComponentFactory : AppComponentFactory() {

  private inline fun <reified T : Any> getInstance(
    cl: ClassLoader,
    className: String,
    providers: Map<KClass<out T>, Provider<T>>,
  ): T? {
    val clazz = Class.forName(className, false, cl).asSubclass(T::class.java)
    val modelProvider = providers[clazz.kotlin] ?: return null
    return modelProvider()
  }

  override fun instantiateActivityCompat(
    cl: ClassLoader,
    className: String,
    intent: Intent?,
  ): Activity {
    return getInstance(cl, className, activityProviders)
      ?: super.instantiateActivityCompat(cl, className, intent)
  }

  override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
    val app = super.instantiateApplicationCompat(cl, className)
    activityProviders = (app as ChasmApplication).appGraph.activityProviders
    return app
  }

  companion object {
    private lateinit var activityProviders: Map<KClass<out Activity>, Provider<Activity>>
  }
}
