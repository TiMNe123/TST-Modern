# TST-Modern Local Agent Contract

## Local-only agent files

- `AGENTS.md` and everything under `.agents/` are local workflow files. Never stage, commit, or push them.
- Never bypass their ignore rules with `git add -f`, a negated ignore rule, or an equivalent command unless the user explicitly reverses this policy.
- Before every commit, inspect `git diff --cached --name-only` and stop if it contains `AGENTS.md`, `.agents/**`, or another ignored local artifact.

## Multiblock port workflow

- Before editing production code for a new TST multiblock port, read and follow `.agents/skills/port-tst-multiblock-gtceu/SKILL.md` completely.
- Work directly in the existing `dev` checkout. Do not create a branch or worktree unless the user explicitly requests one.
- Check the current branch and working tree first. Never switch away from dirty changes.
- Audit the user-provided local source directory, currently `D:\tmp`. Do not substitute GitHub, another remote copy, or a similar machine without explicit approval.
- Complete the source audit and machine contract, validate the contract, and obtain user approval before implementation.
- Never infer structure axes, casing/meta mappings, recipes, mechanics, tiers, research, or textures from a similar machine.
- Follow the port skill's controller, tooltip, casing-name, localization, and color requirements exactly.
- Run the machine-contract, casing, locked-texture, formed-appearance, localization, task-relevant tests, and full build gates before reporting completion.

## Code discovery

- If `.codegraph/` exists, use CodeGraph before text search when locating or understanding code.
- Prefer codebase-memory graph tools when available. Use text search for literals, resources, configuration, non-code files, or when graph results are insufficient.
- Read the real call path before changing behavior. Fix shared root causes rather than patching one symptom.

## ECC usage

- ECC capabilities are optional helpers, not project requirements. Higher-priority instructions, this contract, and the user's explicit request always win.
- Use an ECC skill or specialized agent only when it is available, relevant, and proportionate to the task.
- Handle straightforward or single-file work directly. Use parallel agents only for independent, bounded work that materially reduces risk or latency.
- For new behavior and bug fixes, prefer a small failing regression test before implementation when the repository has a suitable test surface.
- Do not impose universal 80% coverage, integration tests, or E2E tests when the project has no corresponding measurable gate. The port skill's validators are authoritative for multiblock ports.
- Review material code changes before commit and resolve critical or high-confidence findings. Trivial local policy or documentation edits do not require agent fan-out.
- Prefer immutable value objects and snapshots, but allow controlled mutation required by Forge/GTCEu lifecycle state, registries, builders, NBT, and bounded caches.
- Apply security checks according to the changed attack surface. Always check staged changes for secrets; apply web, database, authentication, CSRF, XSS, and rate-limit checks only when those surfaces exist.
- Do not introduce generic API envelopes, repository layers, skeleton projects, or plugin directory structures unless the actual TST-Modern feature requires them.
- Do not add ECC-generated `agents/`, `skills/`, `commands/`, `hooks/`, `rules/`, or `mcp-configs/` directories to this repository unless explicitly requested.

## Git workflow

- Do not commit, push, merge, open a pull request, create a branch/worktree, or rewrite history unless the user explicitly requests that action.
- Commit messages must contain only a concise description. Do not add a Conventional Commit type/scope prefix unless the user explicitly requests one.
- Before committing, inspect the exact staged file list, run `git diff --cached --check`, and verify that no secret or ignored local artifact is staged.
- When the user requests the established release flow: validate on `dev`, push `dev`, update `main` safely, merge without conflicts using an explicit merge commit, validate on `main`, then push `main`.
- Never force-push or rewrite shared history without explicit authorization. When authorized, use exact remote leases and create a recoverable local backup first.

## Runtime and permissions

- Use task-appropriate Gradle tests and a full build for production changes. Run client/world/JEI smoke checks when runtime behavior is involved or the user requests them.
- The session already has `danger-full-access`. Omit sandbox escalation parameters unless an exact operation is denied under a narrower mode.
- Never expose secrets, silently discard user changes, or perform destructive filesystem operations outside an explicitly verified target.
