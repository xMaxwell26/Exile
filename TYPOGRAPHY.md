# Exile typography

Custom interfaces use proportional antialiased fonts. Existing archive-backed fonts are unchanged unless a caller explicitly opts in.

| Use | API | Preferred installed face |
| --- | --- | --- |
| Body and controls | `ExileTheme.body()` | Segoe UI, 13 px |
| Dense secondary details | `ExileTheme.small()` | Segoe UI, 11.5 px |
| Section headings | `ExileTheme.headingFont()` or `heading(x, baseline, text, color)` | Cambria, 18 px |
| Chat/login RSFont calls | `RSFont.exileBody()` / `exileSmall()` | Same body/small faces |

Trebuchet MS and Georgia/logical Java fonts provide fallbacks when the preferred Windows faces are unavailable. Sizes are logical pixels; native device-scale text is prepared separately.

Pass the same `TextDrawingArea` instance to measurement and drawing. Its `getTextWidth`, centered methods and `method389` use the same glyph advances. `ExileTheme.label` honors the supplied font. Use `ExileTheme.headingWidth` when fitting a serif heading. Body and small text support legacy `@red@`-style colors and `<col=hex>` / `</col>` spans. These are explicit UI font adapters, not replacements for every legacy animated-font effect.

The client presentation hook calls `ExileTypography.present(graphics, raster, xOffset, yOffset)` after the software surface and item art. At 1×, the software glyph masks already have antialiased edges. Above 1×, cached runs are rasterized from the vector font at the actual device scale, then replayed with pixel-mask occlusion. This avoids magnifying the old bitmap font and avoids per-frame font layout/geometric clip unions.

`ExileTypography.clear(raster)` starts a new generation while retaining reusable buffers. It prevents closed or removed labels from reappearing. A centered local overlay uses `copySurface(source, target, targetStride, dx, dy)` after copying its pixels; offsets translate the entire source canvas. The transfer respects the active destination clip. It preserves the before/after pixels needed to keep later panels above the text.

`ExileTypographyCheck` covers proportional metrics, inline-color measurement, AA coverage, software clipping, later-panel occlusion, removed text, translated overlays and fresh 2× detail. Its timing/allocation printouts are diagnostics without timing assertions. Native previews are `Server/build/exile-typography-1x.png`, `exile-typography-2x.png` and `exile-typography-occlusion.png`.
