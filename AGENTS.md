# Project guidance

Use the existing Java 21 / Spring Boot 3 Maven backend and React / Vite frontend.
Do not upgrade dependencies or apply Next.js-specific patterns to this Vite SPA.
For backend changes use `./mvnw` from the `backend/` directory; for frontend
changes use the scripts in `frontend/package.json`.

## Junior-friendly documentation

Keep existing useful comments. Add concise Czech explanations when introducing
non-obvious framework concepts, near their actual use, rather than commenting
every line. Explain why a concept is used here and distinguish documentation tags
(`@param`) from runtime annotations (`@Param`). Do not change behavior just to
simplify an explanation. Use valid Javadoc for Java API documentation.

## Teaching: `/teach`

Use the installed Matt Pocock skill at `.agents/skills/teach/SKILL.md` when the
user explicitly invokes `/teach` or asks for a teaching session. Keep the upstream
skill unchanged; these project-specific settings supplement its workflow:

- Teach in Czech, including questions, feedback, lessons and cheat sheets.
  Keep code identifiers and annotation names unchanged.
- Use `docs/learning/` at the repository root as the teaching workspace, regardless
  of the current subdirectory. All workspace paths in the upstream skill
  (`MISSION.md`, `RESOURCES.md`, `NOTES.md`, `lessons/`, `reference/`,
  `learning-records/`, `assets/`) resolve inside `docs/learning/`.
  Its `*-FORMAT.md` references still resolve inside the installed skill directory.
- Read `docs/learning/MISSION.md` and existing learning records first. The mission
  is to explain this project to junior developers. If no topic is given, choose
  the next small concept relevant to that mission; start with beans and injection
  when no learning records exist.
- Ground explanations in actual project files and verified official documentation
  matching this project's versions. If a source cannot be fetched, state that
  limitation; do not invent references.
- Teach one small concept per session with a concrete project example, a common
  mistake and a short exercise. Ask one question at a time and wait for the answer.
  Record demonstrated understanding only after the learner answers.
- For each session create a Czech HTML lesson in `docs/learning/lessons/` and
  create or update a printable Czech HTML cheat sheet in `docs/learning/reference/`.
  Use UTF-8, `lang="cs"`, accessible markup, relative links and the shared
  `docs/learning/assets/teaching.css`; no CDN or external scripts are needed.
  Preserve existing notes and increment lesson/learning-record numbers.
- Do not change application code during `/teach` unless the user requests it.

Installed skills live in `.agents/skills/`, with Claude Code links in
`.claude/skills/`. Source information is in `skills-lock.json`. Read the relevant
skill before using it; installation alone does not verify its advice.
