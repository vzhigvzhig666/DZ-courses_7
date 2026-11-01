package com.example.wikipedia

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.options.XCUITestOptions
import java.net.URI
import org.openqa.selenium.WebDriver

enum class MobilePlatform {
    ANDROID,
    IOS
}

object MobileBrowserDriver {
    fun create(): WebDriver {
        val platform = System.getProperty("platform", System.getenv("TEST_PLATFORM") ?: "ANDROID")
            .uppercase()
            .let(MobilePlatform::valueOf)
        val serverUrl = URI(System.getenv("APPIUM_SERVER_URL") ?: "http://127.0.0.1:4723").toURL()

        return when (platform) {
            MobilePlatform.ANDROID -> {
                val options = UiAutomator2Options()
                    .setPlatformName("Android")
                    .setAutomationName("UiAutomator2")
                    .setDeviceName(System.getenv("ANDROID_DEVICE_NAME") ?: "Pixel_9_Pro_XL")
                options.setCapability("browserName", "Chrome")
                AndroidDriver(serverUrl, options)
            }

            MobilePlatform.IOS -> {
                val options = XCUITestOptions()
                    .setPlatformName("iOS")
                    .setAutomationName("XCUITest")
                    .setDeviceName(System.getenv("IOS_DEVICE_NAME") ?: "iPhone 17 Pro")
                    .setPlatformVersion(System.getenv("IOS_VERSION") ?: "26.5")
                options.setCapability("browserName", "Safari")
                IOSDriver(serverUrl, options)
            }
        }
    }
}
