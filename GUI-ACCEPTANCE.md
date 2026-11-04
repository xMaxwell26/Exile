# GUI refresh acceptance — 4 November 2026

The refresh adds proportional antialiased body text, serif headings, charcoal and
oxblood surfaces, bronze inlays, resource bars, clearer skill panels, guide cards,
passive symbols, tooltips, login fields, chat and context menus.

Verified in the local Windows client in fixed mode, a 766 × 563 resized window,
and a maximized 1920 × 1009 viewport. Modal panels center in the playable area;
their drawing, click, hover and gem-drop coordinates use the same origin.
Narrow-window objectives and the HUD clear the inventory and chat.

Live checks confirmed a centered Controls click, inventory tooltips, physical
skill-gem socketing in fixed and resized modes, and removal back to inventory.
A one-tick drag exposed the old inventory hold timer. Gem drops now accept a
deliberate movement without that timer, resolve the press at its original
inventory location and use the actual release endpoint.

Focused checks passed for typography, clipping, text removal, later-panel
occlusion, device-scale art transfer, modal crop/restore/failure handling, guide
actions, jewel filters, viewport bounds, AWT control routing and pointer scaling.
Native renders of the Arsenal, controls, guide, passive tree and jewel picker
were reviewed. The final layout check uses the production proportional font.

The native 2× font diagnostic measured about 3.3 ms per frame for thirty labels
on this PC after warmup; this is a diagnostic, not a performance guarantee.
The logical font warmup check retained buffers instead of allocating glyph
images every frame.

QA also found stale local servers holding the game port. Those instances were
stopped and the new build was used for the final interaction checks. Startup
now waits for the actual bind and exits with an error when the port is occupied;
a second launch was tested and failed correctly.

See `GUI-GUIDE.md` for the shared design rules and `TYPOGRAPHY.md` for font and
device-scale rendering details. Generated previews and QA captures are under
the client/server build directories rather than source control.
