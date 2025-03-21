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

public class RingOfTimetraveler extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_HASTE;
        buffClass = TimeCompression.class;
    }

    @Override
    public String statsInfo() {
        if (isIdentified()) {
            float solo_rate = (float) Math.abs(Math.pow(0.9f, soloBuffedBonus()) > 0.01f ? Math.pow(0.9f, soloBuffedBonus()) : 0.01f);
            float combined_rate = (float) Math.abs(Math.pow(0.9f, combinedBonus(Dungeon.hero)) > 0.01f ? Math.pow(0.9f, combinedBonus(Dungeon.hero)) : 0.01f);
            String info = Messages.get(this,
                    "stats",
                    Messages.decimalFormat("#.##", 100f * solo_rate));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)) {
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * combined_rate));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 17.5f));
        }
    }

    @Override
    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100f * (Math.pow(0.9f, level) - 1f)) + "%";
    }

    @Override
    protected RingBuff buff() {
        return new TimeCompression();
    }

    public static float timeMultiplier(Char target) {
        float result = (float) Math.pow(0.9, getBuffedBonus(target, TimeCompression.class));
        result = Math.abs(result) < 0.01f ? 0.01f : result;
        return result;
    }

    public class TimeCompression extends RingBuff {
    }
}
