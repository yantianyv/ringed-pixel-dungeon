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

public class RingOfAgility extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_ACCURACY;
        buffClass = Agility.class;
    }

    public String statsInfo() {
        if (isIdentified()) {
            Float solo = 0f;
            Float combined = 0f;
            if (soloBuffedBonus() > 0) {
                solo = (float) (1 - Math.pow(0.95f, soloBuffedBonus()));
            } else {
                solo = (float) (Math.pow(0.95f, -soloBuffedBonus()) - 1);
            }
            if (combinedBuffedBonus(Dungeon.hero) > 0) {
                combined = (float) (1 - Math.pow(0.95f, combinedBuffedBonus(Dungeon.hero)));
            } else {
                combined = (float) (Math.pow(0.95f, -combinedBuffedBonus(Dungeon.hero)) - 1);
            }
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * solo));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * combined));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 5f));
        }
    }

    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100f * (1 - Math.pow(0.95f, level))) + "%";
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
    @Override
    protected RingBuff buff() {
        return new Agility();
    }

    public static float agilityChance(Char target) {
        if (getBuffedBonus(target, Agility.class) > 0) {
            return (float) (1 - Math.pow(0.95f, getBuffedBonus(target, Agility.class))) * efficiency;
        } else {
            return (float) (Math.pow(0.95f, -getBuffedBonus(target, Agility.class)) - 1);
        }
    }

    public class Agility extends RingBuff {
    }
}
