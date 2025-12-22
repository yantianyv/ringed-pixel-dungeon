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
            // 使用与实际计算一致的公式
            // 实际计算使用 getBuffedBonus()，它返回所有 buff 的 buffedLvl() 之和
            // 对于单个戒指，就是 buffedLvl()
            int actualBonus = buffedLvl();
            float solo_rate = (float) Math
                    .abs(Math.pow(0.9f, actualBonus * efficiency()) > 0.01f ? Math.pow(0.9f, actualBonus * efficiency()) : 0.01f);
            String info = Messages.get(this,
                    "stats",
                    Messages.decimalFormat("#.##", 100f * solo_rate));
            
            // 组合统计信息，当装备多个同类戒指时显示
            if (isEquipped(Dungeon.hero)) {
                // 计算实际装备的同类戒指数量
                int ringCount = 0;
                if (Dungeon.hero.belongings.ring1() != null && Dungeon.hero.belongings.ring1().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring2() != null && Dungeon.hero.belongings.ring2().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring3() != null && Dungeon.hero.belongings.ring3().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring4() != null && Dungeon.hero.belongings.ring4().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring5() != null && Dungeon.hero.belongings.ring5().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.ring6() != null && Dungeon.hero.belongings.ring6().getClass() == getClass()) ringCount++;
                if (Dungeon.hero.belongings.misc() != null && Dungeon.hero.belongings.misc().getClass() == getClass()) ringCount++;
                
                // 如果有多个戒指，使用 timeMultiplier 直接计算实际效果
                if (ringCount > 1) {
                    float combined_rate = timeMultiplier(Dungeon.hero);
                    info += "\n\n" + Messages.get(this, "combined_stats",
                            Messages.decimalFormat("#.##", 100f * combined_rate));
                }
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
