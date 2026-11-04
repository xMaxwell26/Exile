# Exile interface design

The interface uses charcoal, worn bronze, ivory and oxblood. Keep the stronger
blue, green, violet and red accents for resources, gem identity and item rarity.
Titles use a restrained serif; instructions and numbers use proportional body
type. Use short, mixed-case labels and describe the player's action directly.

## Shared components

- `ExileTheme` owns the palette, frames, insets and font entry points.
- `ExileTheme.heading()` draws panel titles. Body text uses `body()`, compact
  counters use `small()`. Measure with the same font used to draw the text.
- `ExileTypography` caches antialiased glyphs and presents text at device scale.
  Do not enlarge the entire game raster to make text larger.
- `OverlayViewport` centers a 512 by 334 modal in the available play area. Its
  origin is also used by client clicks, hover, gem drops and drags. Inventory
  coordinates remain in the main client space.
- `HighDpiArt.copySurface` and `ExileTypography.copySurface` transfer sharp art
  and text when an overlay is composited. Keep both calls when changing the
  compositor, and preserve the original raster and clip even after an exception.

Keep decorative strokes inside component bounds. Tooltips must wrap and fit
the viewport; descriptions must never cross into adjacent sockets or buttons.
The inventory's twenty-eight cells are reserved for items: do not draw titles,
counts or other footer text inside the grid or across its final row.
Avoid exposing implementation details such as instance IDs in player menus.

Give titles, descriptions, controls and footer help their own vertical bands.
Leave at least five pixels between native text (including its shadow and
descenders) and an inset or panel edge. Reserve a separate full-width title
band for item tooltips; long modifier lists can flow into body columns.
Wrap recipe rules instead of shortening them. Keep gameplay warnings visible
when replacing a server text list with cards, including non-clickable rows.
Rendering and hit testing must share the same control bounds.

## Verification

`dev.ps1 Build` builds the client and server. `dev.ps1 Test` includes the font,
modal, guide, jewel menu and resource previews alongside the existing checks.
Use Java 8 for the running server and integration checks.

The most relevant checks are `ExileTypographyCheck`, `OverlayViewportCheck`,
`GuidePresentationCheck`, `GemcraftCardsCheck`, `ArsenalPanelPreview`,
`HighDpiArtCheck`, `ControlRoutingCheck` and `ViewportInputCheck`.
`InterfaceStatesCheck`, `InterfaceTextBoundsCheck`, `ChatChromeLayoutCheck`
and `ExileLoginCheck` cover the custom interface audit recorded in
`INTERFACE-AUDIT.md`. These checks are included in both development gates.

Inspect the live game in fixed mode, a normal resized window and a maximized
window. Click a centered control, hover an inventory item, and drag a physical
gem into and out of its socket. Headless render checks complement this inspection;
their pixel counts are not a claim of artistic quality.
