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

- Claude: launcher icon rebuild from the user artwork. Files claimed:
  `app/src/main/res/drawable/ic_launcher_background.xml`,
  `app/src/main/res/drawable/ic_launcher_foreground.xml`,
  `app/src/main/res/drawable/ic_launcher_monochrome.xml`,
  `app/src/main/res/mipmap-anydpi-v26/*`, `app/src/main/res/mipmap-*dpi/*`.
- Codex: unclaimed

## Latest verified handoff

- Private-key delete accessibility: 71a941d (`fix: expose accessible key deletion`).
  Each delete action now announces the intended key name from a full 48 dp Material button.
  API 36 instrumentation verified the localized action, minimum 48 dp bounds, disappearance
  after activation, and deletion of the intended metadata record (1/1). Source, market, and
  loop gates; whitespace; Android-test compilation; both-flavor unit tests, lint, and debug
  APK builds passed. Claude's active launcher resources were not staged or modified by Codex.
  Commits through `f45314d` are pushed to `origin/main`. No release warranted for this bounded
  accessibility fix.

- Saved-host action accessibility: 555cf93 (`fix: expose accessible host actions`).
  Favorite/unfavorite and edit actions now
  announce localized, host-specific names from their full 48 dp buttons; the edit glyph is
  a standard vertical-more icon. A focused API 36 emulator test verifies both 48 dp bounds,
  the favorite state/label transition, and opening the correct edit sheet. Source, market,
  and loop gates; whitespace; both-flavor unit tests, lint, and debug APK builds passed.
  The first emulator run exposed 24 dp child-icon semantics; moving semantics to the parent
  button fixed it and the rerun passed 1/1. No release warranted for this bounded fix.
  Commits through `77ddb9f` are pushed to `origin/main`.

- Multiline paste safety coverage: 35ca138 (`test: cover multiline paste cancellation`).
  API 36 instrumentation proves a two-line clipboard opens the localized confirmation and
  Cancel dismisses it while clearing the paste request. Target Android-test compilation,
  the focused emulator test (1/1), source/market/loop gates, whitespace, both-flavor unit
  tests, lint, and debug APK builds passed. No product code or launcher resource was changed;
  no release warranted. Commits through `05318cd` are pushed to `origin/main`.

- README release-link correction: 8f92f76 (`docs: update current APK download links`). The
  primary CTA and four listed downloads now point
  to `v0.4.1-test3`; all five changed GitHub URLs returned HTTP 200. Source, market, loop,
  and whitespace gates passed. Android builds were not rerun for this docs-only increment
  while Claude's launcher resources are intentionally incomplete. No release warranted.

- Accessibility commit: 6a48ffb (`fix: identify session close actions`). Each session-tab
  close control now announces its host title in Persian and English while retaining its
  48 dp target; instrumentation proves that both distinct actions are discoverable and that
  selecting one removes only its intended session. Verified in an isolated clean checkout
  because Claude's active launcher rebuild temporarily has duplicate mdpi resource names.
- Push status: commits through `8f92f76` are on `origin/main`; a later credentialed retry
  succeeded without touching Claude's uncommitted launcher resources.
- Tests: source, market, and loop gates passed; market and gplay unit tests, lint, and debug
  APK builds passed; both `TerminalKeyboardTest` cases passed on the API 36 emulator.
- Release: none for this accessibility-only increment; the latest test release remains below.
- Next: complete and visually verify the claimed launcher rebuild, then run a disposable live
  SSH interaction smoke covering keyboard, paste, hardware keyboard, rotation, and Back.

- Version: 0.4.1 test release 3
- Commit: b8744fa (`test: harden terminal IME recovery coverage`)
- Release: https://github.com/mohamad1313m13-cyber/terminal-ssh-android/releases/tag/v0.4.1-test3
- Verified: source/market/loop gates; market and gplay unit tests, lint, and debug APKs;
  seven market emulator instrumentation tests on API 36. The terminal regression now checks
  two consecutive Back/reopen cycles plus another dismissal, landscape rotation, and reopen;
  Android reported the IME visible after every recovery. All passed on 2026-08-24.
- GitHub Actions: run `32689842441` completed green for verify, optional signed-market,
  and publish; nine debug APK assets are public on the test release.
- Fixed: Compose focus alone did not reopen termlib's custom editor. The toolbar now finds
  and focuses the embedded terminal text-editor view, then requests the IME through Android's
  input manager, retaining the Compose keyboard controller as a fallback.
- Remaining risk: live remote command input, paste, and a physical hardware-keyboard path
  were not exercised; rotation is covered in-place because MainActivity handles configuration
  changes, and the regression test uses a credential-free idle terminal session.
- Next: restore the launcher icon from the exact user artwork, then run a disposable live SSH
  interaction smoke covering keyboard, paste, hardware keyboard, rotation, and Back.

