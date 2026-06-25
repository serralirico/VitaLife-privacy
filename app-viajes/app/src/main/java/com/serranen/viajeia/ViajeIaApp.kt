package com.serranen.viajeia

import android.app.Application
import com.serranen.viajeia.di.AppContainer

class ViajeIaApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
