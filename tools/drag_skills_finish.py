from pathlib import Path
p=Path('Notorious/Client/src/com/koios/client/Client.java');s=p.read_text().replace('if(super.clickMode2==1)skillDrag.move(mouseX-offset,mouseY-offset);else{String change=', 'if(super.clickMode2==1)skillDrag.move(mouseX-offset,mouseY-offset);else if(super.clickMode2==0){String change=').replace('if(change!=null)sendArpgSkill(change);}}','if(change!=null)sendArpgSkill(change);}else skillDrag.cancel();}');p.write_text(s)
p=Path('PROTOTYPE-GUIDE.md');s=p.read_text();s+='''

## Drag-and-drop skills

Press **K** for the skill loadout. Click a skill card to select it. Drag a skill card (or an existing hotbar tile) onto Q, W or E to swap assignments. The keyboard and clickable gameplay bar execute the skill assigned to that slot; labels and cooldowns follow the assignment. Hotbar order saves with the character.

Drag Splash, Faster Attacks or Brutality from the support library onto a skill card to attach it. Drag a filled support socket onto another skill card to move it, or back into the library to remove it. Dropping outside a valid target cancels; Escape, closing the panel or losing focus cancels an active drag. Repeated support drops are idempotent. Support levels stay with the learned support.

Skill cards display gem level, supported damage effectiveness, mana cost and cooldown. The guided tutorial now asks for a support drag and uses the actual assigned keys when explaining Heavy Strike and Cleave. The existing keyboard and mouse rebinding TODO remains separate from assigning skills to Q/W/E.

Also fixed Whirlwind's cooldown ticking: it now counts down and permits repeated casts.
''';p.write_text(s)
p=Path('TODO.md');s=p.read_text();s+='''
## Skill loadout play checks

- K now supports skill-to-hotbar swaps and support attach/move/remove by drag-and-drop. Test held drags, quick drags, off-panel cancellation, focus loss and resizable coordinates.
- Check that Q/W/E, clickable hotbar labels, cooldowns, tutorial prompts and saved order agree after swaps and reconnects.
- Extend input binding beyond the current Q/W/E slots to the keyboard/mouse controls listed above.
''';p.write_text(s)
