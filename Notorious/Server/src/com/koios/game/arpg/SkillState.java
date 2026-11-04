package com.koios.game.arpg;
import com.koios.game.model.item.Item;
/** Server-owned skill resource and cooldown state, advanced once per world tick. */
public final class SkillState {
 /** Actual socketed rolls preserve distinct levels and identity; absent records are legacy saves. */
 private final Item[] physicalGems=new Item[20];
 private int physicalIndex(int skill,int support){if(skill<0||skill>=SKILLS||support< -1||support>2)throw new IllegalArgumentException("Invalid gem socket");return skill*4+support+1;}
 public boolean hasPhysicalGems(){for(Item gem:physicalGems)if(gem!=null)return true;return false;}
 public void validatePhysicalGem(Item item){if(!MapRuns.isGem(item)||item.getCount()!=1)throw new IllegalArgumentException("Choose one gem");for(Item gem:physicalGems)if(gem!=null&&gem.getRoll().getInstanceId().equals(item.getRoll().getInstanceId()))throw new IllegalArgumentException("That gem is already socketed");}
 public void recordGem(int skill,int support,Item gem){physicalGems[physicalIndex(skill,support)]=gem.copy();}
 public Item socketedGem(int skill,int support){Item gem=physicalGems[physicalIndex(skill,support)];return gem==null?MapRuns.gem(support<0?skillLevel(skill):supportLevel(skill,support),support>=0,support<0?skill:support):gem.copy();}
 public void forgetGem(int skill,int support){physicalGems[physicalIndex(skill,support)]=null;}
 public int supportLevel(int skill,int support){Item gem=physicalGems[physicalIndex(skill,support)];return gem==null?supportLevel(support):gem.getRoll().getGem().level;}
 public String savePhysicalGems(){StringBuilder out=new StringBuilder();for(Item gem:physicalGems){if(out.length()>0)out.append(',');out.append(gem==null?"-":ItemRollCodec.encode(gem));}return out.toString();}
 /** Decode to a temporary ledger first: a bad save cannot partially replace live sockets. */
 public void loadPhysicalGems(String text){
  String[] fields=text.split(",",-1);
  if(fields.length!=20)throw new IllegalArgumentException("Invalid physical gem save");
  Item[] next=new Item[20];
  java.util.Set<String> identities=new java.util.HashSet<>();
  for(int i=0;i<next.length;i++){
   if(fields[i].equals("-"))continue;
   boolean support=i%4!=0;
   Item gem=ItemRollCodec.restore(new Item(support?22412:22411),fields,i);
   int expectedKind=support?i%4-1:i/4;
   if(!MapRuns.isGem(gem)||gem.getRoll().getGem().support!=support
      ||gem.getRoll().getGem().kind!=expectedKind
      ||!identities.add(gem.getRoll().getInstanceId()))
    throw new IllegalArgumentException("Invalid socketed gem identity");
   next[i]=gem;
  }
  System.arraycopy(next,0,physicalGems,0,next.length);
 }
 private double mana=50;
 public static final int SKILLS=5;
 private final int[] gemLevels={1,1,1,1,1,1,1,1};
 private final int[] ownedSupports=new int[3];
 public int owned(int support){return ownedSupports[support];}
 public int available(int support){int used=0;for(int mask:supports)if((mask&(1<<support))!=0)used++;return Math.max(0,ownedSupports[support]-used);}
 public void learnSupport(int support,int level,int character){if(support<0||support>2||level<1||level>20||character<Math.max(1,level*4-3))throw new IllegalArgumentException("Support gem level is too high");if(ownedSupports[support]>=1000000)throw new IllegalArgumentException("Support collection is full");ownedSupports[support]++;gemLevels[2+support]=Math.max(gemLevels[2+support],level);active=true;}
 public int removeSupport(int support){if(support<0||support>2||ownedSupports[support]<=0)throw new IllegalArgumentException("No socketed support gem");ownedSupports[support]--;active=true;return gemLevels[2+support];}
 public String saveOwned(){return ownedSupports[0]+","+ownedSupports[1]+","+ownedSupports[2];}
 public void loadOwned(String value){String[] fields=value.split(",");if(fields.length!=3)throw new IllegalArgumentException("Invalid support stock");int[] next=new int[3];for(int i=0;i<3;i++){next[i]=Integer.parseInt(fields[i]);if(next[i]<0||next[i]>1000000)throw new IllegalArgumentException("Invalid support stock");}System.arraycopy(next,0,ownedSupports,0,3);}
 public void reconcileOwnership(){for(int support=0;support<3;support++){int left=ownedSupports[support];for(int skill=0;skill<SKILLS;skill++)if((supports[skill]&(1<<support))!=0){if(left>0)left--;else supports[skill]&=~(1<<support);}}}
 public void clear(int slot){if(slot<0||slot>=3)throw new IllegalArgumentException("Invalid slot");hotbar[slot]=-1;active=true;}
 public int skillLevel(int id){return gemLevels[id>=2?id+3:id];}
 public int supportLevel(int id){return gemLevels[2+id];}
 public void upgrade(int id,int level,int character){if(id<0||id>7||level<1||level>20||level<=gemLevels[id]||character<Math.max(1,level*4-3))throw new IllegalArgumentException("Gem is not an eligible upgrade for your character");gemLevels[id]=level;}
 public String saveLevels(){StringBuilder s=new StringBuilder();for(int level:gemLevels){if(s.length()>0)s.append(',');s.append(level);}return s.toString();}
 public void loadLevels(String value){String[] f=value.split(",");if(f.length!=5&&f.length!=6&&f.length!=8)throw new IllegalArgumentException("Invalid gem save");int[] next={1,1,1,1,1,1,1,1};for(int i=0;i<f.length;i++){next[i]=Integer.parseInt(f[i]);if(next[i]<1||next[i]>20)throw new IllegalArgumentException("Invalid gem level");}System.arraycopy(next,0,gemLevels,0,8);}
 private final int[] cooldowns=new int[SKILLS],supports=new int[SKILLS];
 private final int[] hotbar={-1,-1,-1};
 private int levelIndex(int skill){if(skill<0||skill>=SKILLS)throw new IllegalArgumentException("Invalid skill");return skill>=2?skill+3:skill;}
 public void socketActive(int slot,int skill,int level,int character){if(slot<0||slot>=3||skill<0||skill>=SKILLS||hotbar[slot]>=0)throw new IllegalArgumentException("That active socket is occupied");if(level<1||level>20||character<Math.max(1,level*4-3))throw new IllegalArgumentException("Skill gem level is too high");for(int equipped:hotbar)if(equipped==skill)throw new IllegalArgumentException("That skill is already socketed");gemLevels[levelIndex(skill)]=level;hotbar[slot]=skill;active=true;}
 public int unsocketActive(int slot){if(slot<0||slot>=3||hotbar[slot]<0)throw new IllegalArgumentException("That active socket is empty");int skill=hotbar[slot];hotbar[slot]=-1;active=true;return skill;}
 public int assigned(int slot){if(slot<0||slot>=3)throw new IllegalArgumentException("Invalid hotbar slot");return hotbar[slot];}
 public void assign(int slot,int skill){if(slot<0||slot>=3||skill<0||skill>=SKILLS)throw new IllegalArgumentException("Invalid skill assignment");int old=hotbar[slot];for(int i=0;i<3;i++)if(hotbar[i]==skill){hotbar[i]=old;break;}hotbar[slot]=skill;active=true;}
 public String keyFor(int skill){for(int i=0;i<3;i++)if(hotbar[i]==skill)return new String[]{"Q","W","E"}[i];return "?";}
 public String saveHotbar(){return hotbar[0]+","+hotbar[1]+","+hotbar[2];}
 public void loadHotbar(String value){String[] f=value.split(",");if(f.length!=3)throw new IllegalArgumentException("Invalid hotbar save");boolean[] used=new boolean[SKILLS];int[] next=new int[3];for(int i=0;i<3;i++){int id=Integer.parseInt(f[i]);if(id< -1||id>=SKILLS||id>=0&&used[id])throw new IllegalArgumentException("Invalid hotbar save");next[i]=id;if(id>=0)used[id]=true;}System.arraycopy(next,0,hotbar,0,3);active=true;}
 public void attach(int skill,int support,boolean enabled){if(skill<0||skill>=SKILLS||support<0||support>=3)throw new IllegalArgumentException("Invalid support assignment");if(enabled){if((supports[skill]&(1<<support))==0&&available(support)==0)throw new IllegalArgumentException("Find and learn a support gem first");supports[skill]|=1<<support;}else supports[skill]&=~(1<<support);active=true;}
 public void moveSupport(int from,int to,int support){if(from<0||from>=SKILLS||to<0||to>=SKILLS||support<0||support>=3||(supports[from]&(1<<support))==0)throw new IllegalArgumentException("Invalid support move");if(from!=to){attach(from,support,false);attach(to,support,true);}}
 private int actionLock;
 private boolean active;
 private String lastPayload;
 public int getSupport(int skill){if(skill<0||skill>=SKILLS)throw new IllegalArgumentException("Invalid skill");return supports[skill];}
 public void setSupport(int skill,int mask){if(skill<0||skill>=SKILLS||mask<0||mask>7)throw new IllegalArgumentException("Invalid support selection");supports[skill]=mask;}
 public void toggle(int skill,int support){if(skill<0||skill>=SKILLS||support<0||support>2)throw new IllegalArgumentException("Invalid support selection");attach(skill,support,(supports[skill]&(1<<support))==0);active=true;}
 public double cost(int skill){return new double[]{12,18,24,20,16}[skill]*(1+0.03*(skillLevel(skill)-1))*((supports[skill]&4)!=0?1.4:1)*((supports[skill]&1)!=0?1.2:1);}
 public double effectiveness(int skill){return new double[]{1.8,.9,1.15,1.3,2.0}[skill]*(1+0.05*(skillLevel(skill)-1))*((supports[skill]&4)!=0?(1.3+0.01*(supportLevel(skill,2)-1)):1);}
 public boolean spend(int skill,double aps){
  if(skill<0||skill>=SKILLS||!Double.isFinite(aps)||aps<=0)return false;
  double cost=cost(skill);
  if(actionLock>0||cooldowns[skill]>0||mana+1e-9<cost)return false;
  mana-=cost;double speed=(supports[skill]&2)!=0?(1.25+0.01*(supportLevel(skill,1)-1)):1;
  cooldowns[skill]=Math.max(1,(int)Math.ceil(new double[]{3,3.6,5,4.2,4.8}[skill]/speed/0.6-1e-9));
  actionLock=Math.max(1,(int)Math.ceil(1/aps/speed/0.6));active=true;return true;
 }
 public void tick(double max,double regen){mana=Math.min(max,mana+regen*0.6);for(int i=0;i<SKILLS;i++)if(cooldowns[i]>0)cooldowns[i]--;if(actionLock>0)actionLock--;}
 public int getActionLock(){return actionLock;}
 public double getMana(){return mana;}
 public int getCooldown(int skill){return cooldowns[skill];}
 public void refill(double maximum){mana=maximum;java.util.Arrays.fill(cooldowns,0);actionLock=0;lastPayload=null;}
 public void activate(){active=true;lastPayload=null;}
 public String update(double max){
  if(!active)return null;
  String payload=String.format(java.util.Locale.ROOT,"%.0f|%.0f|%.1f|%.1f|%d|%d",mana,max,cooldowns[0]*0.6,cooldowns[1]*0.6,supports[0],supports[1]);
  payload+="|"+gemLevels[0]+"|"+gemLevels[1]+"|"+gemLevels[2]+"|"+gemLevels[3]+"|"+gemLevels[4];
  payload+="|"+String.format(java.util.Locale.ROOT,"%.1f",cooldowns[2]*0.6)+"|"+supports[2]+"|"+gemLevels[5];
  payload+="|"+hotbar[0]+"|"+hotbar[1]+"|"+hotbar[2];
  for(int skill=3;skill<SKILLS;skill++)payload+="|"+String.format(java.util.Locale.ROOT,"%.1f",cooldowns[skill]*.6)+"|"+supports[skill]+"|"+skillLevel(skill);
  for(int support=0;support<3;support++)payload+="|"+available(support);
  for(int skill=0;skill<SKILLS;skill++)for(int support=0;support<3;support++)payload+="|"+supportLevel(skill,support);
  if(payload.equals(lastPayload))return null;lastPayload=payload;return payload;
 }
}

