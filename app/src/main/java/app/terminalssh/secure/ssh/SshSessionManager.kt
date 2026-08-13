package app.terminalssh.secure.ssh

import app.terminalssh.secure.model.HostKeyPolicy

/**
 * Transport-agnostic session state machine. JSch adapter can drive these transitions
 * without coupling UI code to the SSH library.
 */
class SshSessionManager(
    private val verifier: KnownHostsVerifier = KnownHostsVerifier(),
) {
    var state: SshSessionState = SshSessionState.Idle
        private set

    fun begin() {
        check(state == SshSessionState.Idle || state == SshSessionState.Closed)
        state = SshSessionState.Connecting
    }

    fun onServerKey(
        host: String,
        port: Int,
        algorithm: String,
        key: ByteArray,
        known: KnownHostsVerifier.KnownHost?,
        policy: HostKeyPolicy,
    ): KnownHostsVerifier.Decision {
        check(state == SshSessionState.Connecting)
        return when (val decision = verifier.verify(host, port, algorithm, key, known, policy)) {
            KnownHostsVerifier.Decision.Accept -> {
                state = SshSessionState.Authenticating
                decision
            }
            is KnownHostsVerifier.Decision.FirstUse -> {
                state = SshSessionState.AwaitingHostKeyApproval(decision.fingerprint)
                decision
            }
            is KnownHostsVerifier.Decision.Reject -> {
                state = SshSessionState.Failed(decision.reason)
                decision
            }
        }
    }

    fun approveFirstUse() {
        check(state is SshSessionState.AwaitingHostKeyApproval)
        state = SshSessionState.Authenticating
    }

    fun authenticated() {
        check(state == SshSessionState.Authenticating)
        state = SshSessionState.Connected
    }

    fun fail(code: String, message: String? = null) {
        state = SshSessionState.Failed(code, message)
    }

    fun close() {
        state = SshSessionState.Closed
    }
}
