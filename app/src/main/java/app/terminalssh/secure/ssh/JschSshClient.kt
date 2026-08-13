package app.terminalssh.secure.ssh

import app.terminalssh.secure.model.AuthMethod
import app.terminalssh.secure.model.HostKeyPolicy
import app.terminalssh.secure.model.HostProfile
import app.terminalssh.secure.security.AndroidKeyStoreVault
import app.terminalssh.secure.security.VaultAad
import app.terminalssh.secure.storage.KnownHostsStore
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.HostKey
import com.jcraft.jsch.HostKeyRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicReference

class JschSshClient(
    private val vault: AndroidKeyStoreVault,
    private val knownHosts: KnownHostsStore,
) {
    data class Shell(
        val session: Session,
        val channel: ChannelShell,
        val input: InputStream,
        val output: OutputStream,
    ) : AutoCloseable {
        override fun close() {
            runCatching { output.close() }
            runCatching { input.close() }
            runCatching { channel.disconnect() }
            runCatching { session.disconnect() }
        }
    }

    fun connect(profile: HostProfile, columns: Int = 100, rows: Int = 30): Shell {
        val jsch = JSch()
        val captured = AtomicReference<PresentedHostKey?>()
        jsch.hostKeyRepository = PolicyHostKeyRepository(profile, knownHosts, captured)

        when (val auth = profile.auth) {
            is AuthMethod.Password -> Unit
            is AuthMethod.PrivateKey -> {
                val privateKey = requireNotNull(vault.get(auth.keyVaultRef, VaultAad.PRIVATE_KEY)) { "missing private key" }
                val passphrase = auth.passphraseVaultRef?.let { vault.get(it, VaultAad.PASSPHRASE) }
                try {
                    jsch.addIdentity("vault-key", privateKey, null, passphrase)
                } finally {
                    privateKey.fill(0)
                    passphrase?.fill(0)
                }
            }
        }

        val session = jsch.getSession(profile.username, profile.host, profile.port)
        session.setConfig("StrictHostKeyChecking", "yes")
        session.setConfig("PreferredAuthentications", "publickey,password,keyboard-interactive")
        session.timeout = CONNECT_TIMEOUT_MS

        if (profile.auth is AuthMethod.Password) {
            val password = requireNotNull(vault.get(profile.auth.vaultRef, VaultAad.PASSWORD)) { "missing password" }
            try {
                session.setPassword(password)
            } finally {
                password.fill(0)
            }
        }

        try {
            session.connect(CONNECT_TIMEOUT_MS)
        } catch (e: Exception) {
            val presented = captured.get()
            if (presented != null) {
                when (val decision = presented.decision) {
                    is KnownHostsVerifier.Decision.FirstUse -> throw FirstUseRequired(
                        profile.host, profile.port, presented.algorithm, presented.key, decision.fingerprint,
                    )
                    is KnownHostsVerifier.Decision.Reject -> throw HostKeyRejected(
                        buildString {
                            append("Host key rejected: ").append(decision.reason)
                            decision.expected?.let { append("; expected ").append(it) }
                            decision.actual?.let { append("; received ").append(it) }
                        },
                    )
                    KnownHostsVerifier.Decision.Accept -> Unit
                }
            }
            throw e
        }

        val channel = session.openChannel("shell") as ChannelShell
        channel.setPty(true)
        channel.setPtyType("xterm-256color", columns, rows, 0, 0)
        val input = channel.inputStream
        val output = channel.outputStream
        channel.connect(CONNECT_TIMEOUT_MS)
        return Shell(session, channel, input, output)
    }

    private data class PresentedHostKey(
        val algorithm: String,
        val key: ByteArray,
        val decision: KnownHostsVerifier.Decision,
    )

    private class PolicyHostKeyRepository(
        private val profile: HostProfile,
        private val store: KnownHostsStore,
        private val captured: AtomicReference<PresentedHostKey?>,
        private val verifier: KnownHostsVerifier = KnownHostsVerifier(),
    ) : HostKeyRepository {
        override fun check(host: String?, key: ByteArray): Int {
            val algorithm = HostKey(profile.host, key).type
            val known = store.get(profile.host, profile.port)
            val decision = verifier.verify(profile.host, profile.port, algorithm, key, known, profile.hostKeyPolicy)
            captured.set(PresentedHostKey(algorithm, key.copyOf(), decision))
            return when (decision) {
                KnownHostsVerifier.Decision.Accept -> HostKeyRepository.OK
                is KnownHostsVerifier.Decision.FirstUse -> HostKeyRepository.NOT_INCLUDED
                is KnownHostsVerifier.Decision.Reject -> HostKeyRepository.CHANGED
            }
        }

        override fun add(hostkey: HostKey?, ui: com.jcraft.jsch.UserInfo?) = Unit
        override fun remove(host: String?, type: String?) = Unit
        override fun remove(host: String?, type: String?, key: ByteArray?) = Unit
        override fun getKnownHostsRepositoryID(): String = "TerminalSSH encrypted-profile known hosts"
        override fun getHostKey(): Array<HostKey> = emptyArray()
        override fun getHostKey(host: String?, type: String?): Array<HostKey> = emptyArray()
    }

    companion object { private const val CONNECT_TIMEOUT_MS = 15_000 }
}
