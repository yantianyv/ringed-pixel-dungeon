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
import com.watabou.utils.Bundle;

public class RingOfTimetraveler extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_HASTE;
        buffClass = TimeCompression.class;
    }

    @Override
    public String statsInfo() {
        if (isIdentified()) {
            float solo_rate = (float) Math
                    .abs(Math.pow(0.9f, soloBuffedBonus() * efficiency()) > 0.01f ? Math.pow(0.9f, soloBuffedBonus() * efficiency()) : 0.01f);
            float combined_rate = (float) Math.abs(
                    Math.pow(0.9f, combinedBonus(Dungeon.hero) * efficiency()) > 0.01f ? Math.pow(0.9f, combinedBonus(Dungeon.hero) * efficiency())
                            : 0.01f);
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
        return new TimeCompression();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("efficiency", efficiency);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        efficiency = bundle.getFloat("efficiency");
    }

    public static float timeMultiplier(Char target) {
        float result = (float) Math.pow(0.9, getBuffedBonus(target, TimeCompression.class));
        result = (float) Math.pow(result, efficiency);
        result = Math.abs(result) < 0.01f ? 0.01f : result;
        return result;
    }

    public class TimeCompression extends RingBuff {

        /**
         * 重写act方法，用于执行特定行为
         * @return 返回true表示行为执行成功
         */
        @Override
        public boolean act() {
            efficiency += 0.01;
            efficiency = efficiency > 1 ? 1 : efficiency;
            // 消耗一个时间单位
            spend(TICK);
            // 返回true表示行为执行成功
            return true;
        }
    }
}
