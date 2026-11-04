package com.koios.game.arpg;
/** Presentation-only profiles verified against this cache's human skeleton/frame files.
 * Reuses original RuneScape rigs; does not change damage timing, cooldown or attack speed.
 */
public final class SkillAnimations {
 public static final class Profile {
  public final int sequence;public final String name,motion,source;public final boolean fullSpin;
  private Profile(int sequence,String name,String motion,String source){this.sequence=sequence;this.name=name;this.motion=motion;this.source=source;fullSpin=false;}
 }
 private static final Profile[] PROFILES={
  new Profile(1058,"Heavy Strike","Raised one-handed overhead cut","Dragon longsword special"),
  new Profile(1062,"Cleave","Cross-body double slash","Dragon dagger special"),
  new Profile(1203,"Whirlwind","Wide turning sweep; NOT a full360-degree spin","Dragon halberd special"),
  new Profile(3157,"Ground Slam","Two-handed lift then ground-directed impact","Cached human two-handed ground strike"),
  new Profile(1060,"Execute","Forward finishing thrust","Dragon mace special")
 };
 private static final Profile BLUNT_EXECUTE=new Profile(10505,"Execute","Raised hammer finish into a low crushing strike","Statius warhammer special");
 public static Profile profile(int skill,int weaponId){Profile profile=profile(skill);return skill==4&&(weaponId==22422||weaponId==22432)?BLUNT_EXECUTE:profile;}
 public static int sequence(int skill,int weaponId){return profile(skill,weaponId).sequence;}
 public static Profile profile(int skill){if(skill<0||skill>=PROFILES.length)throw new IllegalArgumentException("Unknown active skill");return PROFILES[skill];}
 /** All current custom melee families bind to the same verified human hand rig. */
 public static int sequence(int skill){return profile(skill).sequence;}
 private SkillAnimations(){}
}

