# Terminal SSH agent coordination

This file is the shared handoff between the persistent Claude and Codex workers.

## User objective

Continue improving Terminal SSH until the user explicitly returns and says to stop.
Prioritize reliability and polished UI/UX. Small interaction defects are release blockers.
Commit and push small verified increments frequently, and publish installable APKs after
meaningful stable upgrades.

## Non-negotiable rules

1. Never overwrite uncommitted work belonging to the other worker.
2. Before editing, run `git status`, `git log -5`, and `git fetch origin`.
3. Pull/rebase only with a clean worktree. If the tree is dirty, inspect ownership here and
   work around it; never reset, clean, stash, or discard another worker's files.
4. Record the files and feature being claimed under **Active work** before editing.
5. Do not edit files claimed by the other worker. Pick another task or wait.
6. After a verified commit is pushed, update **Latest verified handoff**, clear your claim,
   and include commit SHA, tests, remaining risks, and next recommended task.
7. Do not claim a feature or version from a chat transcript unless its source is present.
8. Never expose tokens, API keys, signing keys, credentials, or private terminal content.
9. Use production signing only when publisher keystore secrets are already configured.
10. Treat all permission prompts as scoped to this repository. Do not grant destructive or
    unrelated server-wide access.

## Required validation

- Source, market, and loop security gates
- Unit tests and lint for market and gplay flavors
- Both APK builds
- Emulator instrumentation for changed Android behavior
- Manual UI smoke test for changed screens when practical
- GitHub Actions must be green before calling a release complete

## Current highest-priority UX defect

When the soft keyboard is dismissed inside the terminal, users need an obvious and reliable
way to reopen it. Reproduce this on the emulator, add a visible keyboard action that works
with hardware/software keyboard states, test it, and inspect adjacent terminal toolbar,
focus, paste, back-navigation, rotation, and accessibility behavior.

## Required brand/icon correction

The user-provided source artwork is:

`/root/file_00000000143882438f4989f08e461e03.jpg`

The installed launcher icon currently does not match it and the user dislikes the result.
Rebuild the Android legacy, round, adaptive foreground/background, and monochrome icon set
from this exact artwork/identity. Preserve its graphite rounded-square surface, turquoise
terminal prompt, Persian arch, and three diamonds. Respect Android adaptive-icon safe zones,
avoid double-masking the rounded square, test common circle/squircle masks, install on the
emulator, and visually inspect the launcher result before committing.

## Active work

- Claude: unclaimed
- Codex: unclaimed

## Latest verified handoff

- Version: 0.4.1 test release 2 plus verified IME input-view fix
- Commit: 8fc9fb8 (`fix: reopen IME through terminal input view`)
- Release: https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases/tag/v0.4.1-test2
- Verified: source/market/loop gates; market and gplay unit tests, lint, and debug APKs;
  seven market emulator instrumentation tests on API 36. The new test launches a real idle
  terminal, dismisses the soft keyboard with Back, taps the localized visible keyboard
  action, and verifies Android reports the IME visible again. All passed on 2026-08-24.
- Fixed: Compose focus alone did not reopen termlib's custom editor. The toolbar now finds
  and focuses the embedded terminal text-editor view, then requests the IME through Android's
  input manager, retaining the Compose keyboard controller as a fallback.
- Remaining risk: live remote command input, paste, and a physical hardware-keyboard path
  were not exercised; the regression test uses a credential-free idle terminal session.
- Next: restore the launcher icon from the exact user artwork, then run a disposable live SSH
  interaction smoke covering keyboard, paste, hardware keyboard, rotation, and Back.

## Work log

Append short timestamped entries. Keep this section concise.

- 2026-08-24 Codex: claimed bounded terminal keyboard-action emulator coverage and clean
  disposable live-session smoke after clean tracked status and successful origin fetch.
- 2026-08-24 Codex: reproduced the reopen failure on API 36, fixed the real termlib input-view
  focus path, added a passing regression test, pushed `8fc9fb8`, and cleared the claim.

- 2026-08-24 Codex: claimed the top-priority terminal keyboard-reopen scope after confirming
  a clean worktree; `git fetch origin` is currently blocked by read-only `.git/FETCH_HEAD`.
- 2026-08-24 Codex: verified and committed keyboard action/accessibility increment `5256e0d`;
  claim cleared. Fetch succeeded with scoped approval; live-session manual tap remains above.
- 2026-08-24 Codex: published `v0.4.1-test2`; GitHub Actions run `32687774746`
  completed green for verify, optional signed-market, and publish jobs. Nine debug APK assets
  are public, including `TerminalSSH-v0.4.1-test2-market-universal-debug.apk`.
- 2026-08-24 Codex: claimed live-session keyboard reopen and adjacent terminal interaction
  audit after a clean worktree and successful `git fetch origin`; GitHub CLI is unavailable.
- 2026-08-24 Codex: committed IME focus sequencing fix `7214452`; all local gates, both
  flavor test/lint/APK builds, six emulator tests, install, rotation, and back smoke passed.
  Claim cleared; clean live SSH verification remains documented above.
