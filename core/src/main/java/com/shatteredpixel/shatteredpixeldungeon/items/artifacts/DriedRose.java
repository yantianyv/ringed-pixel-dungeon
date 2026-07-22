/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAgility;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfDefender;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHeal;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfKungfu;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfNahida;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTimetraveler;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings.WeddingRing;

public class DriedRose extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_ROSE1;

        levelCap = 10;

        charge = 100;
        chargeCap = 100;

        defaultAction = AC_SUMMON;
    }

    private boolean talkedTo = false;
    private boolean firstSummon = false;

    private GhostHero ghost = null;
    private int ghostID = 0;

    private MeleeWeapon weapon = null;
    private Armor armor = null;

    private Ring ring1 = null;
    private Ring ring2 = null;

    // 幽妹可佩戴的戒指白名单
    public static final HashSet<Class<? extends Ring>> GHOST_RINGS = new HashSet<>(Arrays.asList(
            RingOfAgility.class,
            RingOfDefender.class,
            RingOfHeal.class,
            RingOfKungfu.class,
            RingOfNahida.class,
            RingOfTimetraveler.class,
            WeddingRing.class
    ));

    public int droppedPetals = 0;

    public static final String AC_SUMMON = "SUMMON";
    public static final String AC_DIRECT = "DIRECT";
    public static final String AC_OUTFIT = "OUTFIT";
    public static final String AC_RELEASE = "RELEASE";

    // 幽妹佩戴婚戒且持有祝福十字架时可超度
    private boolean canRelease(Hero hero) {
        if (cursed || !(ring1 instanceof WeddingRing || ring2 instanceof WeddingRing)) {
            return false;
        }
        for (Ankh ankh : hero.belongings.getAllItems(Ankh.class)) {
            if (ankh.isBlessed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!Ghost.Quest.completed()) {
            return actions;
        }
        if (isEquipped(hero)
                && charge == chargeCap
                && !cursed
                && hero.buff(MagicImmune.class) == null
                && ghostID == 0) {
            actions.add(AC_SUMMON);
        }
        if (ghostID != 0) {
            actions.add(AC_DIRECT);
        }
        if (isIdentified() && !cursed) {
            actions.add(AC_OUTFIT);
        }
        if (Ghost.Quest.completed() && canRelease(hero)) {
            actions.add(AC_RELEASE);
        }

        return actions;
    }

    @Override
    public String defaultAction() {
        if (ghost != null) {
            return AC_DIRECT;
        } else {
            return AC_SUMMON;
        }
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON)) {

            if (hero.buff(MagicImmune.class) != null) {
                return;
            }

            if (!Ghost.Quest.completed()) {
                GameScene.show(new WndUseItem(null, this));
            } else if (ghost != null) {
                GLog.i(Messages.get(this, "spawned"));
            } else if (!isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            } else if (charge != chargeCap) {
                GLog.i(Messages.get(this, "no_charge"));
            } else if (cursed) {
                GLog.i(Messages.get(this, "cursed"));
            } else {
                ArrayList<Integer> spawnPoints = new ArrayList<>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    ghost = new GhostHero(this);
                    ghostID = ghost.id();
                    ghost.pos = Random.element(spawnPoints);

                    GameScene.add(ghost, 1f);
                    Dungeon.level.occupyCell(ghost);

                    CellEmitter.get(ghost.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
                    CellEmitter.get(ghost.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);

                    if (!firstSummon) {
                        ghost.yell(Messages.get(GhostHero.class, "hello", Messages.titleCase(Dungeon.hero.name())));
                        Sample.INSTANCE.play(Assets.Sounds.GHOST);
                        firstSummon = true;

                    } else {
                        if (BossHealthBar.isAssigned()) {
                            ghost.sayBoss();
                        } else {
                            ghost.sayAppeared();
                        }
                    }

                    Invisibility.dispel(hero);
                    Talent.onArtifactUsed(hero);
                    charge = 0;
                    partialCharge = 0;
                    updateQuickslot();

                } else {
                    GLog.i(Messages.get(this, "no_space"));
                }
            }

        } else if (action.equals(AC_DIRECT)) {
            if (ghost == null && ghostID != 0) {
                findGhost();
            }
            if (ghost != null && ghost != Stasis.getStasisAlly()) {
                GameScene.selectCell(ghostDirector);
            }

        } else if (action.equals(AC_OUTFIT)) {
            GameScene.show(new WndGhostHero(this));
        } else if (action.equals(AC_RELEASE)) {
            GameScene.show(new WndOptions(
                    Messages.get(DriedRose.class, "release_confirm_title"),
                    Messages.get(DriedRose.class, "release_confirm_message"),
                    Messages.get(DriedRose.class, "release_confirm_yes"),
                    Messages.get(DriedRose.class, "release_confirm_no")) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        release(hero);
                    }
                }
            });
        }
    }

    // 超度仪式：消耗祝福十字架与婚戒，玫瑰升华为永绽玫瑰
    private void release(Hero hero) {
        Ankh ankh = null;
        for (Ankh a : hero.belongings.getAllItems(Ankh.class)) {
            if (a.isBlessed()) {
                ankh = a;
                break;
            }
        }
        if (ankh == null || !(ring1 instanceof WeddingRing || ring2 instanceof WeddingRing)) {
            return;
        }
        ankh.detach(hero.belongings.backpack);

        int weddingLevel = 0;
        if (weapon != null) {
            if (!weapon.doPickUp(hero)) Dungeon.level.drop(weapon, hero.pos);
            weapon = null;
        }
        if (armor != null) {
            if (!armor.doPickUp(hero)) Dungeon.level.drop(armor, hero.pos);
            armor = null;
        }
        if (ring1 != null) {
            if (ring1 instanceof WeddingRing) {
                weddingLevel = Math.max(weddingLevel, ring1.level());
                ring1.deactivate();
            } else {
                ring1.deactivate();
                if (!ring1.doPickUp(hero)) Dungeon.level.drop(ring1, hero.pos);
            }
            ring1 = null;
        }
        if (ring2 != null) {
            if (ring2 instanceof WeddingRing) {
                weddingLevel = Math.max(weddingLevel, ring2.level());
                ring2.deactivate();
            } else {
                ring2.deactivate();
                if (!ring2.doPickUp(hero)) Dungeon.level.drop(ring2, hero.pos);
            }
            ring2 = null;
        }

        if (ghost != null) {
            GhostHero g = ghost;
            g.yell(Messages.get(GhostHero.class, "release_farewell_" + Random.IntRange(1, 2)));
            CellEmitter.get(g.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
            CellEmitter.get(g.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
            g.destroy();
            if (g.sprite != null) {
                g.sprite.die();
            }
        }

        BloomingRose bloom = new BloomingRose();
        bloom.level(level() + weddingLevel);
        bloom.identify();

        if (isEquipped(hero)) {
            // 直接装备到原槽位，不消耗回合
            doUnequip(hero, false, false);
            if (!bloom.doEquip(hero) && !bloom.doPickUp(hero)) {
                Dungeon.level.drop(bloom, hero.pos);
            }
        } else {
            detach(hero.belongings.backpack);
            if (!bloom.doPickUp(hero)) {
                Dungeon.level.drop(bloom, hero.pos);
            }
        }

        GLog.p(Messages.get(this, "released"));
        Sample.INSTANCE.play(Assets.Sounds.GHOST);
        GameScene.flash(0x80FFFF40);
        hero.spend(1f);
        hero.busy();
    }

    private void findGhost() {
        Actor a = Actor.findById(ghostID);
        if (a != null) {
            ghost = (GhostHero) a;
        } else {
            if (Stasis.getStasisAlly() instanceof GhostHero) {
                ghost = (GhostHero) Stasis.getStasisAlly();
                ghostID = ghost.id();
            } else {
                ghostID = 0;
            }
        }
    }

    public int ghostStrength() {
        return 13 + level() / 2 + WeddingRing.extraStr(Dungeon.hero);
    }

    // 突破+10后解锁第二个戒指槽
    public boolean secondRingSlotUnlocked() {
        return level() > levelCap;
    }

    // 把戒指buff惰性激活到幽妹身上，等级超过玫瑰等级时按玫瑰等级封顶
    public void activateGhostRings(Char ghost) {
        if (ring1 != null) {
            ring1.socketLevelCap(level());
            if (ghost != null) ring1.ensureActivated(ghost);
        }
        if (ring2 != null) {
            ring2.socketLevelCap(level());
            if (secondRingSlotUnlocked()) {
                if (ghost != null) ring2.ensureActivated(ghost);
            } else {
                ring2.deactivate();
            }
        }
    }

    @Override
    public String desc() {
        if (!Ghost.Quest.completed()
                && (ShatteredPixelDungeon.scene() instanceof GameScene || ShatteredPixelDungeon.scene() instanceof AlchemyScene)) {
            return Messages.get(this, "desc_no_quest");
        }

        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (!cursed) {

                if (level() < levelCap) {
                    desc += "\n\n" + Messages.get(this, "desc_hint");
                }

            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        if (weapon != null || armor != null) {
            desc += "\n";

            if (weapon != null) {
                desc += "\n" + Messages.get(this, "desc_weapon", Messages.titleCase(weapon.title()));
            }

            if (armor != null) {
                desc += "\n" + Messages.get(this, "desc_armor", Messages.titleCase(armor.title()));
            }

            desc += "\n" + Messages.get(this, "desc_strength", ghostStrength());

        }

        if (ring1 != null) {
            desc += "\n" + Messages.get(this, "desc_ring", ghostRingTitle(ring1));
        }
        if (ring2 != null) {
            desc += "\n" + Messages.get(this, "desc_ring", ghostRingTitle(ring2));
        }

        return desc;
    }

    // 幽妹戒指的显示名，等级超过玫瑰时按玫瑰等级封顶显示
    private String ghostRingTitle(Ring ring) {
        if (ring.levelKnown && ring.level() > level()) {
            return Messages.format(TXT_TO_STRING_LVL, Messages.titleCase(ring.name()), level());
        }
        return Messages.titleCase(ring.title());
    }

    // 幽妹戒指的专用介绍，数值按玫瑰等级封顶
    public String ghostRingStats(Ring ring) {
        int bonus = Math.min(ring.soloBuffedBonus(), level() + 1);
        if (ring instanceof RingOfAgility) {
            return Messages.get(ring, "ghost_stats",
                    Messages.decimalFormat("#.##", 100f * (1f - Math.pow(0.95f, bonus))));
        } else if (ring instanceof RingOfDefender) {
            return Messages.get(ring, "ghost_stats",
                    Messages.decimalFormat("#.##", 100f * (1f - Math.pow(0.85f, bonus))),
                    Messages.decimalFormat("#.##", bonus * 1.5f));
        } else if (ring instanceof RingOfHeal) {
            return Messages.get(ring, "ghost_stats",
                    Messages.decimalFormat("#.##", 100f * (Math.pow(1.1f, bonus) - 1f)));
        } else if (ring instanceof RingOfKungfu) {
            return Messages.get(ring, "ghost_stats", bonus,
                    Messages.decimalFormat("#.##", 100f * (Math.pow(1.02f, bonus) - 1f)));
        } else if (ring instanceof RingOfNahida) {
            return Messages.get(ring, "ghost_stats",
                    Messages.decimalFormat("#.##", 100f * (Math.pow(1.1f, bonus) - 1f)));
        } else if (ring instanceof RingOfTimetraveler) {
            return Messages.get(ring, "ghost_stats",
                    Messages.decimalFormat("#.##", 100f * (Math.pow(1f / 0.9f, bonus) - 1f)));
        } else if (ring instanceof WeddingRing) {
            return Messages.get(ring, "ghost_stats", bonus);
        }
        return "";
    }

    // 配装窗口展示的幽妹戒指介绍，未解锁的第二槽不生效故不显示
    public String ghostRingsDesc() {
        String desc = "";
        if (ring1 != null) {
            desc += ghostRingTitle(ring1) + "：" + ghostRingStats(ring1);
        }
        if (ring2 != null && secondRingSlotUnlocked()) {
            if (!desc.isEmpty()) desc += "\n";
            desc += ghostRingTitle(ring2) + "：" + ghostRingStats(ring2);
        }
        return desc;
    }

    @Override
    public int value() {
        if (weapon != null) {
            return -1;
        }
        if (armor != null) {
            return -1;
        }
        if (ring1 != null || ring2 != null) {
            return -1;
        }
        return super.value();
    }

    @Override
    public String status() {
        if (ghost == null && ghostID != 0) {
            try {
                findGhost();
            } catch (ClassCastException e) {
                ShatteredPixelDungeon.reportException(e);
                ghostID = 0;
            }
        }
        if (ghost == null) {
            return super.status();
        } else {
            return ((ghost.HP * 100) / ghost.HT) + "%";
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new roseRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (cursed || target.buff(MagicImmune.class) != null) {
            return;
        }

        if (ghost == null) {
            if (charge < chargeCap) {
                partialCharge += 4 * amount;
                while (partialCharge >= 1f) {
                    charge++;
                    partialCharge--;
                }
                if (charge >= chargeCap) {
                    charge = chargeCap;
                    partialCharge = 0;
                    GLog.p(Messages.get(DriedRose.class, "charged"));
                }
                updateQuickslot();
            }
        } else if (ghost.HP < ghost.HT) {
            int heal = Math.round((1 + level() / 3f) * amount);
            ghost.heal(heal, this);
            if (ghost.sprite != null) {
                ghost.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
            }
            updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        if (level() >= 9) {
            image = ItemSpriteSheet.ARTIFACT_ROSE3;
        } else if (level() >= 4) {
            image = ItemSpriteSheet.ARTIFACT_ROSE2;
        }

        //For upgrade transferring via well of transmutation
        droppedPetals = Math.max(Math.min(level(), 10), droppedPetals);

        if (ghost != null) {
            ghost.updateRose();
        }

        return super.upgrade();
    }

    public Weapon ghostWeapon() {
        return weapon;
    }

    public Armor ghostArmor() {
        return armor;
    }

    public Ring ghostRing1() {
        return ring1;
    }

    public Ring ghostRing2() {
        return ring2;
    }

    private static final String TALKEDTO = "talkedto";
    private static final String FIRSTSUMMON = "firstsummon";
    private static final String GHOSTID = "ghostID";
    private static final String PETALS = "petals";

    private static final String WEAPON = "weapon";
    private static final String ARMOR = "armor";
    private static final String RING1 = "ring1";
    private static final String RING2 = "ring2";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(TALKEDTO, talkedTo);
        bundle.put(FIRSTSUMMON, firstSummon);
        bundle.put(GHOSTID, ghostID);
        bundle.put(PETALS, droppedPetals);

        if (weapon != null) {
            bundle.put(WEAPON, weapon);
        }
        if (armor != null) {
            bundle.put(ARMOR, armor);
        }
        if (ring1 != null) {
            bundle.put(RING1, ring1);
        }
        if (ring2 != null) {
            bundle.put(RING2, ring2);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        talkedTo = bundle.getBoolean(TALKEDTO);
        firstSummon = bundle.getBoolean(FIRSTSUMMON);
        ghostID = bundle.getInt(GHOSTID);
        droppedPetals = bundle.getInt(PETALS);

        if (bundle.contains(WEAPON)) {
            weapon = (MeleeWeapon) bundle.get(WEAPON);
        }
        if (bundle.contains(ARMOR)) {
            armor = (Armor) bundle.get(ARMOR);
        }
        if (bundle.contains(RING1)) {
            ring1 = (Ring) bundle.get(RING1);
        }
        if (bundle.contains(RING2)) {
            ring2 = (Ring) bundle.get(RING2);
        }
    }

    public class roseRecharge extends ArtifactBuff {

        @Override
        public boolean act() {

            spend(TICK);

            if (ghost == null && ghostID != 0) {
                findGhost();
            }

            if (ghost != null && !ghost.isAlive()) {
                ghost = null;
            }

            //rose does not charge while ghost hero is alive
            if (ghost != null && !cursed && target.buff(MagicImmune.class) == null) {

                //heals to full over 500 turns
                if (ghost.HP < ghost.HT && Regeneration.regenOn()) {
                    partialCharge += (ghost.HT / 500f) * RingOfEnergy.artifactChargeMultiplier(target);
                    updateQuickslot();

                    while (partialCharge > 1) {
                        ghost.HP++;
                        partialCharge--;
                    }
                } else {
                    partialCharge = 0;
                }

                return true;
            }

            if (charge < chargeCap
                    && !cursed
                    && target.buff(MagicImmune.class) == null
                    && Regeneration.regenOn()) {
                //500 turns to a full charge
                partialCharge += (1 / 5f * RingOfEnergy.artifactChargeMultiplier(target));
                while (partialCharge > 1) {
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap) {
                        partialCharge = 0f;
                        GLog.p(Messages.get(DriedRose.class, "charged"));
                    }
                }
            } else if (cursed && Random.Int(100) == 0) {

                ArrayList<Integer> spawnPoints = new ArrayList<>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = target.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    Wraith.spawnAt(Random.element(spawnPoints), Wraith.class);
                    Sample.INSTANCE.play(Assets.Sounds.CURSED);
                }

            }

            updateQuickslot();

            return true;
        }
    }

    public CellSelector.Listener ghostDirector = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) {
                return;
            }

            Sample.INSTANCE.play(Assets.Sounds.GHOST);

            ghost.directTocell(cell);

        }

        @Override
        public String prompt() {
            return "\"" + Messages.get(GhostHero.class, "direct_prompt") + "\"";
        }
    };

    public static class Petal extends Item {

        {
            stackable = true;
            dropsDownHeap = true;

            image = ItemSpriteSheet.PETAL;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            Catalog.setSeen(getClass());
            Statistics.itemTypesDiscovered.add(getClass());
            DriedRose rose = hero.belongings.getItem(DriedRose.class);

            if (rose == null) {
                GLog.w(Messages.get(this, "no_rose"));
                return false;
            }
            if (rose.level() >= rose.levelCap) {
                GLog.i(Messages.get(this, "no_room"));
                hero.spendAndNext(TIME_TO_PICK_UP);
                return true;
            } else {

                rose.upgrade();
                Catalog.countUse(rose.getClass());
                if (rose.level() == rose.levelCap) {
                    GLog.p(Messages.get(this, "maxlevel"));
                } else {
                    GLog.i(Messages.get(this, "levelup"));
                }

                Sample.INSTANCE.play(Assets.Sounds.DEWDROP);
                GameScene.pickUp(this, pos);
                hero.spendAndNext(TIME_TO_PICK_UP);
                return true;

            }
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

    }

    public static class GhostHero extends DirectableAlly {

        {
            spriteClass = GhostSprite.class;

            flying = true;

            state = HUNTING;

            properties.add(Property.UNDEAD);
            properties.add(Property.INORGANIC);
        }

        private DriedRose rose = null;

        // 婚戒免死次数，满血或重新召唤时重置
        private boolean weddingReviveReady = true;

        public GhostHero() {
            super();
        }

        public GhostHero(DriedRose rose) {
            super();
            this.rose = rose;
            updateRose();
            HP = HT;
        }

        @Override
        public void defendPos(int cell) {
            yell(Messages.get(this, "directed_position_" + Random.IntRange(1, 5)));
            super.defendPos(cell);
        }

        @Override
        public void followHero() {
            yell(Messages.get(this, "directed_follow_" + Random.IntRange(1, 5)));
            super.followHero();
        }

        @Override
        public void targetChar(Char ch) {
            yell(Messages.get(this, "directed_attack_" + Random.IntRange(1, 5)));
            super.targetChar(ch);
        }

		private void updateRose(){
			if (rose == null) {
				rose = Dungeon.hero.belongings.getItem(DriedRose.class);
			}

			//same dodge as the hero
			defenseSkill = (Dungeon.hero.lvl+4);
			if (rose == null) return;
			rose.activateGhostRings(this);
			HT = Math.round(20 + 8*rose.level()+WeddingRing.extraHT(Dungeon.hero) + RingOfDefender.HTAddition(this));
			HT = Math.round(HT * RingOfKungfu.HTMultiplier(this));
			if (HP > HT) HP = HT;
			if (HP >= HT) weddingReviveReady = true;
		}

		public Weapon weapon(){
			if (rose != null)   return rose.weapon;
			else                return null;
		}

		public Armor armor(){
			if (rose != null)   return rose.armor;
			else                return null;
		}

        @Override
        protected boolean act() {
            updateRose();
            if (rose == null
                    || !rose.isEquipped(Dungeon.hero)
                    || Dungeon.hero.buff(MagicImmune.class) != null) {
                damage(1, new NoRoseDamage());
            }

            if (!isAlive()) {
                return true;
            }
            return super.act();
        }

        public static class NoRoseDamage {
        }

		@Override
		public int attackSkill(Char target) {

			float agility = RingOfAgility.agilityChance(this);
			if (Random.Float(1) < agility) {
				return INFINITE_ACCURACY;
			} else if (Random.Float(1) < -agility) {
				return 0;
			}

			//same accuracy as the hero.
			int acc = Dungeon.hero.lvl + 9;
			
			if (weapon() != null){
				acc *= weapon().accuracyFactor( this, target );
			}
			
			return acc;
		}
		
		@Override
		public float attackDelay() {
			float delay = super.attackDelay();
			if (weapon() != null){
				delay *= weapon().delayFactor(this);
			}
			return delay;
		}
		
		@Override
		protected boolean canAttack(Char enemy) {
			return super.canAttack(enemy) || (weapon() != null && weapon().canReach(this, enemy.pos));
		}
		
		@Override
		public int damageRoll() {
			int dmg = 0;
			if (weapon() != null){
				dmg += weapon().damageRoll(this);
			} else {
				dmg += Random.NormalIntRange(0, 5);
			}

			dmg += RingOfKungfu.armedDamageBonus(this);

			return dmg;
		}
		
		@Override
		public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);

            if (weapon() != null) {
                damage = weapon().proc(this, enemy, damage);
                if (!enemy.isAlive() && enemy == Dungeon.hero) {
                    Dungeon.fail(this);
                    GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
                }
            }
            if (buff(WeddingRing.Weddingring.class) != null) {
                int heal = (int) (damage * WeddingRing.ghostPower(this));
                heal = HP + heal > HT ? HT - HP : heal;
                heal(heal, this);
            }

            return damage;
        }

        @Override
        public int defenseProc(Char enemy, int damage) {
            if (armor() != null) {
                damage = armor().proc(enemy, this, damage);
            }
            damage = (int) Math.ceil(damage * RingOfDefender.damageMultiplier(this));
            return super.defenseProc(enemy, damage);
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            if (src instanceof Mob&&src!=this) {
                int dmg_rec = (int) (dmg * (WeddingRing.ghostPower(Dungeon.hero)));
                if (dmg_rec > 0) {
                    ((Mob) src).damage(dmg_rec, this);
                }
            }

            //for the rose status indicator
            Item.updateQuickslot();
        }

        @Override
        public float speed() {
            float speed = super.speed();

            //moves 2 tiles at a time when returning to the hero
            if (state == WANDERING
                    && defendingPos == -1
                    && Dungeon.level.distance(pos, Dungeon.hero.pos) > 1) {
                speed *= 2;
            }

            return speed;
        }

        @Override
        public void spend(float time) {
            time = time * RingOfTimetraveler.timeMultiplier(this);
            super.spend(time);
        }

        @Override
        public int defenseSkill(Char enemy) {
            float agility = RingOfAgility.agilityChance(this);
            if (Random.Float(1) < agility) {
                return INFINITE_EVASION;
            } else if (Random.Float(1) < -agility) {
                return 0;
            }

            int defense = super.defenseSkill(enemy);

			if (defense != 0 && armor() != null ){
				defense = Math.round(armor().evasionFactor( this, defense ));
			}
			
			return defense;
		}
		
		@Override
		public int drRoll() {
			int dr = super.drRoll();
			if (armor() != null){
				dr += Random.NormalIntRange( armor().DRMin(), armor().DRMax());
			}
			if (weapon() != null){
				dr += Random.NormalIntRange( 0, weapon().defenseFactor( this ));
			}
			return dr;
		}

		@Override
		public int glyphLevel(Class<? extends Armor.Glyph> cls) {
			if (armor() != null && armor().hasGlyph(cls, this)){
				return Math.max(super.glyphLevel(cls), armor().buffedLvl());
			} else {
				return super.glyphLevel(cls);
			}
		}

        @Override
        public boolean interact(Char c) {
            updateRose();
            if (c == Dungeon.hero && rose != null && !rose.talkedTo) {
                rose.talkedTo = true;
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        GameScene.show(new WndQuest(GhostHero.this, Messages.get(GhostHero.this, "introduce")));
                    }
                });
                return true;
            } else {
                return super.interact(c);
            }
        }

        @Override
        public void die(Object cause) {
            int wedding = WeddingRing.getBuffedBonus(this, WeddingRing.Weddingring.class);
            if (weddingReviveReady && wedding > 0 && !(cause instanceof NoRoseDamage)) {
                weddingReviveReady = false;
                HP = 1;
                Buff.prolong(this, Invulnerability.class, wedding);
                SpellSprite.show(this, SpellSprite.ANKH);
                GameScene.flash(0x80FFFF40);
                Sample.INSTANCE.play(Assets.Sounds.GHOST);
                yell(Messages.get(this, "wedding_revive_" + Random.IntRange(1, 2)));
                return;
            }
            sayDefeated();
            super.die(cause);
        }

        @Override
        public void destroy() {
            updateRose();
            //TODO stasis?
            if (rose != null) {
                rose.ghost = null;
                rose.charge = 0;
                rose.partialCharge = 0;
                rose.ghostID = -1;
                if (rose.ring1 != null) rose.ring1.deactivate();
                if (rose.ring2 != null) rose.ring2.deactivate();
            }
            super.destroy();
        }

        public void sayAppeared() {
            if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
                yell(Messages.get(this, "dialogue_ascension_" + Random.IntRange(1, 6)));

            } else {

                int depth = (Dungeon.depth - 1) / 5;

                //only some lines are said on the first floor of a depth
                int variant = Dungeon.depth % 5 == 1 ? Random.IntRange(1, 3) : Random.IntRange(1, 6);

                switch (depth) {
                    case 0:
                        yell(Messages.get(this, "dialogue_sewers_" + variant));
                        break;
                    case 1:
                        yell(Messages.get(this, "dialogue_prison_" + variant));
                        break;
                    case 2:
                        yell(Messages.get(this, "dialogue_caves_" + variant));
                        break;
                    case 3:
                        yell(Messages.get(this, "dialogue_city_" + variant));
                        break;
                    case 4:
                    default:
                        yell(Messages.get(this, "dialogue_halls_" + variant));
                        break;
                }
            }
            if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                Sample.INSTANCE.play(Assets.Sounds.GHOST);
            }
        }

        public void sayBoss() {
            int depth = (Dungeon.depth - 1) / 5;

            switch (depth) {
                case 0:
                    yell(Messages.get(this, "seen_goo_" + Random.IntRange(1, 3)));
                    break;
                case 1:
                    yell(Messages.get(this, "seen_tengu_" + Random.IntRange(1, 3)));
                    break;
                case 2:
                    yell(Messages.get(this, "seen_dm300_" + Random.IntRange(1, 3)));
                    break;
                case 3:
                    yell(Messages.get(this, "seen_king_" + Random.IntRange(1, 3)));
                    break;
                case 4:
                default:
                    yell(Messages.get(this, "seen_yog_" + Random.IntRange(1, 3)));
                    break;
            }
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
        }

        public void sayDefeated() {
            if (BossHealthBar.isAssigned()) {
                yell(Messages.get(this, "defeated_by_boss_" + Random.IntRange(1, 3)));
            } else {
                yell(Messages.get(this, "defeated_by_enemy_" + Random.IntRange(1, 3)));
            }
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
        }

        public void sayHeroKilled() {
            yell(Messages.get(this, "player_killed_" + Random.IntRange(1, 3)));
            GLog.newLine();
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
        }

        public void sayAnhk() {
            yell(Messages.get(this, "blessed_ankh_" + Random.IntRange(1, 3)));
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
        }

        {
            immunities.add(CorrosiveGas.class);
            immunities.add(Burning.class);
            immunities.add(ScrollOfRetribution.class);
            immunities.add(ScrollOfPsionicBlast.class);
            immunities.add(AllyBuff.class);
        }

    }

    private static class WndGhostHero extends Window {

        private static final int BTN_SIZE = 32;
        private static final float GAP = 2;
        private static final float BTN_GAP = 12;
        // 横屏单行4按钮，竖屏2×2布局避免窗口过宽
        private static final int WIDTH_LAND = 4 * BTN_SIZE + 3 * (int) BTN_GAP + 8;
        private static final int WIDTH_PORT = 116;

        private ItemButton btnWeapon;
        private ItemButton btnArmor;
        private ItemButton btnRing1;
        private ItemButton btnRing2;
        private RenderedTextBlock ringInfo;
        private boolean landscape;
        private int width;

        WndGhostHero(final DriedRose rose) {

            landscape = PixelScene.landscape();
            width = landscape ? WIDTH_LAND : WIDTH_PORT;

            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(rose));
            titlebar.label(Messages.get(this, "title"));
            titlebar.setRect(0, 0, width, 0);
            add(titlebar);

            RenderedTextBlock message
                    = PixelScene.renderTextBlock(Messages.get(this, "desc", rose.ghostStrength()), 6);
            message.maxWidth(width);
            message.setPos(0, titlebar.bottom() + GAP);
            add(message);

            btnWeapon = new ItemButton() {
                @Override
                protected void onClick() {
                    if (rose.weapon != null) {
                        item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
                        if (!rose.weapon.doPickUp(Dungeon.hero)) {
                            Dungeon.level.drop(rose.weapon, Dungeon.hero.pos);
                        }
                        rose.weapon = null;
                    } else {
                        GameScene.selectItem(new WndBag.ItemSelector() {

                            @Override
                            public String textPrompt() {
                                return Messages.get(WndGhostHero.class, "weapon_prompt");
                            }

                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Belongings.Backpack.class;
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof MeleeWeapon;
                            }

							@Override
							public void onSelect(Item item) {
								if (!(item instanceof MeleeWeapon)) {
									//do nothing, should only happen when window is cancelled
								} else if (item.unique) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
									hide();
								} else if (item.cursed || !item.cursedKnown) {
									GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
									hide();
								}  else if (!item.levelKnown && ((MeleeWeapon)item).STRReq(0) > rose.ghostStrength()){
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength_unknown"));
									hide();
								} else if (((MeleeWeapon)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((MeleeWeapon) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.weapon = (MeleeWeapon) item;
									item(rose.weapon);
								}
								
							}
						});
					}
				}

                @Override
                protected boolean onLongClick() {
                    if (item() != null && item().name() != null) {
                        GameScene.show(new WndInfoItem(item()));
                        return true;
                    }
                    return false;
                }
            };
            float hGap = landscape ? BTN_GAP : 8;
            float vGap = landscape ? BTN_GAP : 8;
            float btnRowWidth = (landscape ? 4 : 2) * BTN_SIZE + (landscape ? 3 : 1) * hGap;
            float btnY = message.top() + message.height() + 8;
            btnWeapon.setRect((width - btnRowWidth) / 2, btnY, BTN_SIZE, BTN_SIZE);
            if (rose.weapon != null) {
                btnWeapon.item(rose.weapon);
            } else {
                btnWeapon.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
            }
            add(btnWeapon);

            btnArmor = new ItemButton() {
                @Override
                protected void onClick() {
                    if (rose.armor != null) {
                        item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
                        if (!rose.armor.doPickUp(Dungeon.hero)) {
                            Dungeon.level.drop(rose.armor, Dungeon.hero.pos);
                        }
                        rose.armor = null;
                    } else {
                        GameScene.selectItem(new WndBag.ItemSelector() {

                            @Override
                            public String textPrompt() {
                                return Messages.get(WndGhostHero.class, "armor_prompt");
                            }

                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Belongings.Backpack.class;
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Armor;
                            }

							@Override
							public void onSelect(Item item) {
								if (!(item instanceof Armor)) {
									//do nothing, should only happen when window is cancelled
								} else if (item.unique || ((Armor) item).checkSeal() != null) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_unique"));
									hide();
								} else if (item.cursed || !item.cursedKnown) {
									GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
									hide();
								}  else if (!item.levelKnown && ((Armor)item).STRReq(0) > rose.ghostStrength()){
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength_unknown"));
									hide();
								} else if (((Armor)item).STRReq() > rose.ghostStrength()) {
									GLog.w( Messages.get(WndGhostHero.class, "cant_strength"));
									hide();
								} else {
									if (item.isEquipped(Dungeon.hero)){
										((Armor) item).doUnequip(Dungeon.hero, false, false);
									} else {
										item.detach(Dungeon.hero.belongings.backpack);
									}
									rose.armor = (Armor) item;
									item(rose.armor);
								}
								
							}
						});
					}
				}

                @Override
                protected boolean onLongClick() {
                    if (item() != null && item().name() != null) {
                        GameScene.show(new WndInfoItem(item()));
                        return true;
                    }
                    return false;
                }
            };
            btnArmor.setRect(btnWeapon.right() + hGap, btnWeapon.top(), BTN_SIZE, BTN_SIZE);
            if (rose.armor != null) {
                btnArmor.item(rose.armor);
            } else {
                btnArmor.item(new WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
            }
            add(btnArmor);

            btnRing1 = createRingButton(rose, false);
            if (landscape) {
                btnRing1.setRect(btnArmor.right() + hGap, btnArmor.top(), BTN_SIZE, BTN_SIZE);
            } else {
                btnRing1.setRect(btnWeapon.left(), btnArmor.bottom() + vGap, BTN_SIZE, BTN_SIZE);
            }
            if (rose.ring1 != null) {
                btnRing1.item(rose.ring1);
            } else {
                btnRing1.item(new WndBag.Placeholder(ItemSpriteSheet.RING_HOLDER));
            }
            add(btnRing1);

            btnRing2 = createRingButton(rose, true);
            btnRing2.setRect(btnRing1.right() + hGap, btnRing1.top(), BTN_SIZE, BTN_SIZE);
            if (rose.ring2 != null) {
                btnRing2.item(rose.ring2);
            } else {
                btnRing2.item(new WndBag.Placeholder(ItemSpriteSheet.RING_HOLDER));
                if (!rose.secondRingSlotUnlocked()) {
                    btnRing2.slot().alpha(0.35f);
                }
            }
            add(btnRing2);

            ringInfo = PixelScene.renderTextBlock(6);
            ringInfo.maxWidth(width);
            add(ringInfo);
            refreshRingStats(rose);
        }

        // 戒指调整后刷新介绍文本与窗口高度
        private void refreshRingStats(DriedRose rose) {
            String stats = rose.ghostRingsDesc();
            // 同步图标上的等级显示
            if (rose.ring1 != null) btnRing1.slot().updateText();
            if (rose.ring2 != null) btnRing2.slot().updateText();
            if (stats.isEmpty()) {
                ringInfo.visible = false;
                resize(width, (int) (btnRing2.bottom() + (landscape ? GAP : 8)));
            } else {
                ringInfo.visible = true;
                ringInfo.text(stats);
                ringInfo.maxWidth(width);
                ringInfo.setPos(0, btnRing2.bottom() + 8);
                resize(width, (int) (ringInfo.bottom() + (landscape ? GAP : 8)));
            }
        }

        private ItemButton createRingButton(final DriedRose rose, final boolean second) {
            return new ItemButton() {
                @Override
                protected void onClick() {
                    final Ring equipped = second ? rose.ring2 : rose.ring1;
                    if (equipped != null) {
                        item(new WndBag.Placeholder(ItemSpriteSheet.RING_HOLDER));
                        equipped.deactivate();
                        equipped.socketLevelCap(-1);
                        if (!equipped.doPickUp(Dungeon.hero)) {
                            Dungeon.level.drop(equipped, Dungeon.hero.pos);
                        }
                        if (second) rose.ring2 = null;
                        else        rose.ring1 = null;
                        refreshRingStats(rose);
                    } else if (second && !rose.secondRingSlotUnlocked()) {
                        GLog.w(Messages.get(WndGhostHero.class, "ring_locked"));
                    } else {
                        GameScene.selectItem(new WndBag.ItemSelector() {

                            @Override
                            public String textPrompt() {
                                return Messages.get(WndGhostHero.class, "ring_prompt");
                            }

                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Belongings.Backpack.class;
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Ring && DriedRose.GHOST_RINGS.contains(item.getClass());
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (!(item instanceof Ring) || !DriedRose.GHOST_RINGS.contains(item.getClass())) {
                                    //do nothing, should only happen when window is cancelled
                                } else if (item.unique && !(item instanceof WeddingRing)) {
                                    GLog.w(Messages.get(WndGhostHero.class, "cant_unique"));
                                    hide();
                                } else if (item.cursed || !item.cursedKnown) {
                                    GLog.w(Messages.get(WndGhostHero.class, "cant_cursed"));
                                    hide();
                                } else {
                                    if (item.isEquipped(Dungeon.hero)) {
                                        ((Ring) item).doUnequip(Dungeon.hero, false, false);
                                    } else {
                                        item.detach(Dungeon.hero.belongings.backpack);
                                    }
                                    if (second) rose.ring2 = (Ring) item;
                                    else        rose.ring1 = (Ring) item;
                                    rose.activateGhostRings(rose.ghost);
                                    item((Ring) item);
                                    refreshRingStats(rose);
                                }
                            }
                        });
                    }
                }

                @Override
                protected boolean onLongClick() {
                    if (item() != null && item().name() != null) {
                        GameScene.show(new WndInfoItem(item()));
                        return true;
                    }
                    return false;
                }
            };
        }

    }
}
