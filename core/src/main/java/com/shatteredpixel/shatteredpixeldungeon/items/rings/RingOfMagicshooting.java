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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfMagicshooting extends Ring {

    {
        // 设置戒指的图标和关联的增益类
        icon = ItemSpriteSheet.Icons.RING_SHARPSHOOT;
        buffClass = Aim.class;
    }

    // 返回戒指的统计信息
    public String statsInfo() {
        if (isIdentified()) {
            String info = Messages.get(this, "stats",
                    soloBuffedBonus() / 2, Messages.decimalFormat("#.##", 100f * (Math.pow(1.1, soloBonus()) - 1f)));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        combinedBuffedBonus(Dungeon.hero) / 2, Messages.decimalFormat("#.##", 100f * (Math.pow(1.1, combinedBonus(Dungeon.hero)) - 1f)));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", 1, Messages.decimalFormat("#.##", 20f));
        }
    }

    // 返回戒指第一个升级属性的字符串表示
    @Override
    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Integer.toString(level + 1);
    }

    // 返回戒指第二个升级属性的字符串表示
    @Override
    public String upgradeStat2(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100f * (Math.pow(1.1, level + 1) - 1f)) + "%";
    }

    // 创建并返回一个 Aim 增益实例
    @Override
    protected RingBuff buff() {
        return new Aim();
    }

    // 获取目标角色的 Aim 增益的单独加成
    public static int levelDamageBonus(Char target) {
        return getBuffedBonus(target, RingOfMagicshooting.Aim.class);
    }

    // 获取目标角色的 Aim 增益的耐久度乘数
    public static float durabilityMultiplier(Char target) {
        return (float) (Math.pow(1.1f, getBonus(target, Aim.class)));
    }

    // 获取目标角色的 Aim 增益的魔力加成乘数
    public static float enchantPowerMultiplier(Char target) {
        return (float) Math.pow(1.1f, getBuffedBonus(target, Aim.class));
    }

    public class Aim extends RingBuff {
    }
}
