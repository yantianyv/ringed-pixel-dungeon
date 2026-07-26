package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BurningElement;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementBuff.Element;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElectricFieldSource;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementalSentry;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementalSentryPlus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrozenInvulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.GnosisEye;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class TravelerSpells {

    public static void castSkill(Wand wand, Hero hero, int target) {
        int lvl = wand.buffedLvl();

        if (wand instanceof WandOfMagicMissile) {
            castMagicMissileSkill((WandOfMagicMissile) wand, hero, target);
        } else if (wand instanceof WandOfLightning) {
            castLightningSkill((WandOfLightning) wand, hero, target);
        } else if (wand instanceof WandOfDisintegration) {
            castDisintegrationSkill((WandOfDisintegration) wand, hero, target);
        } else if (wand instanceof WandOfFireblast) {
            castFireblastSkill((WandOfFireblast) wand, hero, target);
        } else if (wand instanceof WandOfCorrosion) {
            castCorrosionSkill((WandOfCorrosion) wand, hero, target, lvl);
        } else if (wand instanceof WandOfBlastWave) {
            castBlastWaveSkill((WandOfBlastWave) wand, hero, target, lvl);
        } else if (wand instanceof WandOfLivingEarth) {
            castLivingEarthSkill(hero, lvl);
        } else if (wand instanceof WandOfFrost) {
            castFrostSkill(hero, lvl);
        } else if (wand instanceof WandOfPrismaticLight) {
            castPrismaticLightSkill((WandOfPrismaticLight) wand, hero, target);
        } else if (wand instanceof WandOfWarding) {
            castWardingSkill(hero, lvl);
        } else if (wand instanceof WandOfTransfusion) {
            castTransfusionSkill(hero, target, lvl);
        } else if (wand instanceof WandOfCorruption) {
            castCorruptionSkill((WandOfCorruption) wand, hero, target, lvl);
        } else if (wand instanceof WandOfRegrowth) {
            castRegrowthSkill(hero, target, lvl);
        }
    }

    public static void castBurst(Wand wand, Hero hero, int target) {
        int lvl = wand.buffedLvl();

        if (wand instanceof WandOfMagicMissile) {
            castMagicMissileBurst(wand, hero);
        } else if (wand instanceof WandOfLightning) {
            castLightningBurst(hero);
        } else if (wand instanceof WandOfDisintegration) {
            castDisintegrationBurst((WandOfDisintegration) wand, hero);
        } else if (wand instanceof WandOfFireblast) {
            castFireblastBurst(hero);
        } else if (wand instanceof WandOfCorrosion) {
            castCorrosionBurst(hero, target);
        } else if (wand instanceof WandOfBlastWave) {
            castBlastWaveBurst(hero, target);
        } else if (wand instanceof WandOfLivingEarth) {
            castLivingEarthBurst(hero, target);
        } else if (wand instanceof WandOfFrost) {
            castFrostBurst(hero);
        } else if (wand instanceof WandOfPrismaticLight) {
            castPrismaticLightBurst(hero);
        } else if (wand instanceof WandOfWarding) {
            castWardingBurst(hero, lvl);
        } else if (wand instanceof WandOfTransfusion) {
            castTransfusionBurst(hero, target);
        } else if (wand instanceof WandOfCorruption) {
            castCorruptionBurst((WandOfCorruption) wand, hero, target);
        } else if (wand instanceof WandOfRegrowth) {
            castRegrowthBurst(hero);
        }
    }

    private static void castMagicMissileSkill(final WandOfMagicMissile wand, final Hero hero, int target) {
        final Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT);
        final int collision = bolt.collisionPos;
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.MAGIC_MISSILE, hero.sprite, collision,
                new Callback() {
                    @Override
                    public void call() {
                        Char ch = Actor.findChar(collision);
                        if (ch != null) {
                            int dmg = wand.damageRoll();
                            ch.damage(dmg, wand);
                            for (int n : PathFinder.NEIGHBOURS9) {
                                Char splash = Actor.findChar(collision + n);
                                if (splash != null && splash != ch && splash.alignment == Char.Alignment.ENEMY) {
                                    splash.damage(Math.max(1, dmg / 2), wand);
                                }
                            }
                        }
                    }
                });
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    private static void castMagicMissileBurst(Wand wand, Hero hero) {
        int charges = wand.curCharges;
        wand.curCharges = 0;
        for (Wand w : allHeroWands(hero)) {
            if (w != wand) {
                w.gainCharge(charges, true);
            }
        }
        if (charges < 10) {
            GnosisEye eye = GnosisEye.getHeroGnosisEye(hero);
            if (eye != null) {
                eye.gainEnergy(Math.min(50f, (10 - charges) * 10f));
            }
        }
        hero.sprite.emitter().burst(MagicMissile.WhiteParticle.FACTORY, 20);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    private static ArrayList<Wand> allHeroWands(Hero hero) {
        ArrayList<Wand> wands = new ArrayList<>();
        for (Item i : hero.belongings) {
            if (i instanceof Wand) {
                wands.add((Wand) i);
            } else if (i instanceof com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag) {
                for (Item j : ((com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag) i).items) {
                    if (j instanceof Wand) {
                        wands.add((Wand) j);
                    }
                }
            }
        }
        return wands;
    }

    private static void castLightningSkill(final WandOfLightning wand, Hero hero, int target) {
        wand.setCurrent(hero);
        wand.dmgMulti = 1.5f;
        wand.hitAllies = true;
        wand.allyDmgMulti = 0.5f;
        wand.paralysisDuration = 0.2f;
        final Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT);
        wand.fx(bolt, new Callback() {
            @Override
            public void call() {
                wand.onZap(bolt);
                wand.resetTravelerModifiers();
            }
        });
    }

    private static void castLightningBurst(Hero hero) {
        ElectricFieldSource field = Buff.affect(hero, ElectricFieldSource.class);
        field.set(ElectricFieldSource.DURATION);
        hero.sprite.centerEmitter().burst(SparkParticle.FACTORY, 15);
    }

    private static void castDisintegrationSkill(WandOfDisintegration wand, Hero hero, int target) {
        Ballistica beam = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
        int range = Math.min(beam.dist, 6 + lvl(wand) * 2);
        hero.sprite.parent.add(new com.shatteredpixel.shatteredpixeldungeon.effects.Beam.DeathRay(
                hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.path.get(range))));
        for (int c : beam.subPath(1, range)) {
            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            Char ch = Actor.findChar(c);
            if (ch != null) {
                int dmg = Math.round(wand.damageRoll() * ElementBuff.apply(Element.PYRO, hero, ch, 1f));
                ch.damage(dmg, wand);
                Buff.prolong(ch, Vulnerable.class, 5f);
            }
            if (Dungeon.level.flamable[c]) {
                Dungeon.level.destroy(c);
                GameScene.updateMap(c);
            }
        }
    }

    private static void castDisintegrationBurst(WandOfDisintegration wand, Hero hero) {
        ArrayList<Char> enemies = visibleEnemies(hero);
        if (enemies.isEmpty()) return;
        int base = (int) Math.ceil(5f * wand.damageRoll() / enemies.size());
        for (Char ch : enemies) {
            hero.sprite.parent.add(new com.shatteredpixel.shatteredpixeldungeon.effects.Beam.DeathRay(
                    hero.sprite.center(), ch.sprite.center()));
            int dmg = Math.round(base * ElementBuff.apply(Element.PYRO, hero, ch, 1f));
            ch.damage(dmg, wand);
        }
    }

    private static void castFireblastSkill(WandOfFireblast wand, Hero hero, int target) {
        Ballistica bolt = new Ballistica(hero.pos, target,
                Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
        ConeAOE cone = new ConeAOE(bolt, 5, 50,
                Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
        for (int cell : cone.cells) {
            if (cell == hero.pos) continue;
            if (Dungeon.level.map[cell] == Terrain.DOOR) {
                Level.set(cell, Terrain.OPEN_DOOR);
                GameScene.updateMap(cell);
            }
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                int dmg = Math.round(wand.damageRoll() * ElementBuff.apply(Element.PYRO, hero, ch, 1f + 0.5f * lvl(wand)));
                ch.damage(dmg, wand);
            }
        }
        if (Dungeon.level.insideMap(target)) {
            GameScene.add(Blob.seed(target, 1, Fire.class));
            Char ch = Actor.findChar(target);
            if (ch != null) {
                Buff.affect(ch, Burning.class).reignite(ch);
            }
            revealArea(target, 2);
        }
        hero.sprite.zap(target);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
    }

    private static void castFireblastBurst(Hero hero) {
        for (Char ch : Actor.chars()) {
            if (ch.alignment == Char.Alignment.ENEMY) {
                int dmg = 0;
                Burning b = ch.buff(Burning.class);
                if (b != null) {
                    dmg += remainingBurnDamage(b);
                    b.detach();
                }
                BurningElement be = ch.buff(BurningElement.class);
                if (be != null) {
                    dmg += (int) (be.quantity() * avgBurnDamage());
                    be.detach();
                }
                if (dmg > 0) {
                    ch.damage(dmg, hero);
                }
            }
        }
        GnosisEye eye = GnosisEye.getHeroGnosisEye(hero);
        if (eye != null) eye.gainEnergy(80f);
    }

    private static void castCorrosionSkill(WandOfCorrosion wand, Hero hero, int target, int lvl) {
        Ballistica bolt = new Ballistica(hero.pos, target,
                Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
        int collision = bolt.collisionPos;
        GameScene.add(Blob.seed(collision, 50 + 10 * lvl, CorrosiveGas.class)
                .setStrength(2 + lvl, WandOfCorrosion.class));
        CellEmitter.get(collision).burst(Speck.factory(Speck.CORROSION), 10);
        for (int n : PathFinder.NEIGHBOURS9) {
            Char ch = Actor.findChar(collision + n);
            if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                ElementBuff.apply(Element.ANEMO, hero, ch, 0.5f);
            }
        }
        if (Actor.findChar(collision) == null) {
            Dungeon.level.pressCell(collision);
        }
        hero.sprite.zap(collision);
        Sample.INSTANCE.play(Assets.Sounds.GAS);
    }

    private static void castCorrosionBurst(Hero hero, int target) {
        hero.sprite.zap(target);
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.CORROSION, hero.sprite, target,
                new Callback() {
                    @Override
                    public void call() {
                    }
                });
        Sample.INSTANCE.play(Assets.Sounds.GAS);
        for (int n : PathFinder.NEIGHBOURS9) {
            int c = target + n;
            CellEmitter.get(c).burst(Speck.factory(Speck.CORROSION), 6);
            Char ch = Actor.findChar(c);
            if (ch == null || ch.alignment != Char.Alignment.ENEMY) continue;
            Buff.affect(ch, Ooze.class).set(Ooze.DURATION);
            ElementBuff.apply(Element.HYDRO, hero, ch, 1f);
        }
        GnosisEye eye = GnosisEye.getHeroGnosisEye(hero);
        if (eye != null) eye.gainEnergy(80f);
    }

    private static void castBlastWaveSkill(WandOfBlastWave wand, Hero hero, int target, int lvl) {
        if (target == hero.pos) {
            Buff.prolong(hero, Levitation.class, 10f);
            return;
        }
        fxMissile(hero, target, MagicMissile.FORCE);
        Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
        WandOfBlastWave.BlastWave.blast(bolt.collisionPos);
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null && ch != hero && ch.alignment != Char.Alignment.ALLY) {
            ch.damage(3 + lvl, wand);
            Buff.prolong(ch, Paralysis.class, 1f);
            Buff.prolong(ch, Levitation.class, 1f);
            ElementBuff.apply(Element.ANEMO, hero, ch, 1f);

            if ((ch.isAlive() || ch.flying || !Dungeon.level.pit[ch.pos])
                    && bolt.path.size() > bolt.dist + 1 && ch.pos == bolt.collisionPos) {
                Ballistica trajectory = new Ballistica(ch.pos, bolt.path.get(bolt.dist + 1),
                        Ballistica.MAGIC_BOLT);
                WandOfBlastWave.throwChar(ch, trajectory, lvl + 3, false, true, wand);
            }
        }
    }

    private static void castBlastWaveBurst(Hero hero, int target) {
        ArrayList<Char> all = new ArrayList<>();
        for (Char ch : Actor.chars()) {
            if (ch != hero && ch.alignment != Char.Alignment.ALLY && ch instanceof Mob) {
                all.add(ch);
            }
        }
        all.sort((a, b) -> Integer.compare(
                Dungeon.level.distance(hero.pos, a.pos), Dungeon.level.distance(hero.pos, b.pos)));

        ArrayList<Integer> slots = new ArrayList<>();
        for (int n : PathFinder.NEIGHBOURS9) {
            int c = target + n;
            if (!Dungeon.level.solid[c] && Actor.findChar(c) == null) {
                slots.add(c);
            }
        }

        int teleported = 0;
        for (Char ch : all) {
            if (teleported >= 9 || slots.isEmpty()) break;
            if (ch.properties().contains(Char.Property.BOSS)) continue;
            final int dest = Random.element(slots);
            Actor.add(new Pushing(ch, ch.pos, dest, () -> {
                ch.pos = dest;
                Dungeon.level.occupyCell(ch);
            }));
            ch.damage(1, hero);
            Buff.prolong(ch, Paralysis.class, 2f);
            Buff.prolong(ch, Levitation.class, 2f);
            ElementBuff.apply(Element.ANEMO, hero, ch, 3f);
            slots.remove((Integer) dest);
            teleported++;
        }
    }

    private static void castLivingEarthSkill(Hero hero, int lvl) {
        int shield = Math.round(hero.HT * (0.1f + 0.01f * lvl));
        Buff.affect(hero, Barrier.class).setShield(shield);
        hero.sprite.showStatusWithIcon(com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite.POSITIVE,
                Integer.toString(shield), com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText.SHIELDING);
    }

    private static void castLivingEarthBurst(Hero hero, int target) {
        int lost = hero.HT - hero.HP;
        if (lost <= 0) lost = 1;
        int total = 0;
        for (int n : PathFinder.NEIGHBOURS9) {
            int c = target + n;
            Char ch = Actor.findChar(c);
            if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                ch.damage(lost, hero);
                Buff.prolong(ch, Paralysis.class, 3f);
                ElementBuff.apply(Element.GEO, hero, ch, 2f);
                total += Math.min(lost, ch.HP > 0 ? lost : lost + ch.HP);
            }
        }
        if (total > 0) {
            Buff.affect(hero, Barrier.class).setShield(total);
        }
    }

    private static void castFrostSkill(Hero hero, int lvl) {
        if (lvl > 0) {
            GnosisEye eye = GnosisEye.getHeroGnosisEye(hero);
            if (eye != null) eye.gainEnergy(lvl);
        }
        Buff.affect(hero, FrozenInvulnerability.class).set(3f);
        hero.sprite.centerEmitter().burst(com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle.FACTORY, 10);
        Sample.INSTANCE.play(Assets.Sounds.SHATTER);
    }

    private static void castFrostBurst(Hero hero) {
        for (Char ch : Actor.chars()) {
            if (ch != hero && ch.alignment != Char.Alignment.ALLY) {
                ch.damage(3, hero);
                ElementBuff.apply(Element.CRYO, hero, ch, 1f);
                Buff.prolong(ch, Frost.class, 3f);
            }
        }
    }

    private static void castPrismaticLightSkill(WandOfPrismaticLight wand, Hero hero, int target) {
        Ballistica beam = new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT);
        wand.setCurrent(hero);
        wand.onZap(beam);
        Char ch = Actor.findChar(beam.collisionPos);
        if (ch != null && ch.isAlive()) {
            Element[] extra = {Element.ELECTRO, Element.PYRO, Element.DENDRO};
            ElementBuff.apply(Random.element(extra), hero, ch, 1f);
        }
        hero.sprite.parent.add(new com.shatteredpixel.shatteredpixeldungeon.effects.Beam.LightRay(
                hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
    }

    private static void castPrismaticLightBurst(Hero hero) {
        int length = Dungeon.level.length();
        int[] map = Dungeon.level.map;
        boolean[] mapped = Dungeon.level.mapped;
        boolean[] discoverable = Dungeon.level.discoverable;
        boolean noticed = false;
        for (int i = 0; i < length; i++) {
            int terr = map[i];
            if (discoverable[i]) {
                mapped[i] = true;
                if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
                    Dungeon.level.discover(i);
                    if (Dungeon.level.heroFOV[i]) {
                        GameScene.discoverTile(i, terr);
                        ScrollOfMagicMapping.discover(i);
                        noticed = true;
                    }
                }
            }
        }
        if (noticed) {
            Sample.INSTANCE.play(Assets.Sounds.SECRET);
        }
        GameScene.updateFog();
        Buff.prolong(hero, MindVision.class, 30f);
        Dungeon.observe();
        GameScene.updateFog();
    }

    private static void castWardingSkill(Hero hero, int lvl) {
        ElementalSentry sentry = Buff.affect(hero, ElementalSentry.class);
        sentry.set(10f + 2 * lvl);
        sentry.setPower(lvl);
    }

    private static void castWardingBurst(Hero hero, int lvl) {
        ElementalSentryPlus sentry = Buff.affect(hero, ElementalSentryPlus.class);
        sentry.set(30f);
        sentry.setPower(2 * lvl);
    }

    private static void castTransfusionSkill(Hero hero, int target, int lvl) {
        fxMissile(hero, target, MagicMissile.SHADOW);
        Char ch = Actor.findChar(target);
        if (ch != null && ch.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[target]) {
            Charm charm = Buff.affect(ch, Charm.class, 5f);
            charm.object = hero.id();
            Buff.affect(ch, Weakness.class, 5f);
            ch.damage(1 + lvl, hero);
            ElementBuff.apply(Element.HYDRO, hero, ch, 3f);
        }
    }

    private static void castTransfusionBurst(Hero hero, int target) {
        fxMissile(hero, target, MagicMissile.SHADOW);
        Char ch = Actor.findChar(target);
        if (ch != null && ch.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[target]) {
            Charm charm = Buff.affect(ch, Charm.class, 10f);
            charm.object = hero.id();
            Buff.prolong(ch, Paralysis.class, 3f);
            if (Random.Float() < 0.5f && !ch.isImmune(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo.class)) {
                Buff.prolong(ch, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo.class, 5f);
            } else {
                ch.attack(ch);
            }
        }
    }

    private static void castCorruptionSkill(WandOfCorruption wand, Hero hero, int target, int lvl) {
        fxMissile(hero, target, MagicMissile.SHADOW);
        Char ch = Actor.findChar(target);
        if (ch instanceof Mob && Dungeon.level.heroFOV[target]) {
            Mob enemy = (Mob) ch;
            if (attemptCorrupt(hero, enemy, lvl)) return;
            attemptCorrupt(hero, enemy, lvl);
        } else {
            Wraith w = Wraith.spawnAt(target);
            if (w != null) {
                w.adjustStats(lvl);
                AllyBuff.affectAndLoot(w, hero, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.class);
                w.sprite.emitter().burst(ShadowParticle.CURSE, 5);
            }
        }
    }

    private static void castCorruptionBurst(WandOfCorruption wand, Hero hero, int target) {
        fxMissile(hero, target, MagicMissile.SHADOW);
        Char ch = Actor.findChar(target);
        if (ch instanceof Mob && Dungeon.level.heroFOV[target]) {
            attemptCorrupt(hero, (Mob) ch, lvl(wand));
        }
    }

    private static void castRegrowthSkill(Hero hero, int target, int lvl) {
        Ballistica bolt = new Ballistica(hero.pos, target, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
        ConeAOE cone = new ConeAOE(bolt, 4, 30, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET);
        for (int cell : cone.cells) {
            if (!Dungeon.level.insideMap(cell)) continue;
            int terr = Dungeon.level.map[cell];
            if (terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO
                    || terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS) {
                if (terr != Terrain.HIGH_GRASS && terr != Terrain.FURROWED_GRASS) {
                    Level.set(cell, Terrain.GRASS);
                    GameScene.updateMap(cell);
                }
                Char ch = Actor.findChar(cell);
                if (ch != null && ch.alignment != Char.Alignment.ALLY) {
                    ch.damage(1 + lvl, hero);
                    ElementBuff.apply(Element.DENDRO, hero, ch, 2f);
                }
            }
        }
        if (Random.Float() < 0.5f) {
            Plant.Seed seed = (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED);
            Dungeon.level.plant(seed, target);
        }
        hero.sprite.zap(target);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    private static void castRegrowthBurst(Hero hero) {
        for (Char ch : Actor.chars()) {
            if (ch.alignment == Char.Alignment.ALLY) {
                if (ch == hero || Dungeon.level.heroFOV[ch.pos]) {
                    applyRandomPlantBuff(ch);
                }
            } else if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.heroFOV[ch.pos]) {
                Buff.affect(ch, Roots.class, 5f);
                applyRandomPlantDebuff(ch, hero);
            }
        }
    }

    private static void applyRandomPlantBuff(Char ch) {
        if (Random.Int(2) == 0) {
            Buff.affect(ch, Earthroot.Armor.class).level(ch.HT / 2);
        } else {
            Buff.affect(ch, Sungrass.Health.class).boost(ch.HT / 3);
        }
    }

    private static void applyRandomPlantDebuff(Char ch, Hero hero) {
        int r = Random.Int(3);
        switch (r) {
            case 0:
                Buff.affect(ch, Poison.class).set(2 + ch.HT / 8);
                break;
            case 1:
                Buff.affect(ch, Corrosion.class).set(4f, 2 + Dungeon.scalingDepth() / 5, Object.class);
                break;
            default:
                Buff.affect(ch, Burning.class).reignite(ch, 4f);
                break;
        }
    }

    private static boolean attemptCorrupt(Hero hero, Mob enemy, int lvl) {
        float corruptingPower = 3 + lvl / 3f;
        float enemyResist = 1 + Dungeon.scalingDepth() / 4f;
        enemyResist *= 1 + 4 * Math.pow(enemy.HP / (float) enemy.HT, 2);

        if (enemy.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.class) != null
                || enemy.buff(Doom.class) != null) {
            corruptingPower = enemyResist - 0.001f;
        }

        if (corruptingPower > enemyResist) {
            corruptEnemy(hero, enemy);
            return true;
        }
        return false;
    }

    private static void corruptEnemy(Hero hero, Mob enemy) {
        if (enemy.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.class) != null
                || enemy.buff(Doom.class) != null) {
            return;
        }
        if (!enemy.isImmune(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.class)) {
            com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.corruptionHeal(enemy);
            AllyBuff.affectAndLoot(enemy, hero, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption.class);
        } else {
            Buff.affect(enemy, Doom.class);
        }
    }

    private static ArrayList<Char> visibleEnemies(Hero hero) {
        ArrayList<Char> list = new ArrayList<>();
        for (Char ch : Actor.chars()) {
            if (ch != hero && ch.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[ch.pos]) {
                list.add(ch);
            }
        }
        return list;
    }

    private static void revealArea(int center, int radius) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int c = center + i + j * Dungeon.level.width();
                if (!Dungeon.level.insideMap(c)) continue;
                if (Dungeon.level.discoverable[c]) {
                    Dungeon.level.mapped[c] = true;
                    int terr = Dungeon.level.map[c];
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
                        Dungeon.level.discover(c);
                        if (Dungeon.level.heroFOV[c]) {
                            GameScene.discoverTile(c, terr);
                            ScrollOfMagicMapping.discover(c);
                        }
                    }
                }
            }
        }
        GameScene.updateFog();
    }

    private static int remainingBurnDamage(Burning b) {
        return (int) Math.ceil(b.left() * avgBurnDamage());
    }

    private static float avgBurnDamage() {
        return (1 + 3 + Dungeon.scalingDepth() / 4f) / 2f;
    }

    private static void fxMissile(Hero hero, int target, int type) {
        MagicMissile.boltFromChar(hero.sprite.parent, type, hero.sprite, target,
                new Callback() {
                    @Override
                    public void call() {
                    }
                });
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    private static int lvl(Wand wand) {
        return wand.buffedLvl();
    }
}
