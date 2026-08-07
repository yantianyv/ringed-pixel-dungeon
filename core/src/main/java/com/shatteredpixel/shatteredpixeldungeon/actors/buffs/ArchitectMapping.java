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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.RingString;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.specialrings.SpecialRing;

import java.util.ArrayList;

// 架构师映射驱动：每回合同步背包戒指的映射状态。
// 被映射的戒指通过 Ring.mapTo 建立真实的 RingBuff（与装备同一机制），
// 仅等级按稳定映射天赋点数封顶（与玫瑰镶嵌同一套 cap 机制）。
// 本 buff 不显示图标；英雄不再是架构师时解除所有映射并移除自身。
public class ArchitectMapping extends Buff {

    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    @Override
    public boolean act() {
        if (!(target instanceof Hero) || ((Hero) target).subClass != HeroSubClass.ARCHITECT) {
            unmapAll();
            detach();
            return true;
        }
        sync((Hero) target);
        spend(TICK);
        return true;
    }

    private void sync(Hero hero) {
        // 映射戒指的生效等级 = 稳定映射天赋点数（0/1/2/3）
        int cap = hero.pointsInTalent(Talent.STABLE_MAPPING);
        Belongings b = hero.belongings;

        // 收集背包中所有戒指：背包顶层 + 戒指串内
        ArrayList<Ring> present = new ArrayList<>();
        scanContainer(b.backpack, present);
        for (Item item : b.backpack.items.toArray(new Item[0])) {
            if (item instanceof RingString) {
                scanContainer((RingString) item, present);
            }
        }

        // 建立/刷新映射：已鉴定且未诅咒的普通戒指
        for (Ring r : present) {
            if (!(r instanceof SpecialRing) && r.isIdentified() && !r.cursed) {
                if (!r.isMapped() || r.mappedLevelCap() != cap) {
                    r.mapTo(hero, cap);
                }
            } else if (r.isMapped()) {
                r.unmap();
            }
        }

        // 清理已离开背包的映射戒指（被丢弃/出售/嬗变/移出戒指串等）
        for (Ring.RingBuff buff : hero.buffs(Ring.RingBuff.class).toArray(new Ring.RingBuff[0])) {
            Ring r = buff.getRing();
            if (r.isMapped() && !present.contains(r)) {
                r.unmap();
            }
        }
    }

    private void scanContainer(Bag container, ArrayList<Ring> out) {
        for (Item item : container.items.toArray(new Item[0])) {
            if (item instanceof Ring) {
                out.add((Ring) item);
            }
        }
    }

    private void unmapAll() {
        if (target == null) return;
        for (Ring.RingBuff buff : target.buffs(Ring.RingBuff.class).toArray(new Ring.RingBuff[0])) {
            Ring r = buff.getRing();
            if (r.isMapped()) {
                r.unmap();
            }
        }
    }
}
