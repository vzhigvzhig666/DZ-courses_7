package com.example.wikipedia

import java.time.Duration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

@DisplayName("Wikipedia mobile UI black-box checks")
class WikipediaBlackBoxTest {
    private lateinit var driver: WebDriver
    private lateinit var wait: WebDriverWait

    @BeforeEach
    fun openMainPage() {
        driver = MobileBrowserDriver.create()
        wait = WebDriverWait(driver, Duration.ofSeconds(20))
        driver.get("https://en.wikipedia.org/wiki/Main_Page")
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")))
    }

    @AfterEach
    fun closeBrowser() {
        if (::driver.isInitialized) driver.quit()
    }

    @Test
    fun `main screen shows its heading and available controls`() {
        assertTrue(wait.until {
            try {
                driver.findElement(By.cssSelector("h1")).text.contains("Welcome to Wikipedia", ignoreCase = true)
            } catch (_: StaleElementReferenceException) {
                false
            }
        })
        assertTrue(isVisibleAndEnabled(By.id("searchIcon")))
        assertTrue(isVisibleAndEnabled(By.id("mw-mf-main-menu-button")))
        assertTrue(
            driver.findElements(By.cssSelector("a, button, input, select, textarea, [role='button'], [role='link']"))
                .size >= 5
        )
    }

    @Test
    fun `navigation menu opens about Wikipedia screen`() {
        driver.findElement(By.id("main-menu-input")).click()

        val aboutLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.linkText("About Wikipedia"))
        )
        assertTrue(aboutLink.isDisplayed)
        aboutLink.click()

        wait.until { driver.currentUrl.orEmpty().contains("Wikipedia:About") }
        assertTrue(wait.until {
            try {
                driver.findElements(By.cssSelector("h1")).any { it.text.contains("About", ignoreCase = true) }
            } catch (_: StaleElementReferenceException) {
                false
            }
        })
        assertTrue(wait.until {
            try {
                driver.findElements(By.cssSelector("main a, #content a")).any { it.isDisplayed }
            } catch (_: StaleElementReferenceException) {
                false
            }
        })
    }

    private fun isVisibleAndEnabled(locator: By): Boolean = wait.until {
        try {
            val element = driver.findElement(locator)
            element.isDisplayed && element.isEnabled
        } catch (_: StaleElementReferenceException) {
            false
        }
    }
}
