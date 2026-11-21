package com.example.wikipedia

import io.appium.java_client.AppiumBy
import io.appium.java_client.ios.IOSDriver
import java.time.Duration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

@Tag("ios")
@DisplayName("Wikipedia iOS app")
class WikipediaIosBlackBoxTest {
    private lateinit var driver: IOSDriver
    private lateinit var wait: WebDriverWait

    private val addLanguage = id("Preferred Languages Add Language Button")
    private val allLanguages = id("All Languages View")
    private val languageRows = predicate(
        "type == 'XCUIElementTypeCell' AND name BEGINSWITH 'Language Selection Language ' AND visible == 1"
    )
    private val search = By.className("XCUIElementTypeSearchField")
    private val close = predicate(
        "type == 'XCUIElementTypeButton' AND (name == 'Close' OR name == 'Cancel') AND visible == 1"
    )

    @BeforeEach
    fun openLanguages() {
        driver = WikipediaIosDriver.create()
        wait = WebDriverWait(driver, Duration.ofSeconds(15)).apply {
            ignoring(StaleElementReferenceException::class.java)
        }

        assertEquals("NATIVE_APP", driver.context)
        val activeApp = driver.executeScript("mobile: activeAppInfo") as Map<*, *>
        assertEquals(WikipediaIosDriver.bundleId, activeApp["bundleId"])
        finishOnboarding()
        click(id("profile-button"))
        click(id("Settings"))
        click(id("My languages"))
        visible(id("Wikipedia languages"))
        visible(preferredLanguage("en"))
        assertEquals(
            listOf("Language Selection Preferred Language en"),
            preferredLanguages(),
            "Start with English as the only Wikipedia language"
        )
    }

    @AfterEach
    fun quit() {
        if (::driver.isInitialized) driver.quit()
    }

    @Test
    fun `language picker opens scrolls and closes`() {
        visible(id("YOUR LANGUAGES"))
        visible(id("PRIMARY"))
        assertFalse(visible(id("Edit")).isEnabled)
        click(preferredLanguage("en"))
        visible(addLanguage)

        click(id("Customize Explore feed"))
        visible(id("Explore feed"))
        click(id("BackButton"))
        visible(preferredLanguage("en"))

        openPicker()
        visible(language("aa"))
        driver.executeScript("mobile: swipe", mapOf("direction" to "up"))
        wait.until(ExpectedConditions.invisibilityOfElementLocated(language("aa")))
        wait.until { driver.findElements(languageRows).isNotEmpty() }

        click(close)
        visible(preferredLanguage("en"))
        click(close)
        visible(id("My languages"))
    }

    @Test
    fun `language search filters clears and handles an empty result`() {
        openPicker()
        val field = visible(search)
        field.click()
        field.sendKeys("Spanish")
        visible(language("es"))
        wait.until(ExpectedConditions.invisibilityOfElementLocated(language("aa")))

        click(id("Clear text"))
        visible(language("aa"))
        visible(search).sendKeys("qzxfake000")
        wait.until(ExpectedConditions.invisibilityOfElementLocated(language("es")))
        wait.until { driver.findElements(languageRows).isEmpty() }

        click(id("Clear text"))
        visible(language("aa"))
    }

    @Test
    fun `Spanish can be added reordered and removed`() {
        openPicker()
        val field = visible(search)
        field.click()
        field.sendKeys("Spanish")
        click(language("es"))

        waitForLanguageOrder("en", "es")

        click(id("Edit"))
        try {
            moveLanguageAbove("es", "en")
            waitForLanguageOrder("es", "en")
            moveLanguageAbove("en", "es")
            waitForLanguageOrder("en", "es")
        } finally {
            val spanish = visible(preferredLanguage("es"))
            spanish.findElement(predicate("type == 'XCUIElementTypeButton' AND name BEGINSWITH 'Remove '")).click()
            click(id("Delete"))
        }
        waitForLanguageOrder("en")
        click(predicate("type == 'XCUIElementTypeButton' AND name == 'selected' AND visible == 1"))
        assertFalse(visible(id("Edit")).isEnabled)
    }

    private fun openPicker() {
        click(addLanguage)
        visible(allLanguages)
        visible(search)
    }

    private fun finishOnboarding() {
        wait.until {
            val button = listOf("App Onboarding Skip Button", "App Onboarding Next Button")
                .firstNotNullOfOrNull { name -> driver.findElements(id(name)).firstOrNull { it.isDisplayed } }
            if (button != null) {
                button.click()
                false
            } else {
                driver.findElements(id("profile-button")).any { it.isDisplayed }
            }
        }
    }

    private fun preferredLanguages(): List<String> = driver.findElements(
        predicate("type == 'XCUIElementTypeCell' AND name BEGINSWITH 'Language Selection Preferred Language '")
    ).mapNotNull { it.getAttribute("name") }

    private fun waitForLanguageOrder(vararg codes: String) {
        val expected = codes.map { "Language Selection Preferred Language $it" }
        wait.until { preferredLanguages() == expected }
    }

    private fun moveLanguageAbove(code: String, targetCode: String) {
        val handle = visible(preferredLanguage(code))
            .findElement(predicate("type == 'XCUIElementTypeButton' AND name BEGINSWITH 'Reorder '"))
            .rect
        val target = visible(preferredLanguage(targetCode)).rect
        val x = handle.x + handle.width / 2
        driver.executeScript("mobile: dragFromToForDuration", mapOf(
            "duration" to 0.7,
            "fromX" to x,
            "fromY" to handle.y + handle.height / 2,
            "toX" to x,
            "toY" to target.y + target.height / 3
        ))
    }

    private fun visible(locator: By): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator))

    private fun click(locator: By) = wait.until(ExpectedConditions.elementToBeClickable(locator)).click()

    private fun language(code: String): By = id("Language Selection Language $code")

    private fun preferredLanguage(code: String): By = id("Language Selection Preferred Language $code")

    private fun id(value: String): By = AppiumBy.accessibilityId(value)

    private fun predicate(value: String): By = AppiumBy.iOSNsPredicateString(value)
}
