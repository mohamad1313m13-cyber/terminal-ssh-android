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
- Codex: live-session terminal interaction audit and any narrowly required fix; files:
  `app/src/main/java/app/terminalssh/secure/ui/TerminalScreen.kt`, terminal-focused tests,
  and this handoff

## Latest verified handoff

- Version: 0.4.1 test release 2
- Commit: 5256e0d (`fix: add reliable terminal keyboard action`)
- Release: https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases/tag/v0.4.1-test2
- Verified: source/market/loop gates; market and gplay compilation, unit tests, lint, and
  debug APKs; six market emulator instrumentation tests; market debug APK install/launch.
- Fixed: an always-visible 48 dp terminal keyboard action now refocuses the terminal and
  explicitly requests the IME; extra keys remain independently scrollable. Session-close
  and toolbar controls also have localized accessibility semantics and 48 dp targets.
- Remaining risk: scripted live-session entry was not completed because the add-host sheet
  shifted fields under IME resize; do one real-session dismiss/reopen tap test before release.
- Next: complete the terminal interaction audit (keyboard reopen with a live session, paste,
  back navigation, rotation, hardware keyboard), then restore the exact user-provided icon.

## Work log

Append short timestamped entries. Keep this section concise.

- 2026-08-24 Codex: claimed the top-priority terminal keyboard-reopen scope after confirming
  a clean worktree; `git fetch origin` is currently blocked by read-only `.git/FETCH_HEAD`.
- 2026-08-24 Codex: verified and committed keyboard action/accessibility increment `5256e0d`;
  claim cleared. Fetch succeeded with scoped approval; live-session manual tap remains above.
- 2026-08-24 Codex: published `v0.4.1-test2`; GitHub Actions run `32687774746`
  completed green for verify, optional signed-market, and publish jobs. Nine debug APK assets
  are public, including `TerminalSSH-v0.4.1-test2-market-universal-debug.apk`.
- 2026-08-24 Codex: claimed live-session keyboard reopen and adjacent terminal interaction
  audit after a clean worktree and successful `git fetch origin`; GitHub CLI is unavailable.
