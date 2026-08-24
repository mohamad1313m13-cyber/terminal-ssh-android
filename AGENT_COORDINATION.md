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

- Session-tab accessibility: 228e12d (`fix: expose selected terminal session`). Live
  session tabs now use selectable tab semantics and report which terminal is active. Focused
  instrumentation covers the initial selected state and its transition when a second session
  is chosen; Android-test compilation passed, but no emulator was attached, so execution remains
  pending. Source/market/loop gates, whitespace, both-flavor unit tests and lint, and both debug
  APK builds passed. The shared Kotlin daemon was canceled during compilation; Gradle's clean
  in-process fallback completed successfully. Claude's launcher resources and loop prompt were
  not staged or modified. Commits `228e12d` and `01282cd` are preserved locally; push is blocked
  because GitHub HTTPS credentials are unavailable to this worker. No release is warranted for this accessibility-only increment.
  Next safe task: complete and visually verify the claimed launcher rebuild, then execute this
  focused session-tab test with the launcher/terminal emulator smoke.

- CR-only multiline paste safety: bd47c23 (`fix: confirm CR-only multiline paste`). Paste
  confirmation now recognizes LF, CRLF, and CR separators, preventing CR-only command batches
  from bypassing the multiline safety dialog. Focused API 36 instrumentation passed (1/1), as
  did Android-test compilation, source/market/loop gates, whitespace, both-flavor unit tests and
  lint, and both debug APK builds. Claude's launcher resources and loop prompt were not staged or
  modified. Commits through `9504db9` are on `origin/main` after a concurrent credentialed update;
  the direct Codex push attempt lacked GitHub HTTPS credentials. No release is warranted for this bounded fix.
  Next safe task: complete and visually verify the launcher rebuild, then run the credential-scoped
  live SSH keyboard/paste smoke.

- Terminal symbolic-key accessibility: 0281c1b (`fix: label terminal symbolic keys`). The
  interrupt, end-of-input, clear-screen, and four arrow toolbar actions now expose explicit
  localized Persian and English names instead of relying on glyph pronunciation. Focused API
  36 instrumentation passed (1/1). Source, market, loop, whitespace, Android-test compilation,
  both-flavor unit tests and lint, and both debug APK builds passed. Claude's launcher resources
  and loop prompt were not staged or modified. Commits through `396986c` are pushed to
  `origin/main`. No new release is warranted for this bounded fix. Remaining terminal interaction
  risk is the credential-scoped live SSH keyboard/paste smoke documented below.

- Settings toggle accessibility: 628a05b (`fix: make settings toggle rows accessible`). Each security switch now exposes one full-width,
  48 dp labeled toggle target with Switch role and checked state instead of limiting activation to
  the trailing control. Focused instrumentation covers bounds, clickability, initial/changed state,
  and persistence. Source, market, loop, whitespace, Android-test compilation, and both-flavor
  unit/lint/APK gates passed. The shared Kotlin daemon was concurrently canceled and its incremental
  cache reported corruption, but Gradle's clean in-process fallback completed successfully. No
  emulator is attached, so focused execution remains pending. Other terminal/icon claims were not
  staged or modified by this increment; no release warranted. Commits `628a05b` and `539da0a`
  are preserved locally; the scoped push retry still lacks GitHub HTTPS credentials.

- Host-editor localization: 4f223b6 (`fix: localize host validation errors`). Required-host and invalid-port errors now use
  the existing localized resources instead of always showing Persian. Focused instrumentation
  covers English messages, rejection of both invalid forms, and a successful corrected save.
  Source, market, loop, whitespace, Android-test compilation, and both-flavor unit/lint/APK
  gates passed. No emulator is attached, so the focused test is compiled but not yet executed.
  Claude's launcher resources and loop prompt were not staged or modified by Codex. No release
  warranted for this bounded localization fix. Commits `4f223b6` and `fea000c` are preserved
  locally above `origin/main`; push is blocked by unavailable GitHub HTTPS credentials.

- Terminal modifier accessibility: 7c8e083 (`fix: expose terminal modifier state`). Ctrl and Alt now expose Android toggle
  semantics and current checked state while preserving their mutual exclusion and 48 dp
  targets. Focused API 36 instrumentation passed (1/1); an initial run was externally
  interrupted when the shared app package was removed during Claude's launcher audit, with
  no assertion/product crash. Source, market, loop, whitespace, Android-test compilation,
  and both-flavor unit/lint/APK gates passed. Claude's launcher resources and loop prompt
  were not staged or modified by Codex. No release warranted for this bounded accessibility
  fix. Commits through `309d21b` are pushed to `origin/main`. Next safe task: wait for
  Claude's launcher claim, then inspect the committed icon set
  and complete any remaining common-mask/emulator launcher verification.

