package app.terminalssh.secure.ssh

class FirstUseRequired(
    val host: String,
    val port: Int,
    val algorithm: String,
    val key: ByteArray,
    val fingerprint: String,
) : Exception("Host key approval required for $host:$port ($fingerprint)")

class HostKeyRejected(message: String) : Exception(message)
