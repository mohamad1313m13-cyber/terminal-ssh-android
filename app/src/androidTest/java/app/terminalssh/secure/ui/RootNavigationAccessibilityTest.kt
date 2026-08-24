package app.terminalssh.secure.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.terminalssh.secure.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RootNavigationAccessibilityTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)

    @Test
    fun tabsUseVisibleLabelsWithoutDuplicateIconDescriptions() {
        ActivityScenario.launch(MainActivity::class.java).use {
            val labels = listOf(
                R.string.tab_hosts,
                R.string.tab_terminal,
                R.string.tab_keys,
                R.string.tab_settings,
            ).map(instrumentation.targetContext::getString)

            labels.forEach { label ->
                assertTrue(device.wait(Until.hasObject(By.text(label)), TIMEOUT_MS))
                assertFalse("$label was announced twice", device.hasObject(By.desc(label)))
            }

            val hosts = device.findObject(By.text(labels.first()))
            val settings = device.findObject(By.text(labels.last()))
            assertTrue("initial tab was not selected", hosts.isSelected)

            settings.click()
            assertTrue(device.wait(Until.hasObject(By.text(labels.last()).selected(true)), TIMEOUT_MS))
            assertFalse("previous tab remained selected", hosts.isSelected)
        }
    }

    private companion object {
        const val TIMEOUT_MS = 5_000L
    }
}
