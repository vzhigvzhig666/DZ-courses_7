package com.example.wikipedia

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import java.net.URI

object WikipediaDriver {
    const val APP_PACKAGE = "org.wikipedia"

    fun create(): AndroidDriver {
        val server = URI(System.getenv("APPIUM_SERVER_URL") ?: "http://127.0.0.1:4723").toURL()
        val options = UiAutomator2Options()
            .setUdid(System.getenv("ANDROID_SERIAL") ?: "emulator-5554")
            .setAppPackage(APP_PACKAGE)
            .setAppActivity("org.wikipedia.main.MainActivity")
            .setAppWaitActivity("org.wikipedia.*")
            .setNoReset(true)
        options.setCapability("appium:forceAppLaunch", true)
        options.setCapability("appium:shouldTerminateApp", true)

        return AndroidDriver(server, options)
    }
}
