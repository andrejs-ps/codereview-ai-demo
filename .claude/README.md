# Guardrail enforcement stages (softest to hardest)

How a rule (here: "Error Prone must pass") can be enforced on an AI agent's work.

| # | Stage                                                                                                     | Enforced by                                            | Strength                | Bypass                                             |
|---|-----------------------------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------|----------------------------------------------------|
| 1 | Instruction in an `.md` file (`CLAUDE.md`)                                                                | Agent compliance                                       | Soft                    | Agent skips or forgets it                          |
| 2 | Check built into the build (Error Prone in `mvn compile`) plus an instruction to build only through `mvn` | Agent compliance; the check rides along on every build | Soft, but hard to miss  | Agent never runs the build                         |
| 3 | Claude Code hook (`PreToolUse` on Bash/PowerShell, in `.claude/settings.json`)                            | Harness                                                | Hard for the agent only | Manual commits, other tools, odd git syntax        |
| 4 | Git `pre-commit` / `pre-push` hook                                                                        | Git                                                    | Hard for everyone       | `--no-verify` (can be denied in agent permissions) |
| 5 | CI required check with branch protection                                                                  | Server                                                 | Hard, final gate        | None locally                                       |

## Current setup

- **Stage 2:** Error Prone is configured in the root `pom.xml` (`maven-compiler-plugin`), with JDK flags in
  `.mvn/jvm.config`.
- **Stage 4:** `githooks/pre-commit` runs `mvn -q compile` and blocks the commit on failure. Activate once per clone:
  `git config core.hooksPath githooks`.
- **Stage 3:** not currently installed. It was tried (a `PreToolUse` hook on Bash that ran `mvn -q compile` for
  `git commit` / `git push` and exited 2 on failure) and replaced by stage 4 as the gate, but it remains a valid stage
  to demo.
- **Not set up:** stages 1 and 5.

## Notes

- Stage 4 is skipped by `git commit --no-verify`.
- Stage 5 is the only stage that actually enforces the rule. Stages 1-4 give fast local feedback.
