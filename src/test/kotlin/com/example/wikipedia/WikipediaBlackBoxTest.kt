package com.example.wikipedia

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import java.time.Duration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.Point
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.PointerInput
import org.openqa.selenium.interactions.Sequence
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

@DisplayName("Wikipedia Android app")
class WikipediaBlackBoxTest {
    private lateinit var driver: AndroidDriver
    private lateinit var wait: WebDriverWait

    @BeforeEach
    fun openLanguages() {
        driver = WikipediaDriver.create()
        wait = WebDriverWait(driver, Duration.ofSeconds(12)).apply {
            ignoring(StaleElementReferenceException::class.java)
        }

        wait.withMessage("Wikipedia should reach the main screen").until {
            if (visibleNow(id("nav_tab_more"))) {
                true
            } else {
                val next = driver.findElements(text("Skip")).firstOrNull { it.isDisplayed }
                    ?: driver.findElements(description("Forward")).firstOrNull { it.isDisplayed }
                next?.click()
                false
            }
        }
        assertEquals("NATIVE_APP", driver.context)
        assertEquals(WikipediaDriver.APP_PACKAGE, driver.currentPackage)
        click(id("nav_tab_more"))
        click(id("main_drawer_settings_container"))
        click(text("Wikipedia languages"))
        visible(text("Your languages"))
        assertEquals(listOf("English"), languages(), "Start with English as the only Wikipedia language")
    }

    @AfterEach
    fun closeApp() {
        if (::driver.isInitialized) driver.quit()
    }

    @Test
    fun `language list opens the add screen and navigates back`() {
        visible(id("wikipedia_languages_recycler"))
        assertEquals("1", visible(id("wiki_language_order")).text)
        assertEquals("EN", visible(text("EN")).text)
        click(text("English"))
        assertEquals(listOf("English"), languages())

        openAddLanguage()
        visible(text("All languages"))
        visible(text("Español"))
        visible(text("Spanish"))
        visible(text("Japanese"))

        val screen = driver.manage().window().size
        swipe(Point(screen.width / 2, screen.height * 3 / 4), Point(screen.width / 2, screen.height / 3))
        wait.until(ExpectedConditions.invisibilityOfElementLocated(text("Spanish")))
        click(description("Go back"))

        visible(text("Your languages"))
        assertEquals(listOf("English"), languages())
        click(description("Navigate up"))
        visible(text("Settings"))
    }

    @Test
    fun `language search filters clears and shows no results`() {
        openAddLanguage()
        click(description("Search"))

        val input = By.className("android.widget.EditText")
        visible(input).sendKeys("Spanish")
        visible(text("Español"))
        wait.until(ExpectedConditions.invisibilityOfElementLocated(text("Japanese")))

        click(description("Clear query"))
        visible(text("Japanese"))
        visible(text("Español"))

        visible(input).sendKeys("qzxfake000")
        visible(text("No languages found"))
        click(description("Clear query"))
        visible(text("All languages"))
        visible(text("Japanese"))
    }

    @Test
    fun `language can be added reordered and removed`() {
        openAddLanguage()
        click(text("Español"))

        waitForLanguages("English", "Español")
        visible(text("ES"))
        moveSecondLanguageToTop()
        waitForLanguages("Español", "English")
        moveSecondLanguageToTop()
        waitForLanguages("English", "Español")

        openRemoveLanguage()
        click(description("Done"))
        waitForLanguages("English", "Español")

        selectSpanishForRemoval()
        click(text("Cancel"))
        waitForLanguages("English", "Español")

        if (visibleNow(description("Done"))) click(description("Done"))
        selectSpanishForRemoval()
        click(text("OK"))
        waitForLanguages("English")
        wait.until(ExpectedConditions.invisibilityOfElementLocated(description("More options")))
    }

    private fun openAddLanguage() {
        click(text("Add language"))
        visible(text("Add a language"))
    }

    private fun openRemoveLanguage() {
        click(description("More options"))
        click(text("Remove language"))
        visible(description("Done"))
    }

    private fun selectSpanishForRemoval() {
        openRemoveLanguage()
        val checkbox = visible(
            By.xpath("//*[@text='Español']/../*[@resource-id='org.wikipedia:id/wiki_language_checkbox']")
        )
        checkbox.click()
        assertEquals("true", checkbox.getAttribute("checked"))
        click(description("Delete selected items"))
        visible(text("Remove selected language?"))
    }

    private fun moveSecondLanguageToTop() {
        val (first, second) = wait.until {
            driver.findElements(id("wiki_language_drag_handle")).takeIf { handles -> handles.size == 2 }
        }
        val firstHandle = first.rect
        val secondHandle = second.rect
        val x = secondHandle.x + secondHandle.width / 2
        val from = Point(x, secondHandle.y + secondHandle.height / 2)
        val to = Point(x, firstHandle.y - firstHandle.height / 5)
        swipe(from, to, 900)
    }

    private fun swipe(from: Point, to: Point, duration: Long = 450) {
        val finger = PointerInput(PointerInput.Kind.TOUCH, "finger")
        val gesture = Sequence(finger, 0)
            .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.x, from.y))
            .addAction(finger.createPointerDown(0))
            .addAction(finger.createPointerMove(Duration.ofMillis(duration), PointerInput.Origin.viewport(), to.x, to.y))
            .addAction(finger.createPointerUp(0))
        driver.perform(listOf(gesture))
    }

    private fun languages(): List<String> =
        driver.findElements(id("wiki_language_title")).map { it.text }.filterNot { it == "Add language" }

    private fun waitForLanguages(vararg expected: String) {
        wait.withMessage("Expected language order ${expected.toList()}").until {
            languages() == expected.toList()
        }
    }

    private fun visibleNow(locator: By): Boolean = driver.findElements(locator).any { it.isDisplayed }

    private fun visible(locator: By): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator))

    private fun click(locator: By) = wait.until(ExpectedConditions.elementToBeClickable(locator)).click()

    private fun id(name: String): By = AppiumBy.id("${WikipediaDriver.APP_PACKAGE}:id/$name")

    private fun description(value: String): By = AppiumBy.accessibilityId(value)

    private fun text(value: String): By = AppiumBy.androidUIAutomator("new UiSelector().text(\"$value\")")
}