## Work log

Append short timestamped entries. Keep this section concise.

- 2026-08-24 Codex: claimed private-key delete-action labeling, 48 dp touch target, and
  focused emulator coverage after a successful fetch; Claude's launcher files remain untouched.
- 2026-08-24 Codex: corrected the Material navigation test selector after the first emulator
  run, then passed focused API 36 instrumentation (1/1), all required gates, and cleared the claim.
- 2026-08-24 Codex: pushed the verified private-key action increment and handoff through
  `f45314d`; Claude's uncommitted launcher resources remain untouched.
- 2026-08-24 Codex: committed the verified key-action increment and handoff; scoped push failed
  because GitHub HTTPS credentials are unavailable, so both local commits remain preserved.

- 2026-08-24 Codex: claimed saved-host favorite/edit accessibility labels and 48 dp touch
  targets after fetching origin; Claude's launcher resources remain untouched.
- 2026-08-24 Codex: API 36 caught inner-icon 24 dp semantics; moved labels to the full
  buttons, passed focused instrumentation (1/1) and all required static/build gates, and
  cleared the claim without touching Claude's launcher work.
- 2026-08-24 Codex: preserved verified commits `555cf93` and `095f303`; push is blocked by
  unavailable GitHub HTTPS credentials after two scoped attempts.
- 2026-08-24 Codex: a later scoped credential retry succeeded; pushed the verified host-action
  increment and handoff through `77ddb9f` without touching Claude's launcher work.

- 2026-08-24 Codex: claimed bounded emulator coverage for multiline paste confirmation and
  cancellation after fetching origin; Claude's launcher resources remain untouched.
- 2026-08-24 Codex: verified the focused API 36 test (1/1), Android-test compilation, all
  three static gates, and whitespace; committed `35ca138` and cleared the claim.
- 2026-08-24 Codex: push failed because GitHub HTTPS credentials are unavailable; remote
  remains `8f92f76`, with verified local commits preserved and launcher work untouched.
- 2026-08-24 Codex: reran both-flavor unit/lint/APK gates and pushed the five accumulated
  verified commits through `05318cd`; Claude's uncommitted launcher work remains untouched.

- 2026-08-24 Codex: claimed the bounded README download-link correction after fetching
  origin and preserving Claude's active launcher-resource work unchanged.
- 2026-08-24 Codex: verified all five test3 release URLs with HTTP 200 plus source, market,
  loop, and whitespace gates; claim cleared with launcher work still untouched.
- 2026-08-24 Codex: pushed the four verified local commits through `8f92f76` after scoped
  GitHub credentials became available; origin now includes the accessibility and link fixes.

- 2026-08-24 Codex: claimed bounded TalkBack-specific session close labeling and targeted
  emulator coverage after confirming Claude's launcher resource claim and fetching origin.
- 2026-08-24 Codex: verified session-specific close announcements in both locales and targeted
  close behavior with both API 36 terminal tests; all static, unit, lint, and APK gates passed
  in an isolated clean checkout. Committed `6a48ffb`; claim cleared, no release warranted.
- 2026-08-24 Codex: push blocked twice by missing HTTPS credentials; preserved both local
  commits and Claude's staged/uncommitted launcher files without modification.

- 2026-08-24 Codex: claimed bounded terminal IME rotation/repeated-reopen instrumentation
  after a clean tracked worktree and successful `git fetch origin`; launcher resources remain
  exclusively claimed by Claude.
- 2026-08-24 Codex: pushed verified IME resilience coverage `b8744fa`; all gates, both
  flavor unit/lint/APK builds, the focused test, and all seven API 36 instrumentation tests
  passed. Claim cleared; no new APK published because this is test-only hardening.
- 2026-08-24 Codex: further terminal interaction work is safely blocked: no disposable SSH
  endpoint or credentials are present to observe live command/paste bytes, and Claude is using
  the shared emulator for the claimed icon audit. Safe next task is a credential-scoped live
  keyboard/paste/hardware-keyboard smoke after the icon work releases the emulator.

- 2026-08-24 Claude: claimed the launcher icon rebuild from `/root/file_00000000143882438f4989f08e461e03.jpg`
  after a clean tracked worktree, successful `git fetch origin`, and local main level with origin.

- 2026-08-24 Codex: claimed bounded terminal keyboard-action emulator coverage and clean
  disposable live-session smoke after clean tracked status and successful origin fetch.
- 2026-08-24 Codex: reproduced the reopen failure on API 36, fixed the real termlib input-view
  focus path, added a passing regression test, pushed `8fc9fb8`, and cleared the claim.
- 2026-08-24 Codex: published `v0.4.1-test3`; Actions run `32689842441` completed green
  and the universal market test APK plus eight flavor/ABI debug APKs are public.

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
