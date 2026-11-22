package com.example.wikipedia

import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.options.XCUITestOptions
import java.net.URI
import java.time.Duration

object WikipediaIosDriver {
    val bundleId: String = System.getenv("IOS_BUNDLE_ID") ?: "org.wikimedia.wikipedia"

    fun create(): IOSDriver {
        val server = URI(System.getenv("APPIUM_SERVER_URL") ?: "http://127.0.0.1:4723").toURL()
        val options = XCUITestOptions()
            .setBundleId(bundleId)
            .setWaitForIdleTimeout(Duration.ofSeconds(1))
            .setNoReset(true)
            .setForceAppLaunch(true)
            .setShouldTerminateApp(true)
        val udid = System.getenv("IOS_UDID")
        if (udid == null) {
            options.setDeviceName(System.getenv("IOS_DEVICE_NAME") ?: "iPhone 17 Pro")
            options.setPlatformVersion(System.getenv("IOS_VERSION") ?: "26.5")
        } else {
            options.setUdid(udid)
        }
        return IOSDriver(server, options)
    }
}
