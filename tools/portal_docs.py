from pathlib import Path
p=Path('PROTOTYPE-GUIDE.md');s=p.read_text();needle='After the introduction,';idx=s.index(needle);s=s[:idx]+'''Maps show an objective tracker at the top right: defeat the Settlement Champion. It includes the boss direction, distance and remaining enemy count. A hint arrow marks the boss. On boss death, it changes to **MAP COMPLETE**, marks a return portal and announces completion. Collect ground loot before leaving or closing the map.

**Portal Scrolls** (Read in inventory) open a return portal near you and a paired map portal at your private base. Walk up to the return portal and click Return-to-base; use the Map portal at home to re-enter near where you opened it. Creating another portal replaces the old return location. Maps start with an entrance portal, and the boss creates a free exit even if you have no scrolls. Starter kits include five scrolls; existing players can buy five for 25 coins in P / Guide, and enemies drop more. Entry limits from level 10 still apply. Active maps and their portals close on logout or expiry.

'''+s[idx:];p.write_text(s)
