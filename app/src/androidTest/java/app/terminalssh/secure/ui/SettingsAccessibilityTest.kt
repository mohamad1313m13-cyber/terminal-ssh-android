package app.terminalssh.secure.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.terminalssh.secure.R
import app.terminalssh.secure.TerminalApp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsAccessibilityTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)

    @Test
    fun terminalPalettesAreNamedLargeAndExposeSelection() {
        val app = instrumentation.targetContext.applicationContext as TerminalApp
        val previousTheme = app.settings.themeName

        try {
            app.settings.themeName = "persian_neon"
            ActivityScenario.launch(MainActivity::class.java).use {
                val settingsTab = instrumentation.targetContext.getString(R.string.tab_settings)
                val persianNeon = instrumentation.targetContext.getString(R.string.settings_palette_persian_neon)
                val oled = instrumentation.targetContext.getString(R.string.settings_palette_oled)

                assertTrue(device.wait(Until.hasObject(By.text(settingsTab)), TIMEOUT_MS))
                device.findObject(By.text(settingsTab)).click()
                assertTrue(device.wait(Until.hasObject(By.desc(oled)), TIMEOUT_MS))

                val oledAction = device.findObject(By.desc(oled))
                val minimumPx = (48 * instrumentation.targetContext.resources.displayMetrics.density).toInt()
                assertTrue("$oled width was ${oledAction.visibleBounds.width()}px", oledAction.visibleBounds.width() >= minimumPx)
                assertTrue("$oled height was ${oledAction.visibleBounds.height()}px", oledAction.visibleBounds.height() >= minimumPx)
                assertTrue(device.findObject(By.desc(persianNeon)).isSelected)
                assertFalse(oledAction.isSelected)

                oledAction.click()
                assertTrue(device.wait(Until.findObject(By.desc(oled)), TIMEOUT_MS).isSelected)
                assertTrue(app.settings.themeName == "oled")
            }
        } finally {
            app.settings.themeName = previousTheme
        }
    }

    private companion object {
        const val TIMEOUT_MS = 5_000L
    }
}
