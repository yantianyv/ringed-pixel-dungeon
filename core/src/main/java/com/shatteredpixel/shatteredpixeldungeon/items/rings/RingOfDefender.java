/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import java.util.HashSet;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfDefender extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_TENACITY;
        buffClass = Defender.class;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            hero.updateHT(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            hero.updateHT(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取装备的属性信息描述
     * 根据装备是否已识别以及是否已装备来显示不同的属性信息
     * @return 返回格式化的属性描述文本
     */
    public String statsInfo() {
        // 判断装备是否已识别
        if (isIdentified()) {
            // 计算并格式化单个装备的属性加成百分比
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * (1f - Math.pow(0.85f, soloBuffedBonus() *  efficiency()))));
            // 如果装备在英雄身上且单个加成不等于组合加成，则添加组合属性信息
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * (1f - Math.pow(0.85f, combinedBuffedBonus(Dungeon.hero) * efficiency()))));
            }
            return info;
        } else {
            // 如果装备未识别，返回典型属性值（固定为15%）
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 15f));
        }
    }

    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100f * (1f - Math.pow(0.85f, level + 1))) + "%";
    }

    @Override
    protected RingBuff buff() {
        return new Defender();
    }

    public static float damageMultiplier(Char t) {
        //(HT - HP)/HT = heroes current % missing health.
        // float effect_rate = Math.max(((float) (t.HT - t.HP) / t.HT), getBuffedBonus(t, Defender.class) * 0.01f);
        // effect_rate = effect_rate < 1f ? effect_rate : 1f;
        return (float) Math.pow(0.85, getBuffedBonus(t, Defender.class) * efficiency);
    }

    public static final HashSet<Class> RESISTS = new HashSet<>();

    static {
        RESISTS.add(Burning.class);
        RESISTS.add(Chill.class);
        RESISTS.add(Frost.class);
        RESISTS.add(Ooze.class);
        RESISTS.add(Paralysis.class);
        RESISTS.add(Poison.class);
        RESISTS.add(Corrosion.class);

        RESISTS.add(ToxicGas.class);
        RESISTS.add(Electricity.class);

        RESISTS.addAll(AntiMagic.RESISTS);
    }

    public static float resist(Char target, Class effect) {
        if (getBuffedBonus(target, Resistance.class) == 0) {
            return 1f;
        }

        for (Class c : RESISTS) {
            if (c.isAssignableFrom(effect)) {
                // float effect_rate = Math.max(((float) (target.HP) / target.HT), getBuffedBonus(target, Defender.class) * 0.01f);
                // effect_rate = effect_rate < 1f ? effect_rate : 1f;
                return (float) Math.pow(0.85, getBuffedBonus(target, Resistance.class) * efficiency);
            }
        }

        return 1f;
    }

    public static float HTAddition(Char target) {
        return (float) Math.pow(getBuffedBonus(target, Defender.class) * 1.5, 1);
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
    public class Defender extends RingBuff {

        @Override
        public boolean act() {
            float target_efficiency = 1f - Dungeon.hero.HP / (float) Dungeon.hero.HT;
            efficiency = efficiency < target_efficiency ? target_efficiency : efficiency * 0.99f + target_efficiency * 0.01f;
            spend(TICK);
            return true;
        }
    }

    public class Resistance extends RingBuff {

    }
}
