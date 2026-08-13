package app.terminalssh.secure.ssh

sealed interface SshSessionState {
    data object Idle : SshSessionState
    data object Resolving : SshSessionState
    data object Connecting : SshSessionState
    data class AwaitingHostKeyApproval(val fingerprint: String) : SshSessionState
    data object Authenticating : SshSessionState
    data object Connected : SshSessionState
    data class Failed(val code: String, val message: String? = null) : SshSessionState
    data object Closed : SshSessionState
}
