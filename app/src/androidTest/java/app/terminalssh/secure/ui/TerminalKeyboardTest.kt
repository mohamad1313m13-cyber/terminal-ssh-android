package app.terminalssh.secure.ui

import android.provider.Settings
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.terminalssh.secure.R
import app.terminalssh.secure.TerminalApp
import app.terminalssh.secure.model.AuthMethod
import app.terminalssh.secure.model.HostProfile
import app.terminalssh.secure.ssh.SshSession
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TerminalKeyboardTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private lateinit var app: TerminalApp
    private var previousHardwareKeyboardSetting = "0"

    @Before
    fun setUp() {
        app = instrumentation.targetContext.applicationContext as TerminalApp
        app.sessions.closeAll()
        previousHardwareKeyboardSetting = readSecureSetting("show_ime_with_hard_keyboard")
        writeSecureSetting("show_ime_with_hard_keyboard", "1")
    }

    @After
    fun tearDown() {
        device.setOrientationNatural()
        app.sessions.closeAll()
        writeSecureSetting("show_ime_with_hard_keyboard", previousHardwareKeyboardSetting)
    }

    @Test
    fun keyboardActionReopensImeAfterBackDismissal() {
        app.sessions.add(idleSession())

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val terminalTab = instrumentation.targetContext.getString(R.string.tab_terminal)
            val keyboardAction = instrumentation.targetContext.getString(R.string.show_keyboard)

            // Material navigation merges icon semantics into the item; its visible label is
            // the stable selector across Compose and Android versions.
            assertTrue(device.wait(Until.hasObject(By.text(terminalTab)), UI_TIMEOUT_MS))
            device.findObject(By.text(terminalTab)).click()
            assertTrue(device.wait(Until.hasObject(By.desc(keyboardAction)), UI_TIMEOUT_MS))

            // Exercise more than the first recovery. Input-method races commonly appear only
            // after Android reuses an existing input connection.
            repeat(2) {
                dismissAndReopenKeyboard(scenario, keyboardAction)
            }

            // MainActivity handles orientation changes in place. Ensure the embedded terminal
            // editor remains discoverable after its AndroidView is laid out at a new size.
            device.pressBack()
            assertTrue(waitForIme(scenario, visible = false))
            device.setOrientationLeft()
            assertTrue(device.wait(Until.hasObject(By.desc(keyboardAction)), UI_TIMEOUT_MS))
            device.findObject(By.desc(keyboardAction)).click()
            assertTrue(waitForIme(scenario, visible = true))
        }
    }

    @Test
    fun closeActionNamesAndClosesOnlyItsSession() {
        val firstTitle = "First host"
        val secondTitle = "Second host"
        app.sessions.add(idleSession(id = "first-session", title = firstTitle))
        app.sessions.add(idleSession(id = "second-session", title = secondTitle))

        ActivityScenario.launch(MainActivity::class.java).use {
            val terminalTab = instrumentation.targetContext.getString(R.string.tab_terminal)
            val firstClose = instrumentation.targetContext.getString(R.string.close_session, firstTitle)
            val secondClose = instrumentation.targetContext.getString(R.string.close_session, secondTitle)

            assertTrue(device.wait(Until.hasObject(By.text(terminalTab)), UI_TIMEOUT_MS))
            device.findObject(By.text(terminalTab)).click()
            assertTrue(device.wait(Until.hasObject(By.desc(firstClose)), UI_TIMEOUT_MS))
            assertTrue(device.wait(Until.hasObject(By.desc(secondClose)), UI_TIMEOUT_MS))

            device.findObject(By.desc(firstClose)).click()

            assertTrue(device.wait(Until.gone(By.desc(firstClose)), UI_TIMEOUT_MS))
            assertTrue(device.hasObject(By.desc(secondClose)))
        }
    }

    private fun dismissAndReopenKeyboard(
        scenario: ActivityScenario<MainActivity>,
        keyboardAction: String,
    ) {
        device.waitForIdle()
        device.pressBack()
        assertTrue(waitForIme(scenario, visible = false))
        device.findObject(By.desc(keyboardAction)).click()
        assertTrue(waitForIme(scenario, visible = true))
    }

    private fun idleSession(
        id: String = "keyboard-regression",
        title: String = "Keyboard test",
    ): SshSession = SshSession(
        id = id,
        profile = HostProfile(
            id = "$id-host",
            label = title,
            host = "127.0.0.1",
            port = 1,
            username = "tester",
            auth = AuthMethod.Password(""),
        ),
        client = app.client,
        keepAlive = false,
        onClipboardCopy = {},
        onPasteRequest = {},
    )

    private fun waitForIme(scenario: ActivityScenario<MainActivity>, visible: Boolean): Boolean {
        repeat(IME_POLL_ATTEMPTS) {
            var isVisible = false
            scenario.onActivity { activity ->
                isVisible = WindowInsetsCompat.toWindowInsetsCompat(
                    activity.window.decorView.rootWindowInsets,
                ).isVisible(WindowInsetsCompat.Type.ime())
            }
            if (isVisible == visible) return true
            Thread.sleep(IME_POLL_MS)
        }
        return false
    }

    private fun readSecureSetting(name: String): String =
        Settings.Secure.getString(instrumentation.targetContext.contentResolver, name) ?: "0"

    private fun writeSecureSetting(name: String, value: String) {
        instrumentation.uiAutomation.executeShellCommand("settings put secure $name $value").close()
        device.waitForIdle()
    }

    private companion object {
        const val UI_TIMEOUT_MS = 5_000L
        const val IME_POLL_ATTEMPTS = 30
        const val IME_POLL_MS = 100L
    }
}
