/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
* 
 * Ringed Pixel Dungeon
 * Copyright (C) 2025-2025 yantianyv
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
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import java.util.ArrayList;
import java.util.Collections;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Cheat;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class RingOfWealth extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_WEALTH;
        buffClass = Wealth.class;
    }

    private float triesToDrop = Float.MIN_VALUE;
    private int dropsToRare = Integer.MIN_VALUE;

    public String statsInfo() {
        if (isIdentified()) {
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * (Math.pow(1.20f, soloBuffedBonus()) - 1f)));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##",
                                100f * (Math.pow(1.20f, combinedBuffedBonus(Dungeon.hero)) - 1f)));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 20f));
        }
    }

    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100f * (Math.pow(1.2f, level + 1) - 1f)) + "%";
    }

    private static final String TRIES_TO_DROP = "tries_to_drop";
    private static final String DROPS_TO_RARE = "drops_to_rare";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRIES_TO_DROP, triesToDrop);
        bundle.put(DROPS_TO_RARE, dropsToRare);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        triesToDrop = bundle.getFloat(TRIES_TO_DROP);
        dropsToRare = bundle.getInt(DROPS_TO_RARE);
    }

    @Override
    protected RingBuff buff() {
        return new Wealth();
    }

    @Override
    public boolean isUpgradable() {
        return level() > 5;
    }

    public static float dropChanceMultiplier(Char target) {
        return (float) Math.pow(1.20, getBuffedBonus(target, Wealth.class));
    }

    public static ArrayList<Item> tryForBonusDrop(Char target, int tries) {
        int bonus = getBuffedBonus(target, Wealth.class);

        if (bonus <= 0) {
            return null;
        }

        CounterBuff triesToDrop = target.buff(TriesToDropTracker.class);
        if (triesToDrop == null) {
            triesToDrop = Buff.affect(target, TriesToDropTracker.class);
            triesToDrop.countUp(Random.NormalIntRange(0, 20));
        }

        CounterBuff dropsToEquip = target.buff(DropsToEquipTracker.class);
        if (dropsToEquip == null) {
            dropsToEquip = Buff.affect(target, DropsToEquipTracker.class);
            dropsToEquip.countUp(Random.NormalIntRange(5, 10));
        }

        // now handle reward logic
        ArrayList<Item> drops = new ArrayList<>();

        triesToDrop.countDown(tries);
        while (triesToDrop.count() <= 0) {
            if (dropsToEquip.count() <= 0) {
                int equipBonus = 0;
                ArrayList<Integer> bufflevels = new ArrayList<>();
                for (Buff w : target.buffs(Wealth.class)) {
                    bufflevels.add(((Wealth) w).buffedLvl());
                }
                // 按从小到大排列
                Collections.sort(bufflevels);
                float baselevel = bufflevels.get(bufflevels.size() - 1) / 6f;
                // 副戒提供的最大等级与主戒相关，第一枚最高提供主戒的1/6，第二枚2/6，以此类推。
                for (int i = 0; i < bufflevels.size() - 1; i++) {
                    int b = bufflevels.get(i);
                    equipBonus += Math.round(b > i * baselevel ? i * baselevel : b);
                }
                equipBonus += bufflevels.get(bufflevels.size() - 1);

                Item i;
                do {
                    i = genEquipmentDrop(equipBonus - 1);
                } while (Challenges.isItemBlocked(i));
                drops.add(i);
                dropsToEquip.countUp(Random.NormalIntRange(5, 10));
            } else {
                Item i;
                do {
                    i = genConsumableDrop(bonus - 1);
                } while (Challenges.isItemBlocked(i));
                drops.add(i);
                dropsToEquip.countDown(1);
            }
            triesToDrop.countUp(Random.NormalIntRange(0, 20));
        }

        return drops;
    }

    // used for visuals
    // 1/2/3 used for low/mid/high tier consumables
    // 3 used for +0-1 equips, 4 used for +2 or higher equips
    private static int latestDropTier = 0;

    public static void showFlareForBonusDrop(Visual vis) {
        if (vis == null || vis.parent == null) {
            return;
        }
        switch (latestDropTier) {
            default:
                break; // do nothing
            case 1:
                new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
                break;
            case 2:
                new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
                break;
            case 3:
                new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
                break;
            case 4:
                new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
                break;
        }
        latestDropTier = 0;
    }

    public static Item genConsumableDrop(int level) {
        float roll = Random.Float();
        // 60% chance - 4% per level. Starting from +15: 0%
        if (roll < (0.6f - 0.04f * level)) {
            latestDropTier = 1;
            return genLowValueConsumable();
            // 30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
        } else if (roll < (0.9f - 0.02f * level)) {
            latestDropTier = 2;
            return genMidValueConsumable();
            // 10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
        } else {
            latestDropTier = 3;
            return genHighValueConsumable();
        }
    }

    private static Item genLowValueConsumable() {
        switch (Random.Int(4)) {
            case 0:
            default:
                Item i = new Gold().random();
                return i.quantity(i.quantity() / 2);
            case 1:
                return Generator.randomUsingDefaults(Generator.Category.STONE);
            case 2:
                return Generator.randomUsingDefaults(Generator.Category.POTION);
            case 3:
                return Generator.randomUsingDefaults(Generator.Category.SCROLL);
        }
    }

    private static Item genMidValueConsumable() {
        switch (Random.Int(6)) {
            case 0:
            default:
                Item i = genLowValueConsumable();
                return i.quantity(i.quantity() * 2);
            case 1:
                i = Generator.randomUsingDefaults(Generator.Category.POTION);
                if (!(i instanceof ExoticPotion)) {
                    return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
                } else {
                    return Reflection.newInstance(i.getClass());
                }
            case 2:
                i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                if (!(i instanceof ExoticScroll)) {
                    return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
                } else {
                    return Reflection.newInstance(i.getClass());
                }
            case 3:
                return Random.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
            case 4:
                return new Bomb();
            case 5:
                return new Honeypot();

        }
    }

    private static Item genHighValueConsumable() {
        switch (Random.Int(4)) {
            case 0:
            default:
                Item i = genMidValueConsumable();
                if (i instanceof Bomb) {
                    return new Bomb.DoubleBomb();
                } else {
                    return i.quantity(i.quantity() * 2);
                }
            case 1:
                return new StoneOfEnchantment();
            case 2:
                return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration()
                        : new PotionOfExperience();
            case 3:
                return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis()
                        : new ScrollOfTransmutation();
        }
    }

    private static Item genEquipmentDrop(int level) {
        Item result;
        // each upgrade increases depth used for calculating drops by 1
        int floorset = (Dungeon.depth + level) / 5;
        switch (Random.Int(5 + (Dungeon.isCheated(Cheat.BOOTSTRAPPING) ? 1 : 0))) {
            default:
            case 0:
            case 1:
                Weapon w = Generator.randomWeapon(floorset, true);
                if (!w.hasGoodEnchant() && Random.Int(10) < level) {
                    w.enchant();
                } else if (w.hasCurseEnchant()) {
                    w.enchant(null);
                }
                result = w;
                break;
            case 2:
                Armor a = Generator.randomArmor(floorset);
                if (!a.hasGoodGlyph() && Random.Int(10) < level) {
                    a.inscribe();
                } else if (a.hasCurseGlyph()) {
                    a.inscribe(null);
                }
                result = a;
                break;
            case 3:
                result = Generator.randomUsingDefaults(Generator.Category.RING);
                break;
            case 4:
                result = Generator.random(Generator.Category.ARTIFACT);
                break;
            case 5:
                result = new RingOfWealth();
                break;
        }
        // minimum level is 1/2/3/4/5/6 when ring level is 1/3/5/7/9/11
        if (result.isUpgradable()) {
            int minLevel = (level + 1) / 2;
            if (result.level() < minLevel) {
                result.level(minLevel);
            }
        }
        // 为戒指单独写的逻辑
        if (result instanceof Ring) {
            result.level(result.level() + Random.Int((level + 1) / 2));
        }
        result.curse(false);
        result.cursedKnown = true;
        if (result.level() >= 2) {
            latestDropTier = 4;
        } else {
            latestDropTier = 3;
        }
        return result;
    }

    @Override
    public boolean doEquip(final Hero hero) {
        Statistics.wealth = true;
        return super.doEquip(hero);
    }

    // ————————————————戒指效率————————————————
    private static float efficiency = 1.0f;

    @Override
    public float efficiency() {
        return efficiency; // 返回当前类别的共享效率
    }

    @Override
    public void efficiency(float x) {
        x = x > 1 ? 1 : x;
        x = x < 0 ? 0 : x;
        efficiency = x;
    }

    // ————————————————————————————————————————
    public class Wealth extends RingBuff {

    }

    public static class TriesToDropTracker extends CounterBuff {

        {
            revivePersists = true;
        }
    }

    public static class DropsToEquipTracker extends CounterBuff {

        {
            revivePersists = true;
        }
    }
}
