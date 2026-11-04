from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a[:40]);p.write_text(s.replace(a,b))
p=r/'game/arpg/MapRuns.java';edit(p,'public static void enter(Player p){Run run=', 'public static void enter(Player p){if(!isHome(p)||p.isDead()||p.isTeleporting())throw new IllegalArgumentException("Enter from your private map device");Run run=');edit(p,'if(p.isDisconnected()||p.isDead()', 'if(p.isDisconnected()||p.isDead()') if 'if(p.isDisconnected()||p.isDead()' in p.read_text() else None
# Fix house start calling enter immediately after same-tick teleport: normal map action occurs after client loads, teleport flag clears naturally.
p=r/'game/arpg/PrototypePanel.java';s=p.read_text().replace('freeSlots()<8','freeSlots()<9').replace('eight inventory','nine inventory');s=s.replace('if(old.getRarity()==ItemRoll.Rarity.RARE)throw new IllegalArgumentException("Already rare");','if(old.getRarity()!=ItemRoll.Rarity.NORMAL&&old.getRarity()!=ItemRoll.Rarity.MAGIC)throw new IllegalArgumentException("Requires a normal or magic item");');p.write_text(s)
p=r/'game/model/EntityCharacter.java';edit(p,'hurt(arpgLifeLoss==null?damage.getDamage():arpgLifeLoss, aggressor);','if(damage.getDamage()>0)getCombat().setLastDamageReceived(System.currentTimeMillis());\n        hurt(arpgLifeLoss==null?damage.getDamage():arpgLifeLoss, aggressor);')
