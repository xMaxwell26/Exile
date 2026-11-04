# Custom interface audit — 4 November 2026

Scope: Exile's custom interfaces and overlays. Existing RuneScape tabs and
legacy dialogs are outside this pass. No character progression or balance
changes are part of the cleanup.

## Changes

- Arsenal: skill details, gem sockets, contextual help and binding cards have
  separate space. Binding cards use one centered row; there is no text below
  them or across a socket. Long binding names and large cooldowns are fitted.
- Controls: status and conflict confirmation fit their own rows, with a shorter
  footer. Existing capture, clear, reset and confirmation targets are retained.
- Guide: action cards retain the level-10 entry/death rules and ground-loot
  warning. These notices stay visible while hovering an action's explanation.
- Crafting: eleven materials have a compact overview; the four reference pages
  show three spacious recipe rows with full descriptions, restrictions and
  navigation. Selected equipment, gems and uncut jewels have focused instructions.
- Passive tree: the energy branch and footer are separated. Hovered upgrade
  details, available points and refund status clear the lower frame.
- Item tooltips: wrapped serif titles occupy a full-width band. Advanced stats
  use body columns when required; column splits prefer affix boundaries.
- Tutorial and map trackers: titles and instructions have separate bands.
  Bounds match painting, clear HUD/sidebar regions and stop above chat input
  when a modal is open.
- Custom sidebar tabs: measured paragraphs and compact body type prevent
  captions from crossing their buttons or frames.
- Login: account, password, checkbox, submit and feedback have separate space.
  Long field values keep the cursor visible; connection messages wrap into a
  dedicated two-line band. The world selector follows the viewport size.
- Chat chrome: channel modes occupy one line inside their strip; long drafts
  scroll to keep the caret visible.

## Coverage

Native renders use the production proportional fonts. Generated previews are
under `Notorious/Server/build/`, not source control.

- Arsenal: populated, empty, level 20, all supports, long bindings, long cooldowns.
- Controls: normal and conflict confirmation.
- Guide, passive tree and hovered node.
- Crafting: empty, populated and second inventory page; selected equipment,
  active gem, support gem and uncut jewel; all four reference pages.
- Jewel crafting: All, Sword, Dagger, Mace and Support filters.
- Map device: empty, populated, second page, tier 10, resume, completed and
  abandon states. Expedition supply cards are also rendered.
- Tooltips: normal/advanced, current largest modifier payload, long item name,
  fixed and resized viewports.
- Trackers: tutorial/map states in fixed, narrow and wide viewports, including
  chat-hosted instructions. Custom sidebar captions, character stats, resource
  HUD and input bounds are included in focused checks.
- Login: 765 × 503, 1280 × 720 and 1920 × 1080, long fake credentials,
  wrapped failure feedback and missing optional art.

`dev.ps1 Build` compiles client and server. The focused UI suite covers panel
content, click boundaries, native font bounds, viewport composition, real AWT
input routing and drag scaling. The new checks are registered in `dev.ps1 Test`
and the cloud `dev.sh test` gate. Test logs and native previews are retained in
the ignored build directories for local review.

Final Windows checks passed for 26 panel states, 68 guide/passive contracts,
crafting reference/navigation, gem filters, tooltips, trackers, typography,
controls persistence and AWT routing. Drag scaling passed at four viewport sizes.
The live rebuilt QA client was inspected at fixed 765 × 503, compact resized
766 × 563 and maximized 1920 × 1009. Arsenal Controls clicks and resized recipe
paging were exercised; the live item tooltip and tutorial guidance also cleared
their neighboring controls. The QA character remains in its existing tutorial.

Rendering tests complement visual inspection. They check geometry and content
preservation; a passing pixel count alone does not establish visual quality.
