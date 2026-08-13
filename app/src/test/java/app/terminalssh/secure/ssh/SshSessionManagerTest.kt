package app.terminalssh.secure.ssh

import app.terminalssh.secure.model.HostKeyPolicy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SshSessionManagerTest {
    @Test fun tofuFlowRequiresExplicitApproval() {
        val manager = SshSessionManager()
        manager.begin()
        manager.onServerKey("host", 22, "ssh-ed25519", byteArrayOf(1,2,3), null, HostKeyPolicy.TRUST_ON_FIRST_USE)
        assertIs<SshSessionState.AwaitingHostKeyApproval>(manager.state)
        manager.approveFirstUse()
        assertEquals(SshSessionState.Authenticating, manager.state)
        manager.authenticated()
        assertEquals(SshSessionState.Connected, manager.state)
    }
}
