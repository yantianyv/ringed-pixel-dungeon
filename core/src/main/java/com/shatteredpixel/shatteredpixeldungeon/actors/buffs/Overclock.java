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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.PortableTerminal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

// 超频：2 倍命中、3 倍攻速，持续到终端过热为止。
// 命中加成挂接在 Char.hit，攻速加成挂接在 Hero.attackDelay。
public class Overclock extends Buff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float ACC_MULTIPLIER = 2f;   // 2 倍命中
    public static final float DELAY_DIVISOR = 3f;    // 3 倍攻速

    @Override
    public int icon() {
        return BuffIndicator.OVERCLOCK;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    // 每回合检查：终端过热或丢失时结束超频
    @Override
    public boolean act() {
        spend(TICK);
        if (target instanceof Hero) {
            PortableTerminal terminal = ((Hero) target).belongings.getItem(PortableTerminal.class);
            if (terminal == null || terminal.isOverheated()) {
                detach();
            }
        } else {
            detach();
        }
        return true;
    }

    // 供 Char.hit 使用：命中加成
    public static boolean grantsAccuracy(Char ch) {
        return ch.buff(Overclock.class) != null;
    }

    // 供 Hero.attackDelay 使用：攻速加成
    public static boolean grantsSpeed(Char ch) {
        return ch.buff(Overclock.class) != null;
    }
}
