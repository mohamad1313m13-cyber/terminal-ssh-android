package app.terminalssh.secure.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import app.terminalssh.secure.model.AuthMethod
import app.terminalssh.secure.model.HostKeyPolicy
import app.terminalssh.secure.model.HostProfile
import app.terminalssh.secure.security.AndroidKeyStoreVault
import app.terminalssh.secure.security.VaultAad
import app.terminalssh.secure.ssh.FirstUseRequired
import app.terminalssh.secure.ssh.JschSshClient
import app.terminalssh.secure.storage.KnownHostsStore
import org.connectbot.terminal.Terminal
import org.connectbot.terminal.TerminalEmulator
import org.connectbot.terminal.TerminalEmulatorFactory
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val executor = Executors.newCachedThreadPool()
    private lateinit var vault: AndroidKeyStoreVault
    private lateinit var knownHosts: KnownHostsStore
    private lateinit var client: JschSshClient
    private lateinit var emulator: TerminalEmulator
    private var shell: JschSshClient.Shell? = null
    @Volatile private var activeProfile: HostProfile? = null
    private val connectionGeneration = AtomicLong(0)

    private lateinit var host: EditText
    private lateinit var port: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var status: TextView
    private lateinit var connect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        vault = AndroidKeyStoreVault(this)
        knownHosts = KnownHostsStore(this)
        client = JschSshClient(vault, knownHosts)
        emulator = TerminalEmulatorFactory.create(
            initialRows = 30,
            initialCols = 100,
            onKeyboardInput = { bytes -> sendRaw(bytes) },
            onResize = { dimensions ->
                shell?.channel?.setPtySize(dimensions.columns, dimensions.rows, 0, 0)
            },
            onClipboardCopy = { text ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("terminal", text))
            },
        )
        setContentView(buildUi())
    }

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
        }

        val hostRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        host = field("Host", 1f)
        port = field("Port", 0.35f).apply {
            setText("22")
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        hostRow.addView(host); hostRow.addView(port)
        root.addView(hostRow)

        val authRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        username = field("Username", 0.8f)
        password = field("Password", 1f).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        authRow.addView(username); authRow.addView(password)
        root.addView(authRow)

        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        connect = Button(this).apply {
            text = "Connect"
            setOnClickListener { connect() }
        }
        status = TextView(this).apply {
            text = "Disconnected"
            setPadding(16, 0, 0, 0)
        }
        actionRow.addView(connect)
        actionRow.addView(status, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(actionRow)

        val terminalView = ComposeView(this).apply {
            setContent {
                Terminal(
                    terminalEmulator = emulator,
                    keyboardEnabled = true,
                    showSoftKeyboard = true,
                    onPasteRequest = { pasteClipboard() },
                )
            }
        }
        root.addView(terminalView, LinearLayout.LayoutParams(-1, 0, 1f))
        return root
    }

    private fun field(hintText: String, weight: Float): EditText = EditText(this).apply {
        hint = hintText
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
        setSingleLine(true)
    }

    private fun connect() {
        shell?.close()
        shell = null
        emulator.clearScreen()

        val hostText = host.text.toString().trim()
        val userText = username.text.toString().trim()
        val passChars = password.text
        val portNumber = port.text.toString().toIntOrNull() ?: 22
        if (hostText.isBlank() || userText.isBlank() || passChars.isEmpty()) {
            toast("Host, username and password are required")
            return
        }
        if (portNumber !in 1..65535) {
            toast("Port must be between 1 and 65535")
            return
        }

        val ref = UUID.randomUUID().toString()
        val passBytes = editableUtf8(passChars)
        try {
            vault.put(ref, passBytes, VaultAad.PASSWORD)
        } finally {
            passBytes.fill(0)
            passChars.clear()
        }
        val profile = HostProfile(
            id = UUID.randomUUID().toString(),
            host = hostText,
            port = portNumber,
            username = userText,
            auth = AuthMethod.Password(ref),
            hostKeyPolicy = HostKeyPolicy.TRUST_ON_FIRST_USE,
        )
        val generation = connectionGeneration.incrementAndGet()
        activeProfile?.let(::deleteEphemeralSecret)
        activeProfile = profile
        connect.isEnabled = false
        status.text = "Connecting…"
        doConnect(profile, generation)
    }

    private fun doConnect(profile: HostProfile, generation: Long) {
        executor.execute {
            try {
                val dimensions = emulator.dimensions
                val connected = client.connect(profile, dimensions.columns, dimensions.rows)
                if (generation != connectionGeneration.get()) {
                    connected.close()
                    deleteEphemeralSecret(profile)
                    return@execute
                }
                shell = connected
                deleteEphemeralSecret(profile)
                if (activeProfile === profile) activeProfile = null
                runOnUiThread {
                    status.text = "Connected"
                    connect.text = "Reconnect"
                    connect.isEnabled = true
                }
                pumpOutput(connected)
            } catch (first: FirstUseRequired) {
                if (generation != connectionGeneration.get()) {
                    deleteEphemeralSecret(profile)
                    return@execute
                }
                runOnUiThread { showHostKeyApproval(profile, generation, first) }
            } catch (t: Throwable) {
                deleteEphemeralSecret(profile)
                if (activeProfile === profile) activeProfile = null
                runOnUiThread {
                    status.text = "Failed"
                    toast(t.message ?: t.javaClass.simpleName)
                    connect.isEnabled = true
                }
            }
        }
    }

    private fun showHostKeyApproval(profile: HostProfile, generation: Long, first: FirstUseRequired) {
        AlertDialog.Builder(this)
            .setTitle("Verify host key")
            .setMessage("${first.host}:${first.port}\n${first.algorithm}\n${first.fingerprint}\n\nOnly continue if this fingerprint is expected.")
            .setPositiveButton("Trust and connect") { _, _ ->
                knownHosts.put(first.host, first.port, first.algorithm, first.key)
                status.text = "Host trusted; reconnecting…"
                if (generation == connectionGeneration.get()) doConnect(profile, generation)
            }
            .setNegativeButton("Cancel") { _, _ ->
                deleteEphemeralSecret(profile)
                if (activeProfile === profile) activeProfile = null
                status.text = "Cancelled"
                connect.isEnabled = true
            }
            .setOnCancelListener {
                deleteEphemeralSecret(profile)
                if (activeProfile === profile) activeProfile = null
                status.text = "Cancelled"
                connect.isEnabled = true
            }
            .show()
    }

    private fun pumpOutput(openShell: JschSshClient.Shell) {
        val buffer = ByteArray(16 * 1024)
        try {
            while (!openShell.channel.isClosed) {
                val read = openShell.input.read(buffer)
                if (read < 0) break
                if (read > 0) emulator.writeInput(buffer, 0, read)
            }
        } catch (_: Throwable) {
        } finally {
            buffer.fill(0)
            if (shell === openShell) shell = null
            runOnUiThread { status.text = "Disconnected" }
        }
    }

    private fun sendRaw(bytes: ByteArray) {
        val current = shell ?: return
        val copy = bytes.copyOf()
        executor.execute {
            try {
                current.output.write(copy)
                current.output.flush()
            } finally {
                copy.fill(0)
            }
        }
    }

    private fun pasteClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString() ?: return
        sendRaw(text.encodeToByteArray())
    }

    private fun editableUtf8(editable: Editable): ByteArray {
        val chars = CharArray(editable.length)
        for (i in chars.indices) chars[i] = editable[i]
        return try {
            val buffer = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(chars))
            ByteArray(buffer.remaining()).also { buffer.get(it) }
        } finally {
            chars.fill('\u0000')
        }
    }

    private fun deleteEphemeralSecret(profile: HostProfile) {
        when (val auth = profile.auth) {
            is AuthMethod.Password -> vault.delete(auth.vaultRef, VaultAad.PASSWORD)
            is AuthMethod.PrivateKey -> Unit
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        connectionGeneration.incrementAndGet()
        activeProfile?.let(::deleteEphemeralSecret)
        activeProfile = null
        shell?.close()
        shell = null
        executor.shutdownNow()
        super.onDestroy()
    }
}
