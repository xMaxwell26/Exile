import com.koios.game.arpg.*;
import com.koios.game.model.*;
import com.koios.game.model.mob.*;
import com.koios.game.map.*;

/** Both original melee creatures: visible anticipation, conditional impact and safe cancellation. */
public final class ConstructWindupCheck {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static MapRuns.MapMob actor(CombatPresentationCheck.Fixture fixture, int layout) throws Exception {
        // Use the actual encounter ID selection with each layout's role1 creature.
        fixture.run.layout = layout;
        MapRuns.MapMob dummy = fixture.mob(fixture.area(6,4,0), false);
        dummy.setDead(true); // Keeps the test actor ordinary rather than the first-pack elite.
        return fixture.mob(fixture.area(6,4,1), false);
    }

    private static int begin(CombatPresentationCheck.Fixture fixture, MapRuns.MapMob mob) {
        fixture.stand(CombatPresentationCheck.offset(mob.getLocation(),2,0));
        double life = CombatPresentationCheck.pool(fixture.p);
        fixture.run.ticks = mob.nextAttackTick - 1;
        MapRuns.tick(fixture.p);
        EncounterProfiles.Profile profile = EncounterProfiles.get(mob.role,mob.getId());
        check(mob.strikeAt == fixture.run.ticks + profile.windupTicks
                && mob.getCurrentAnimation().getId() == profile.attackAnimation
                && CombatPresentationCheck.pool(fixture.p) == life,
                "Visible configured attack begins without immediate damage: " + mob.getId());
        return fixture.run.ticks;
    }

    public static void main(String[] args) throws Exception {
        PrototypeIntegrationCheck.loaded();
        for (int layout : new int[]{1,2}) {
            try (CombatPresentationCheck.Fixture fixture = new CombatPresentationCheck.Fixture()) {
                MapRuns.MapMob mob = actor(fixture,layout);
                EncounterProfiles.Profile profile = EncounterProfiles.get(mob.role,mob.getId());
                int start = begin(fixture,mob);
                mob.update();
                check(!mob.getFollowing().isFollowing(),"Committed creature cannot chase during windup");
                double before = CombatPresentationCheck.pool(fixture.p);
                MapRuns.tick(fixture.p);
                check(before - CombatPresentationCheck.pool(fixture.p) == profile.damage(1)
                        && mob.strikeAt == 0,"Exactly one conditional impact at the configured tick");
                for (int tick = fixture.run.ticks + 1; tick < start + profile.attackTicks; tick++) {
                    MapRuns.tick(fixture.p);
                    check(mob.strikeAt == 0,"No second swing before configured cadence");
                }
                MapRuns.tick(fixture.p);
                check(fixture.run.ticks == start + profile.attackTicks
                        && mob.strikeAt == fixture.run.ticks + profile.windupTicks,
                        "Start-to-start cadence includes windup");
            }

            // Every path that invalidates a pending attack must cancel it, not retarget by reused index.
            for (int cancel = 0; cancel < 12; cancel++) {
                try (CombatPresentationCheck.Fixture fixture = new CombatPresentationCheck.Fixture()) {
                    MapRuns.MapMob mob = actor(fixture,layout);
                    begin(fixture,mob);
                    double before = CombatPresentationCheck.pool(fixture.p);
                    Mob replacement = null;
                    switch (cancel) {
                        case 0: fixture.stand(CombatPresentationCheck.offset(mob.getLocation(),4,0)); break;
                        case 1: MapRegion.getRegion(mob.getX()+1,mob.getY()).setClip(mob.getX()+1,mob.getY(),0,0xffffff); break;
                        case 2: fixture.p.setDead(true); break;
                        case 3: MapRuns.home(fixture.p); PrototypeIntegrationCheck.idle(fixture.p); break;
                        case 4: mob.hurt(999999,fixture.p); break;
                        case 5:
                            Location at = mob.getLocation().copy();
                            MobManager.remove(mob);
                            replacement = new Mob(90,at) { };
                            MobManager.spawn(replacement);
                            break;
                        case 6: MapRuns.cleanup(fixture.p); break;
                        case 7: fixture.p.setPosition(fixture.p.getX(),fixture.p.getY(),1); PrototypeIntegrationCheck.idle(fixture.p); break;
                        case 8: fixture.p.setTeleporting(true); break;
                        case 9: mob.setDead(true); break;
                        case 10: fixture.p.setPosition(3094,3493,0); PrototypeIntegrationCheck.idle(fixture.p); break;
                        case 11: mob.hitpoints = 0; break;
                        default: throw new AssertionError("Unknown cancellation fixture");
                    }
                    MapRuns.tick(fixture.p);
                    check(CombatPresentationCheck.pool(fixture.p) >= before && mob.strikeAt == 0,
                            "Cancelled impact cannot damage player: layout"+layout+" / case"+cancel);
                    if (replacement != null) {
                        check(MobManager.mobs[replacement.getIndex()] == replacement,"Reused NPC index remains untouched");
                        MobManager.remove(replacement);
                    }
                }
            }
        }
        System.out.println("Passed " + checks + " authored melee windup/impact/cancellation checks across both creatures.");
    }
}