- Settings palette accessibility: 844a761 (`fix: expose terminal palette choices`). Each terminal palette now exposes a
  localized name, radio-button role and selected state from a 48 dp target; six targets fit
  a 360 dp handset without clipping. The focused API 36 test compiles and covers bounds,
  initial/changed selection, and persistence. Source, market, loop, whitespace, Android-test
  compilation, and both-flavor unit/lint/APK gates passed. No emulator was attached, so the
  focused instrumentation test remains authored but not executed. Claude's launcher files
  were not staged or modified; no release warranted for this bounded accessibility fix.

- Snippet delete accessibility: acd567c (`fix: identify snippet delete actions`). Each
  delete action now announces its snippet name from the full Material button. A focused API
  36 emulator test verified two distinct localized actions, a minimum 48 dp bound, removal of
  only the selected metadata record, and preservation of the other record (1/1). Source,
  market, and loop gates; whitespace; Android-test compilation; both-flavor unit tests, lint,
  and debug APK builds passed. Claude's claimed launcher resources were not staged or modified.
  Commits through `44137de` are pushed to `origin/main`. No release warranted for this bounded
  accessibility fix.

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

- 2026-08-24 Codex: added selected-state/tab semantics for live terminal sessions and focused
  instrumentation; Android-test compilation and every static/unit/lint/APK gate passed, with
  emulator execution pending because no device is attached. Claude's launcher work was untouched.
- 2026-08-24 Codex: committed session-tab accessibility as `228e12d` with handoff `01282cd`;
  the scoped push reached GitHub but could not authenticate, so both commits remain local.

- 2026-08-24 Codex: fixed CR-only multiline paste detection, passed focused API 36
  instrumentation (1/1) and every required static/build gate, and committed `bd47c23` without
  touching Claude's launcher work.
- 2026-08-24 Codex: committed handoff `9504db9`; the direct push lacked GitHub HTTPS
  credentials, then a concurrent credentialed update advanced `origin/main` through the handoff.

- 2026-08-24 Codex: claimed and completed localized terminal symbolic-key labels; focused API
  36 instrumentation passed after correcting a viewport-dependent assertion, and all required
  static/build gates passed without touching Claude's launcher work.
- 2026-08-24 Codex: pushed the verified symbolic-key increment and handoff through `396986c`;
  launcher work remains Claude-claimed and live SSH input remains blocked on a disposable endpoint.

- 2026-08-24 Codex: completed full-row settings-toggle semantics and focused instrumentation;
  all static/build gates passed via Gradle's fallback compiler, with emulator execution pending.
- 2026-08-24 Codex: committed settings accessibility `628a05b` and handoff `539da0a`; push
  remains blocked by unavailable HTTPS credentials, without staging concurrent worker files.

- 2026-08-24 Codex: claimed and completed localized host-editor validation with focused
  instrumentation; all static/build gates passed, with execution pending an attached emulator.
- 2026-08-24 Codex: committed host validation `4f223b6` and handoff `fea000c`; scoped push
  failed because GitHub HTTPS credentials are unavailable, leaving both commits preserved.

- 2026-08-24 Codex: a scoped retry pushed the terminal-modifier increment and accumulated
  verified accessibility commits through `309d21b`; Claude's launcher work remains untouched.

- 2026-08-24 Codex: claimed terminal-palette names, selected-state semantics, 48 dp targets,
  and focused emulator coverage; Claude's launcher resources remain untouched.
- 2026-08-24 Codex: passed Android-test compilation and all static/unit/lint/APK gates;
  cleared the palette claim with focused instrumentation pending an available emulator.
- 2026-08-24 Codex: committed palette accessibility `844a761` and handoff `712f21f`;
  two scoped pushes failed because GitHub HTTPS credentials are unavailable, so the verified
  local commits remain preserved above `origin/main` without staging Claude's launcher work.

- 2026-08-24 Codex: claimed snippet-specific delete accessibility labels, 48 dp action
  target, and focused emulator coverage; Claude's launcher resources remain untouched.
- 2026-08-24 Codex: passed the focused API 36 instrumentation test (1/1), all required static
  and both-flavor build gates, then cleared the snippet claim without touching launcher files.
- 2026-08-24 Codex: pushed the verified snippet increment and handoff through `44137de`;
  Claude's active launcher files and the unrelated loop prompt remain unstaged and untouched.

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
