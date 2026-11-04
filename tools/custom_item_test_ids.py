from pathlib import Path
p=Path('Notorious/Server/test/ArpgFoundationTest.java');s=p.read_text();
for old,new in [('Container.transfer(inv,bank,1,1277)','Container.transfer(inv,bank,1,a.getId())'),('Container.transfer(full,new Container(Container.Type.STANDARD,0),0,1277)','Container.transfer(full,new Container(Container.Type.STANDARD,0),0,a.getId())'),('Bank.deposit(player,0,1277,1,true)','Bank.deposit(player,0,a.getId(),1,true)'),('Bank.deposit(player,1,1277,1,true)','Bank.deposit(player,1,a.getId(),1,true)'),('Bank.withdrawSelected(player,1277,1,1,','Bank.withdrawSelected(player,a.getId(),1,1,'),('cache.get(3214,0,1277)','cache.get(3214,0,a.getId())'),('cache.get(3214,1,1277)','cache.get(3214,1,a.getId())')]:s=s.replace(old,new)
p.write_text(s)
